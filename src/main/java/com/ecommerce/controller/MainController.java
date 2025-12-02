package com.ecommerce.controller;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.FavoriteDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;
import com.ecommerce.util.FormatterUtils;
import com.ecommerce.util.SessionManager;
import com.ecommerce.util.Toast; // Import Toast
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; 
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainController implements Initializable {

    // --- FXML Components ---
    @FXML private TilePane productGrid;
    @FXML private ToggleGroup tabGroup; 
    @FXML private VBox navHomeButton; 
    @FXML private VBox navFavoriteButton;
    @FXML private ImageView btnCart; 
    
    @FXML private ScrollPane mainScrollPane; 
    @FXML private VBox contentVBox;
    @FXML private TextField txtSearch;

    // --- CÁC BIẾN CHO PHÂN TRANG ---
    @FXML private Button btnPrevPage;
    @FXML private Button btnNextPage;
    @FXML private Label lblPageNumber;

    // --- DAOs và Session ---
    private final ProductDAO productDAO = new ProductDAO();
    private final CartDAO cartDAO = CartDAO.getInstance(); 
    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    
    // --- State và Ảnh ---
    private Image defaultImage;
    private Image heartEmptyImage;
    private Image heartFilledImage;
    private int currentUserId;
    
    // --- Biến Logic Phân Trang ---
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 8; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Kiểm tra đăng nhập
        if(SessionManager.getCurrentUser() == null) {
            return;
        }
        this.currentUserId = SessionManager.getCurrentUser().getUserId();
        
        // Load ảnh icon
        try {
            defaultImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            heartEmptyImage = new Image(getClass().getResourceAsStream("/images/heart.png"));
            heartFilledImage = new Image(getClass().getResourceAsStream("/images/heart_filled.png"));
        } catch (Exception e) { }

        // --- XỬ LÝ TAB MENU (TOP, MEN, WOMEN...) KHÔNG DÙNG CSS ---
        if (tabGroup != null) { 
            // 1. Bắt sự kiện khi bấm chuyển Tab
            tabGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
                
                // A. Trả nút CŨ về màu xám (nếu có)
                if (oldToggle != null) {
                    ((ToggleButton) oldToggle).setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-weight: bold; -fx-cursor: hand;");
                }

                // B. Đổi nút MỚI sang màu Cam + Gạch chân
                if (newToggle != null) {
                    ToggleButton newBtn = (ToggleButton) newToggle;
                    newBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4d2d; -fx-font-weight: bold; -fx-border-color: #ee4d2d; -fx-border-width: 0 0 2 0; -fx-cursor: hand;");
                    
                    // Load dữ liệu
                    String selectedTabText = newBtn.getText();
                    currentPage = 1;
                    loadProductsByGender(selectedTabText);
                } else {
                    loadProductsByGender("TOP");
                }
            });

            // 2. Thiết lập trạng thái ban đầu (Chạy 1 lần khi mở app)
            javafx.application.Platform.runLater(() -> {
                 ToggleButton selected = (ToggleButton) tabGroup.getSelectedToggle();
                 if (selected != null) {
                     // Ép màu cam cho nút đang chọn
                     selected.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4d2d; -fx-font-weight: bold; -fx-border-color: #ee4d2d; -fx-border-width: 0 0 2 0; -fx-cursor: hand;");
                     loadProductsByGender(selected.getText());
                 } else {
                     loadProductsByGender("TOP");
                 }
                 
                 // Reset màu xám cho các nút còn lại
                 for (javafx.scene.control.Toggle toggle : tabGroup.getToggles()) {
                     if (toggle != selected) {
                         ((ToggleButton) toggle).setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-weight: bold; -fx-cursor: hand;");
                     }
                 }

                 if (navHomeButton != null && navHomeButton.getScene() != null) {
                     ((Stage) navHomeButton.getScene().getWindow()).setTitle("E-Commerce - Trang chính");
                 }
            });
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            ToggleButton selected = (ToggleButton) tabGroup.getSelectedToggle();
            loadProductsByGender(selected != null ? selected.getText() : "TOP");
            return;
        }
        List<Product> searchResults = productDAO.searchProducts(keyword);
        displayProductList(searchResults);
        
        if(lblPageNumber != null) lblPageNumber.setText("Kết quả tìm kiếm");
        if(btnPrevPage != null) btnPrevPage.setDisable(true);
        if(btnNextPage != null) btnNextPage.setDisable(true);
    }

    private void loadProductsByGender(String genderText) {
        List<Product> productList;
        
        if (genderText.equalsIgnoreCase("TOP")) {
            int offset = (currentPage - 1) * ITEMS_PER_PAGE;
            productList = productDAO.getProductsWithPagination(ITEMS_PER_PAGE, offset);
            
            if (lblPageNumber != null) lblPageNumber.setText("Trang " + currentPage);
            if (btnPrevPage != null) btnPrevPage.setDisable(currentPage == 1);
            if (btnNextPage != null) btnNextPage.setDisable(productList.size() < ITEMS_PER_PAGE);
            
        } else {
            String dbGender = "";
            switch (genderText.toUpperCase()) {
                case "MEN": dbGender = "Nam"; break;
                case "WOMEN": dbGender = "Nữ"; break;
                case "KIDS": dbGender = "Trẻ em"; break;
            }
            productList = productDAO.getProductsByGender(dbGender);
            
            if (lblPageNumber != null) lblPageNumber.setText("Tất cả");
            if (btnPrevPage != null) btnPrevPage.setDisable(true);
            if (btnNextPage != null) btnNextPage.setDisable(true);
        }
        
        displayProductList(productList);
    }
    
    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadProductsByGender("TOP");
            if (mainScrollPane != null) mainScrollPane.setVvalue(0.0);
        }
    }
    
    @FXML
    private void handleNextPage() {
        currentPage++;
        loadProductsByGender("TOP");
        if (mainScrollPane != null) mainScrollPane.setVvalue(0.0);
    }

    private void displayProductList(List<Product> productList) {
        if (productGrid == null) return; 
        productGrid.getChildren().clear();

        if (productList.isEmpty()) {
            Label noResult = new Label("Không tìm thấy sản phẩm nào.");
            noResult.setPrefWidth(300);
            noResult.setAlignment(Pos.CENTER);
            productGrid.getChildren().add(noResult);
            return;
        }

        try {
            for (Product product : productList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/product_card.fxml"));
                VBox productCard = loader.load();

                ImageView imgProduct = (ImageView) productCard.lookup("#imgProduct");
                Label lblName = (Label) productCard.lookup("#lblName");
                Label lblPrice = (Label) productCard.lookup("#lblPrice");
                ImageView iconHeart = (ImageView) productCard.lookup("#iconHeart");
                ImageView iconCart = (ImageView) productCard.lookup("#iconCart");
                
                lblName.setText(product.getName());
                lblPrice.setText(FormatterUtils.formatPrice(product.getPrice()) + " VND");
                
                try {
                    String imgName = product.getImage();
                    if (imgName != null && !imgName.isEmpty()) {
                        imgProduct.setImage(new Image(getClass().getResourceAsStream("/images/" + imgName)));
                    } else { imgProduct.setImage(defaultImage); }
                } catch (Exception e) { imgProduct.setImage(defaultImage); }
                
                productCard.setOnMouseClicked(e -> openProductDetail(product, (Node) e.getSource()));
                productCard.setStyle("-fx-cursor: hand;"); 
                
                // --- Xử lý nút Thêm giỏ hàng ---
                iconCart.setOnMouseClicked(e -> {
                    Stage currentStage = (Stage) productGrid.getScene().getWindow();
                    
                    if (product.getQuantity() <= 0) {
                        Toast.show("Sản phẩm đã hết hàng!", currentStage);
                    } else {
                        cartDAO.addToCart(product, 1, "M"); 
                        Toast.show("Đã thêm '" + product.getName() + "' vào giỏ!", currentStage);
                    }
                    e.consume(); 
                });
                
                // --- Xử lý nút Yêu thích ---
                boolean isFav = favoriteDAO.isFavorite(currentUserId, product.getProductId());
                iconHeart.setImage(isFav ? heartFilledImage : heartEmptyImage);
                iconHeart.setOnMouseClicked(e -> {
                    toggleFavorite(product.getProductId(), iconHeart);
                    e.consume(); 
                });

                productGrid.getChildren().add(productCard);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    private void toggleFavorite(int productId, ImageView iconHeart) {
        boolean isCurrentlyFavorite = favoriteDAO.isFavorite(currentUserId, productId);
        Stage currentStage = (Stage) iconHeart.getScene().getWindow();
        
        if (isCurrentlyFavorite) {
            favoriteDAO.removeFavorite(currentUserId, productId);
            iconHeart.setImage(heartEmptyImage);
            Toast.show("Đã xóa khỏi yêu thích", currentStage);
        } else {
            favoriteDAO.addFavorite(currentUserId, productId);
            iconHeart.setImage(heartFilledImage);
            Toast.show("Đã thêm vào yêu thích", currentStage);
        }
    }
    
    private void openProductDetail(Product product, Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/product_detail.fxml"));
            Scene scene = new Scene(loader.load());
            ProductDetailController controller = loader.getController();
            controller.initData(product); 
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Chi tiết sản phẩm");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private Scene getSafeScene() {
        if (navHomeButton != null) return navHomeButton.getScene(); 
        if (tabGroup != null && !tabGroup.getToggles().isEmpty()) {
             return ((ToggleButton) tabGroup.getToggles().get(0)).getScene();
        }
        return null; 
    }

    private BorderPane getMainBorderPane() {
        Scene scene = getSafeScene();
        return (scene != null) ? (BorderPane) scene.getRoot() : null;
    }

    @FXML
    private void handleNavHomeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Scene scene = getSafeScene();
            if (scene != null) {
                Stage stage = (Stage) scene.getWindow(); 
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("E-Commerce - Trang chính");
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    @FXML
    public void handleNavCategoryClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/category_page.fxml"));
            loader.setController(this); 
            Node categoryPage = loader.load();
            BorderPane mainPane = getMainBorderPane();
            if (mainPane != null) {
                mainPane.setCenter(categoryPage);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public void switchToCategoryView() {
        handleNavCategoryClick();
    }

    @FXML
    private void handleCategoryClick(javafx.scene.input.MouseEvent event) {
        String categoryName = ((Label) event.getSource()).getText();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/product_list.fxml"));
            Scene scene = new Scene(loader.load());
            ProductListController controller = loader.getController();
            controller.loadCategory(categoryName);
            Stage stage = (Stage) getMainBorderPane().getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Danh sách sản phẩm");
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    @FXML
    public void handleNavPersonalClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/personal_page.fxml"));
            VBox personalPage = loader.load();
            
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(personalPage);
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setStyle("-fx-background-color: white;");
            
            Label topLabel = new Label("THÀNH VIÊN");
            topLabel.setFont(new Font("System Bold", 18));
            topLabel.setMaxWidth(Double.MAX_VALUE);
            topLabel.setAlignment(Pos.CENTER);
            topLabel.setPadding(new Insets(15, 0, 15, 0));
            topLabel.setStyle("-fx-background-color: white;");
            
            BorderPane mainPane = getMainBorderPane();
            if (mainPane != null) {
                mainPane.setTop(topLabel);
                mainPane.setCenter(scrollPane);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    @FXML
    private void handleNavFavoriteClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/favorites_page.fxml"));
            BorderPane favoritePage = loader.load();
            Stage stage = (Stage) getSafeScene().getWindow(); 
            stage.setScene(new Scene(favoritePage));
            stage.setTitle("Yêu thích");
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    @FXML
    private void handleCartClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/cart.fxml"));
            Stage stage = (Stage) btnCart.getScene().getWindow(); 
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Giỏ hàng");
        } catch (IOException e) { e.printStackTrace(); }
    }
}