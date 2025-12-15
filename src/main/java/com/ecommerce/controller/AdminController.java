package com.ecommerce.controller;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.ecommerce.dao.*;
import com.ecommerce.model.*;
import com.ecommerce.util.FormatterUtils;
import com.ecommerce.util.SessionManager; // Import Session

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AdminController implements Initializable {

    // --- DASHBOARD (SẢNH CHÍNH) ---
    @FXML private Label lblDashRevenue, lblDashOrders, lblDashUsers, lblDashProducts;

    // --- Tab Sản phẩm ---
    @FXML private TableView<Product> tableProduct;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Integer> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colImage;
    @FXML private TextField txtName, txtPrice, txtCategory, txtGender, txtImage, txtQuantity, txtDescription;
    @FXML private TextField txtSearchProduct; 
    @FXML private ComboBox<String> cbFilterCategory; // <-- MỚI: Lọc danh mục
    @FXML private ImageView imgPreview; @FXML private Label lblImageName;

    // --- Tab Đơn hàng ---
    @FXML private TableView<Order> tableOrder;
    @FXML private TableColumn<Order, Integer> colOrderId, colUserId, colTotal;
    @FXML private TableColumn<Order, String> colStatus, colDate, colPayment;
    @FXML private TextField txtSearchOrder;
    
    // --- Tab Người dùng ---
    @FXML private TableView<User> tableUser;
    @FXML private TableColumn<User, Integer> colUserTableId;
    @FXML private TableColumn<User, String> colUsername, colEmail, colPhone, colUserStatus;
    @FXML private TableColumn<User, Integer> colTotalOrders;
    
    // --- Tab Đánh giá ---
    @FXML private TableView<Review> tableReview;
    @FXML private TableColumn<Review, Integer> colReviewId;
    @FXML private TableColumn<Review, String> colReviewUser, colReviewProduct, colReviewComment, colReviewDate;
    @FXML private TableColumn<Review, Integer> colReviewRating;
    
    // --- Tab Voucher ---
    @FXML private TableView<Voucher> tableVoucher;
    @FXML private TableColumn<Voucher, String> colVoucherCode;
    @FXML private TableColumn<Voucher, Integer> colVoucherPercent, colVoucherQty;
    @FXML private TableColumn<Voucher, Date> colVoucherDate;
    @FXML private TextField txtVoucherCode, txtVoucherPercent, txtVoucherQty;
    @FXML private DatePicker dpVoucherDate;
    
    // --- Tab Thống kê ---
    @FXML private BarChart<String, Number> revenueChart;
    
    // --- Toggle Dark Mode ---
    @FXML private ToggleButton btnTheme;

    // --- DAOs ---
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private OrderDetailDAO orderDetailDAO;
    private ReviewDAO reviewDAO;
    private VoucherDAO voucherDAO;
    
    // --- Dữ liệu ---
    private ObservableList<Product> productList;
    private ObservableList<Order> orderList;
    private ObservableList<User> userList;
    private ObservableList<Review> reviewList;
    private ObservableList<Voucher> voucherList;
    private File selectedImageFile; 
    private boolean isDarkMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Khởi tạo DAO
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        userDAO = new UserDAO();
        orderDetailDAO = new OrderDetailDAO();
        reviewDAO = new ReviewDAO();
        voucherDAO = new VoucherDAO();
        
        // 2. Setup Giao diện
        setupColumns();
        setupTableFeatures();
        
        // 3. Load Dữ liệu
        loadAllData();
        
        // 4. Setup Lọc & Tìm kiếm
        setupProductFilter();
        setupSearchOrder();
        
        // 5. Listener chọn bảng
        tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) fillForm(newSelection);
        });
        
        // 6. Init ComboBox Danh mục
        if (cbFilterCategory != null) {
            cbFilterCategory.getItems().addAll("Tất cả", "Áo", "Quần", "Giày");
            cbFilterCategory.setValue("Tất cả");
        }
    }
    
    private void loadAllData() {
        loadProducts();
        loadOrders();
        loadUsers();
        loadReviews();
        loadVouchers();
        loadChart();
        updateDashboard(); // Cập nhật số liệu Sảnh chính
    }
    
    // ===== 1. DASHBOARD (SẢNH CHÍNH) =====
    private void updateDashboard() {
        // Đếm số lượng từ các list đã load
        // 1. Sản phẩm
        if (productList != null && lblDashProducts != null) {
            lblDashProducts.setText(String.valueOf(productList.size()));
        }
        
        // 2. Đơn hàng
        if (orderList != null && lblDashOrders != null) {
            lblDashOrders.setText(String.valueOf(orderList.size()));
        }
        
        // 3. Khách hàng (Đây là chỗ đang bị lỗi của bạn)
        if (userList != null && lblDashUsers != null) {
            lblDashUsers.setText(String.valueOf(userList.size()));
        }
        
        // Tính tổng doanh thu
        long totalRevenue = 0;
        if (orderList != null) {
            for (Order o : orderList) {
                if ("Đã giao hàng".equals(o.getStatus())) {
                    totalRevenue += o.getTotalMoney();
                }
            }
        }
        lblDashRevenue.setText(FormatterUtils.formatPrice((int)totalRevenue) );
    }
    
    // ===== 2. DARK MODE & PROFILE =====
    @FXML
    private void handleToggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            btnTheme.setText("☀ Chế độ Sáng");
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            btnTheme.setText("🌙 Chế độ Tối");
        }
    }
    
    @FXML
    private void handleAdminProfile() {
        User admin = SessionManager.getCurrentUser();
        if (admin == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin đăng nhập.\nHãy đăng xuất và đăng nhập lại.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hồ sơ Admin");
        alert.setHeaderText("Thông tin tài khoản");
        
        String info = "Username: " + admin.getUsername() + "\n" +
                      "Email: " + admin.getEmail() + "\n" +
                      "Vai trò: Quản trị viên (Super Admin)";
        
        alert.setContentText(info);
        
        // Thêm nút đổi mật khẩu
        ButtonType btnChangePass = new ButtonType("Đổi mật khẩu");
        alert.getButtonTypes().add(btnChangePass);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnChangePass) {
            // Chuyển sang màn hình đổi pass (hoặc hiện dialog đổi pass)
            // Ở đây mình tái sử dụng ChangePasswordController nếu muốn, hoặc thông báo
            Alert info2 = new Alert(Alert.AlertType.INFORMATION, "Vui lòng dùng chức năng 'Đổi mật khẩu' ở trang Cá nhân.");
            info2.show();
        }
    }

    // ===== 3. FILTER SẢN PHẨM (MỚI) =====
    private void setupProductFilter() {
        FilteredList<Product> filteredData = new FilteredList<>(productList, p -> true);

        // Listener cho ô tìm kiếm Text
        txtSearchProduct.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilteredList(filteredData);
        });
        
        // Listener cho ComboBox Danh mục
        cbFilterCategory.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateFilteredList(filteredData);
        });

        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableProduct.comparatorProperty());
        tableProduct.setItems(sortedData);
    }
    
    private void updateFilteredList(FilteredList<Product> filteredData) {
        String searchText = txtSearchProduct.getText() == null ? "" : txtSearchProduct.getText().toLowerCase();
        String selectedCategory = cbFilterCategory.getValue();
        
        filteredData.setPredicate(product -> {
            // 1. Check Danh mục
            boolean matchCategory = selectedCategory == null || selectedCategory.equals("Tất cả") || 
                                    product.getCategory().equalsIgnoreCase(selectedCategory);
            
            // 2. Check Từ khóa
            boolean matchSearch = searchText.isEmpty() || 
                                  product.getName().toLowerCase().contains(searchText) ||
                                  String.valueOf(product.getProductId()).contains(searchText);
            
            return matchCategory && matchSearch;
        });
    }

    // ===== GIỮ NGUYÊN CÁC HÀM CŨ (CRUD, SETUP COLUMNS...) =====
    
    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));

        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalMoney"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        colUserTableId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colTotalOrders.setCellValueFactory(new PropertyValueFactory<>("totalOrders"));
        colUserStatus.setCellValueFactory(new PropertyValueFactory<>("statusStr"));

        colReviewId.setCellValueFactory(new PropertyValueFactory<>("reviewId"));
        colReviewUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colReviewProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colReviewRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colReviewComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        colReviewDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        colVoucherCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colVoucherPercent.setCellValueFactory(new PropertyValueFactory<>("discountPercent"));
        colVoucherQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colVoucherDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
    }

    private void setupTableFeatures() {
        colStatus.setCellFactory(column -> new TableCell<Order, String>() { @Override protected void updateItem(String item, boolean empty) { super.updateItem(item, empty); if (empty || item == null) { setText(null); setStyle(""); } else { setText(item); if (item.contains("Đã giao")) setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); else if (item.contains("hủy")) setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); else setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;"); } } });
        colQuantity.setCellFactory(column -> new TableCell<Product, Integer>() { @Override protected void updateItem(Integer qty, boolean empty) { super.updateItem(qty, empty); if (empty || qty == null) { setText(null); setStyle(""); } else { setText(String.valueOf(qty)); if (qty == 0) setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-background-color: #fadbd8;"); else if (qty < 10) setStyle("-fx-text-fill: #d35400; -fx-font-weight: bold;"); else setStyle(""); } } });
        colUserStatus.setCellFactory(column -> new TableCell<User, String>() { @Override protected void updateItem(String item, boolean empty) { super.updateItem(item, empty); if (empty || item == null) { setText(null); setStyle(""); } else { setText(item); if (item.equals("Đã khóa")) setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); else setStyle("-fx-text-fill: green;"); } } });
        colReviewRating.setCellFactory(column -> new TableCell<Review, Integer>() { @Override protected void updateItem(Integer item, boolean empty) { super.updateItem(item, empty); if (empty || item == null) { setText(null); setStyle(""); } else { setText(item + " ★"); if(item >= 4) setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); else if(item <= 2) setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); else setStyle("-fx-text-fill: orange;"); } } });
        tableOrder.setOnMouseClicked(event -> { if (event.getClickCount() == 2) { Order selected = tableOrder.getSelectionModel().getSelectedItem(); if (selected != null) showOrderDetailPopup(selected); } });
    }

    private void setupSearchOrder() {
        FilteredList<Order> filteredData = new FilteredList<>(orderList, p -> true);
        txtSearchOrder.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(order -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return String.valueOf(order.getOrderId()).contains(lower) || String.valueOf(order.getUserId()).contains(lower) || order.getStatus().toLowerCase().contains(lower);
            });
        });
        SortedList<Order> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableOrder.comparatorProperty());
        tableOrder.setItems(sortedData);
    }
    
    // --- LOADERS ---
    private void loadProducts() { productList = FXCollections.observableArrayList(productDAO.getAll()); if(txtSearchProduct!=null && !txtSearchProduct.getText().isEmpty()) updateFilteredList(new FilteredList<>(productList)); else tableProduct.setItems(productList); }
    private void loadOrders() { orderList = FXCollections.observableArrayList(orderDAO.getAllOrders()); if(txtSearchOrder!=null && !txtSearchOrder.getText().isEmpty()) setupSearchOrder(); else tableOrder.setItems(orderList); }
    private void loadUsers() { userList = FXCollections.observableArrayList(userDAO.getAllUsersWithStats()); tableUser.setItems(userList); }
    private void loadReviews() { if(tableReview!=null) { reviewList = FXCollections.observableArrayList(reviewDAO.getAllReviews()); tableReview.setItems(reviewList); }}
    private void loadVouchers() { if(tableVoucher!=null) { voucherList = FXCollections.observableArrayList(voucherDAO.getAllVouchers()); tableVoucher.setItems(voucherList); }}
    private void loadChart() { revenueChart.getData().clear(); Map<String, Integer> d = orderDAO.getRevenueByDate(); XYChart.Series<String, Number> s = new XYChart.Series<>(); s.setName("Doanh thu"); for(Map.Entry<String,Integer> e : d.entrySet()) s.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())); revenueChart.getData().add(s); }
    
    // --- HANDLERS ---
    @FXML private void handleRefreshOrders() { loadOrders(); loadChart(); updateDashboard(); }
    @FXML private void handleApproveOrder() { Order s = tableOrder.getSelectionModel().getSelectedItem(); if(s!=null && orderDAO.updateStatus(s.getOrderId(), "Đã giao hàng")) { showAlert("OK", "Đã duyệt!"); loadOrders(); loadChart(); updateDashboard(); } }
    @FXML private void handleCancelOrder() { Order s = tableOrder.getSelectionModel().getSelectedItem(); if(s!=null && orderDAO.updateStatus(s.getOrderId(), "Đã hủy (Admin)")) { showAlert("OK", "Đã hủy!"); loadOrders(); updateDashboard(); } }
    
    @FXML private void handleRefreshUsers() { loadUsers(); updateDashboard(); }
    @FXML private void handleLockUser() { User s = tableUser.getSelectionModel().getSelectedItem(); if(s!=null && userDAO.setLockStatus(s.getUserId(), !s.isLocked())) { showAlert("OK", "Đã cập nhật!"); loadUsers(); } }
    @FXML private void handleDeleteUser() { User s = tableUser.getSelectionModel().getSelectedItem(); if(s!=null && new Alert(Alert.AlertType.CONFIRMATION,"Xóa?",ButtonType.YES,ButtonType.NO).showAndWait().get()==ButtonType.YES && userDAO.deleteUser(s.getUserId())) { showAlert("OK","Đã xóa"); loadUsers(); updateDashboard(); } }
    
    @FXML private void handleRefreshReviews() { loadReviews(); }
    @FXML private void handleDeleteReview() { Review s = tableReview.getSelectionModel().getSelectedItem(); if(s!=null && new Alert(Alert.AlertType.CONFIRMATION,"Xóa?",ButtonType.YES,ButtonType.NO).showAndWait().get()==ButtonType.YES && reviewDAO.deleteReview(s.getReviewId())) { showAlert("OK","Đã xóa"); loadReviews(); } }
    
    @FXML private void handleAddVoucher() { try { Voucher v = new Voucher(txtVoucherCode.getText().toUpperCase(), Integer.parseInt(txtVoucherPercent.getText()), Integer.parseInt(txtVoucherQty.getText()), Date.valueOf(dpVoucherDate.getValue())); if(voucherDAO.insert(v)) { showAlert("OK","Thêm xong"); loadVouchers(); txtVoucherCode.clear(); } } catch(Exception e){} }
    @FXML private void handleDeleteVoucher() { Voucher s = tableVoucher.getSelectionModel().getSelectedItem(); if(s!=null && voucherDAO.delete(s.getCode())) { showAlert("OK","Đã xóa"); loadVouchers(); } }
    
    @FXML private void handleAddProduct() { try { Product p = new Product(); p.setName(txtName.getText()); p.setPrice(Integer.parseInt(txtPrice.getText())); p.setCategory(txtCategory.getText()); p.setGender(txtGender.getText()); String img = txtImage.getText().trim(); if(img.isEmpty()) img="default.png"; p.setImage(img); p.setQuantity(Integer.parseInt(txtQuantity.getText())); p.setDescription(txtDescription.getText()); if(selectedImageFile!=null) saveImageToProject(selectedImageFile, img); if(productDAO.insert(p)) { showAlert("OK", "Đã thêm!"); loadProducts(); updateDashboard(); handleClearInput(); } } catch(Exception e){ showAlert("Lỗi", "Dữ liệu sai."); } }
    @FXML private void handleUpdateProduct() { Product s = tableProduct.getSelectionModel().getSelectedItem(); if(s==null) return; try { s.setName(txtName.getText()); s.setPrice(Integer.parseInt(txtPrice.getText())); s.setCategory(txtCategory.getText()); s.setGender(txtGender.getText()); String img = txtImage.getText().trim(); s.setImage(img); s.setQuantity(Integer.parseInt(txtQuantity.getText())); s.setDescription(txtDescription.getText()); if(selectedImageFile!=null) saveImageToProject(selectedImageFile, img); if(productDAO.update(s)) { showAlert("OK", "Cập nhật xong!"); loadProducts(); } } catch(Exception e){ showAlert("Lỗi", "Dữ liệu sai."); } }
    @FXML private void handleDeleteProduct() { Product s = tableProduct.getSelectionModel().getSelectedItem(); if(s!=null && productDAO.delete(s.getProductId())) { showAlert("OK", "Đã xóa!"); loadProducts(); updateDashboard(); handleClearInput(); } }
    @FXML private void handleClearInput() { txtName.clear(); txtPrice.clear(); txtCategory.clear(); txtGender.clear(); txtImage.clear(); txtQuantity.clear(); txtDescription.clear(); imgPreview.setImage(null); selectedImageFile=null; tableProduct.getSelectionModel().clearSelection(); }
    
    // --- HELPERS ---
    private void fillForm(Product p) { txtName.setText(p.getName()); txtPrice.setText(String.valueOf(p.getPrice())); txtCategory.setText(p.getCategory()); txtGender.setText(p.getGender()); txtQuantity.setText(String.valueOf(p.getQuantity())); txtDescription.setText(p.getDescription()); if (txtImage != null) txtImage.setText(p.getImage()); try { imgPreview.setImage(new Image(getClass().getResourceAsStream("/images/" + p.getImage()))); } catch (Exception e) { imgPreview.setImage(null); } selectedImageFile = null; }
    @FXML private void handleChooseImage() { FileChooser fc = new FileChooser(); fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png","*.jpg")); File f = fc.showOpenDialog(txtName.getScene().getWindow()); if(f!=null){ selectedImageFile=f; txtImage.setText(f.getName()); try{imgPreview.setImage(new Image(f.toURI().toString()));}catch(Exception e){} } }
    private boolean saveImageToProject(File f, String n) { try { Files.copy(f.toPath(), new File(System.getProperty("user.dir") + "/src/main/resources/images", n).toPath(), StandardCopyOption.REPLACE_EXISTING); Files.copy(f.toPath(), new File(System.getProperty("user.dir") + "/target/classes/images", n).toPath(), StandardCopyOption.REPLACE_EXISTING); return true; } catch (IOException e) { return false; } }
    @FXML private void handleLoadChart() { loadChart(); }
    private void showOrderDetailPopup(Order order) { Dialog<Void> d = new Dialog<>(); d.setTitle("Chi tiết #" + order.getOrderId()); d.getDialogPane().getButtonTypes().add(new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE)); VBox c = new VBox(10); c.setPadding(new Insets(10)); for(CartItem i : orderDetailDAO.getItemsByOrderId(order.getOrderId())) c.getChildren().add(new Label("• " + i.getProductName() + " - x" + i.getQuantity())); d.getDialogPane().setContent(new ScrollPane(c)); d.showAndWait(); }
    @FXML private void handleLogout() { try { ((Stage) txtName.getScene().getWindow()).setScene(new Scene(new FXMLLoader(getClass().getResource("/com/ecommerce/view/login.fxml")).load())); } catch (Exception e) {} }
    private void showAlert(String t, String c) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(t); a.setContentText(c); a.showAndWait(); }
}