package com.ecommerce.controller;

import com.ecommerce.dao.*;
import com.ecommerce.model.*;
import com.ecommerce.util.FormatterUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.Map;
import java.util.ResourceBundle;

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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AdminController implements Initializable {

    // --- FXML BIẾN ---
    @FXML private TableView<Product> tableProduct;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Integer> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colImage;
    @FXML private TextField txtName, txtPrice, txtCategory, txtGender, txtImage, txtQuantity, txtDescription, txtSearchProduct;
    @FXML private ImageView imgPreview; @FXML private Label lblImageName;

    @FXML private TableView<Order> tableOrder;
    @FXML private TableColumn<Order, Integer> colOrderId, colUserId, colTotal;
    @FXML private TableColumn<Order, String> colStatus, colDate, colPayment;
    @FXML private TextField txtSearchOrder;
    
    @FXML private TableView<User> tableUser;
    @FXML private TableColumn<User, Integer> colUserTableId;
    @FXML private TableColumn<User, String> colUsername, colEmail, colPhone, colUserStatus;
    @FXML private TableColumn<User, Integer> colTotalOrders;
    
    @FXML private TableView<Review> tableReview;
    @FXML private TableColumn<Review, Integer> colReviewId;
    @FXML private TableColumn<Review, String> colReviewUser, colReviewProduct, colReviewComment, colReviewDate;
    @FXML private TableColumn<Review, Integer> colReviewRating;
    
    @FXML private TableView<Voucher> tableVoucher;
    @FXML private TableColumn<Voucher, String> colVoucherCode;
    @FXML private TableColumn<Voucher, Integer> colVoucherPercent, colVoucherQty;
    @FXML private TableColumn<Voucher, Date> colVoucherDate;
    @FXML private TextField txtVoucherCode, txtVoucherPercent, txtVoucherQty;
    @FXML private DatePicker dpVoucherDate;
    
    @FXML private BarChart<String, Number> revenueChart;

    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private OrderDetailDAO orderDetailDAO;
    private ReviewDAO reviewDAO;
    private VoucherDAO voucherDAO;
    
    private ObservableList<Product> productList;
    private ObservableList<Order> orderList;
    private ObservableList<User> userList;
    private ObservableList<Review> reviewList;
    private ObservableList<Voucher> voucherList;
    private File selectedImageFile; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        userDAO = new UserDAO();
        orderDetailDAO = new OrderDetailDAO();
        reviewDAO = new ReviewDAO();
        voucherDAO = new VoucherDAO();
        
        setupColumns();
        setupTableFeatures();
        
        loadProducts();
        loadOrders();
        loadUsers();
        loadReviews();
        loadVouchers();
        loadChart(); 
        
        tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) fillForm(newSelection);
        });
        
        setupSearchProduct();
        setupSearchOrder();
    }
    
    // --- SETUP CỘT & TABLE ---
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

    // --- SEARCH ---
    private void setupSearchProduct() { FilteredList<Product> f = new FilteredList<>(productList, p -> true); txtSearchProduct.textProperty().addListener((o, old, val) -> { f.setPredicate(p -> { if (val == null || val.isEmpty()) return true; String lower = val.toLowerCase(); return p.getName().toLowerCase().contains(lower) || p.getCategory().toLowerCase().contains(lower) || String.valueOf(p.getProductId()).contains(lower); }); }); SortedList<Product> s = new SortedList<>(f); s.comparatorProperty().bind(tableProduct.comparatorProperty()); tableProduct.setItems(s); }
    private void setupSearchOrder() { FilteredList<Order> f = new FilteredList<>(orderList, p -> true); txtSearchOrder.textProperty().addListener((o, old, val) -> { f.setPredicate(or -> { if (val == null || val.isEmpty()) return true; String lower = val.toLowerCase(); return String.valueOf(or.getOrderId()).contains(lower) || String.valueOf(or.getUserId()).contains(lower) || or.getStatus().toLowerCase().contains(lower); }); }); SortedList<Order> s = new SortedList<>(f); s.comparatorProperty().bind(tableOrder.comparatorProperty()); tableOrder.setItems(s); }

    // --- USER MANAGEMENT (ĐÂY LÀ PHẦN BẠN BỊ THIẾU TRƯỚC ĐÓ) ---
    private void loadUsers() { userList = FXCollections.observableArrayList(userDAO.getAllUsersWithStats()); tableUser.setItems(userList); }
    @FXML private void handleRefreshUsers() { loadUsers(); } // <-- Hàm này trước đây bị thiếu
    @FXML private void handleLockUser() { User s = tableUser.getSelectionModel().getSelectedItem(); if(s!=null && userDAO.setLockStatus(s.getUserId(), !s.isLocked())) { showAlert("OK", "Đã cập nhật!"); loadUsers(); } else showAlert("Lỗi", "Chọn user!"); }
    @FXML private void handleDeleteUser() { User s = tableUser.getSelectionModel().getSelectedItem(); if(s!=null && new Alert(Alert.AlertType.CONFIRMATION,"Xóa?",ButtonType.YES,ButtonType.NO).showAndWait().get()==ButtonType.YES && userDAO.deleteUser(s.getUserId())) { showAlert("OK","Đã xóa"); loadUsers(); } }

    // --- REVIEW MANAGEMENT ---
    private void loadReviews() { if (tableReview != null) { reviewList = FXCollections.observableArrayList(reviewDAO.getAllReviews()); tableReview.setItems(reviewList); } }
    @FXML private void handleRefreshReviews() { loadReviews(); }
    @FXML private void handleDeleteReview() { Review s = tableReview.getSelectionModel().getSelectedItem(); if(s!=null && new Alert(Alert.AlertType.CONFIRMATION,"Xóa?",ButtonType.YES,ButtonType.NO).showAndWait().get()==ButtonType.YES && reviewDAO.deleteReview(s.getReviewId())) { showAlert("OK","Đã xóa"); loadReviews(); } }

    // --- VOUCHER MANAGEMENT ---
    private void loadVouchers() { if(tableVoucher!=null) { voucherList = FXCollections.observableArrayList(voucherDAO.getAllVouchers()); tableVoucher.setItems(voucherList); } }
    @FXML private void handleAddVoucher() { try { Voucher v = new Voucher(txtVoucherCode.getText().toUpperCase(), Integer.parseInt(txtVoucherPercent.getText()), Integer.parseInt(txtVoucherQty.getText()), Date.valueOf(dpVoucherDate.getValue())); if(voucherDAO.insert(v)) { showAlert("OK","Thêm xong"); loadVouchers(); txtVoucherCode.clear(); } } catch(Exception e){} }
    @FXML private void handleDeleteVoucher() { Voucher s = tableVoucher.getSelectionModel().getSelectedItem(); if(s!=null && voucherDAO.delete(s.getCode())) { showAlert("OK","Đã xóa"); loadVouchers(); } }

    // --- PRODUCT & ORDER (BASIC) ---
    private void loadProducts() { productList = FXCollections.observableArrayList(productDAO.getAll()); if(txtSearchProduct!=null && !txtSearchProduct.getText().isEmpty()) setupSearchProduct(); else tableProduct.setItems(productList); }
    private void loadOrders() { orderList = FXCollections.observableArrayList(orderDAO.getAllOrders()); if(txtSearchOrder!=null && !txtSearchOrder.getText().isEmpty()) setupSearchOrder(); else tableOrder.setItems(orderList); }
    @FXML private void handleRefreshOrders() { loadOrders(); loadChart(); }
    @FXML private void handleApproveOrder() { Order s = tableOrder.getSelectionModel().getSelectedItem(); if(s!=null && orderDAO.updateStatus(s.getOrderId(), "Đã giao hàng")) { showAlert("OK", "Đã duyệt!"); loadOrders(); loadChart(); } }
    @FXML private void handleCancelOrder() { Order s = tableOrder.getSelectionModel().getSelectedItem(); if(s!=null && orderDAO.updateStatus(s.getOrderId(), "Đã hủy (Admin)")) { showAlert("OK", "Đã hủy!"); loadOrders(); } }
    
    // --- PRODUCT CRUD ---
    private void fillForm(Product p) { txtName.setText(p.getName()); txtPrice.setText(String.valueOf(p.getPrice())); txtCategory.setText(p.getCategory()); txtGender.setText(p.getGender()); txtQuantity.setText(String.valueOf(p.getQuantity())); txtDescription.setText(p.getDescription()); if (txtImage != null) txtImage.setText(p.getImage()); try { imgPreview.setImage(new Image(getClass().getResourceAsStream("/images/" + p.getImage()))); } catch (Exception e) { imgPreview.setImage(null); } selectedImageFile = null; }
    @FXML private void handleChooseImage() { FileChooser fc = new FileChooser(); fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png","*.jpg")); File f = fc.showOpenDialog(txtName.getScene().getWindow()); if(f!=null){ selectedImageFile=f; txtImage.setText(f.getName()); try{imgPreview.setImage(new Image(f.toURI().toString()));}catch(Exception e){} } }
    private boolean saveImageToProject(File f, String n) { try { Files.copy(f.toPath(), new File(System.getProperty("user.dir") + "/src/main/resources/images", n).toPath(), StandardCopyOption.REPLACE_EXISTING); Files.copy(f.toPath(), new File(System.getProperty("user.dir") + "/target/classes/images", n).toPath(), StandardCopyOption.REPLACE_EXISTING); return true; } catch (IOException e) { return false; } }
    @FXML private void handleAddProduct() { try { Product p = new Product(); p.setName(txtName.getText()); p.setPrice(Integer.parseInt(txtPrice.getText())); p.setCategory(txtCategory.getText()); p.setGender(txtGender.getText()); String img = txtImage.getText().trim(); if(img.isEmpty()) img="default.png"; p.setImage(img); p.setQuantity(Integer.parseInt(txtQuantity.getText())); p.setDescription(txtDescription.getText()); if(selectedImageFile!=null) saveImageToProject(selectedImageFile, img); if(productDAO.insert(p)) { showAlert("OK", "Đã thêm!"); loadProducts(); handleClearInput(); } } catch(Exception e){ showAlert("Lỗi", "Dữ liệu sai."); } }
    @FXML private void handleUpdateProduct() { Product s = tableProduct.getSelectionModel().getSelectedItem(); if(s==null) return; try { s.setName(txtName.getText()); s.setPrice(Integer.parseInt(txtPrice.getText())); s.setCategory(txtCategory.getText()); s.setGender(txtGender.getText()); String img = txtImage.getText().trim(); s.setImage(img); s.setQuantity(Integer.parseInt(txtQuantity.getText())); s.setDescription(txtDescription.getText()); if(selectedImageFile!=null) saveImageToProject(selectedImageFile, img); if(productDAO.update(s)) { showAlert("OK", "Cập nhật xong!"); loadProducts(); } } catch(Exception e){ showAlert("Lỗi", "Dữ liệu sai."); } }
    @FXML private void handleDeleteProduct() { Product s = tableProduct.getSelectionModel().getSelectedItem(); if(s!=null && productDAO.delete(s.getProductId())) { showAlert("OK", "Đã xóa!"); loadProducts(); handleClearInput(); } }
    @FXML private void handleClearInput() { txtName.clear(); txtPrice.clear(); txtCategory.clear(); txtGender.clear(); txtImage.clear(); txtQuantity.clear(); txtDescription.clear(); imgPreview.setImage(null); selectedImageFile=null; tableProduct.getSelectionModel().clearSelection(); }

    // --- CHART & OTHERS ---
    private void loadChart() { revenueChart.getData().clear(); Map<String, Integer> d = orderDAO.getRevenueByDate(); XYChart.Series<String, Number> s = new XYChart.Series<>(); s.setName("Doanh thu"); for(Map.Entry<String,Integer> e : d.entrySet()) s.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())); revenueChart.getData().add(s); }
    @FXML private void handleLoadChart() { loadChart(); }
    private void showOrderDetailPopup(Order order) { Dialog<Void> d = new Dialog<>(); d.setTitle("Chi tiết #" + order.getOrderId()); d.getDialogPane().getButtonTypes().add(new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE)); VBox c = new VBox(10); c.setPadding(new Insets(10)); for(CartItem i : orderDetailDAO.getItemsByOrderId(order.getOrderId())) c.getChildren().add(new Label("• " + i.getProductName() + " - x" + i.getQuantity())); d.getDialogPane().setContent(new ScrollPane(c)); d.showAndWait(); }
    @FXML private void handleLogout() { try { ((Stage) txtName.getScene().getWindow()).setScene(new Scene(new FXMLLoader(getClass().getResource("/com/ecommerce/view/login.fxml")).load())); } catch (Exception e) {} }
    private void showAlert(String t, String c) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(t); a.setContentText(c); a.showAndWait(); }
}