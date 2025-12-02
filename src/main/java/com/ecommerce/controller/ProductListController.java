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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProductListController implements Initializable {

    @FXML private Label lblCategoryTitle;
    @FXML private TilePane productGrid;
    @FXML private ImageView btnBack; 
    @FXML private ToggleGroup genderTabGroup; 
    @FXML private ImageView btnCart; 

    private ProductDAO productDAO = new ProductDAO();
    private CartDAO cartDAO = CartDAO.getInstance();
    private FavoriteDAO favoriteDAO = new FavoriteDAO();
    
    private Image defaultImage;
    private Image heartEmptyImage;
    private Image heartFilledImage;
    private String currentCategory;
    private String currentGender = "TOP"; 
    private int currentUserId;
    private List<Product> productList; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(SessionManager.getCurrentUser() == null) return;
        this.currentUserId = SessionManager.getCurrentUser().getUserId();

        try {
            defaultImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            heartEmptyImage = new Image(getClass().getResourceAsStream("/images/heart.png"));
            heartFilledImage = new Image(getClass().getResourceAsStream("/images/heart_filled.png"));
        } catch (Exception e) {}

        // --- SỬA LỖI GIAO DIỆN TAB (TOP, MEN...) GIỐNG TRANG CHỦ ---
        if (genderTabGroup != null) {
            // 1. Sự kiện khi chuyển Tab
            genderTabGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
                
                // A. Trả nút CŨ về màu xám
                if (oldToggle != null) {
                    ((ToggleButton) oldToggle).setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-weight: bold; -fx-cursor: hand;");
                }

                // B. Đổi nút MỚI sang màu Cam + Gạch chân
                if (newToggle != null) {
                    ToggleButton newBtn = (ToggleButton) newToggle;
                    newBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4d2d; -fx-font-weight: bold; -fx-border-color: #ee4d2d; -fx-border-width: 0 0 2 0; -fx-cursor: hand;");
                    
                    this.currentGender = newBtn.getText();
                } else {
                    this.currentGender = "TOP";
                }
                updateProductView();
            });
            
            // 2. Thiết lập trạng thái ban đầu (Để nút TOP có màu cam ngay khi mở)
            javafx.application.Platform.runLater(() -> {
                 ToggleButton selected = (ToggleButton) genderTabGroup.getSelectedToggle();
                 if (selected != null) {
                     selected.setStyle("-fx-background-color: transparent; -fx-text-fill: #ee4d2d; -fx-font-weight: bold; -fx-border-color: #ee4d2d; -fx-border-width: 0 0 2 0; -fx-cursor: hand;");
                 }
                 
                 // Đảm bảo các nút chưa chọn có màu xám
                 for (javafx.scene.control.Toggle toggle : genderTabGroup.getToggles()) {
                     if (toggle != selected) {
                         ((ToggleButton) toggle).setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-weight: bold; -fx-cursor: hand;");
                     }
                 }
            });
        }
        // -----------------------------------------------------------
    }

    public void loadCategory(String category) {
        this.currentCategory = category;
        lblCategoryTitle.setText(category.toUpperCase());
        updateProductView();
    }
    
    private void updateProductView() {
        String dbGender;
        switch (currentGender.toUpperCase()) {
            case "MEN": dbGender = "Nam"; break;
            case "WOMEN": dbGender = "Nữ"; break;
            case "KIDS": dbGender = "Trẻ em"; break;
            default: dbGender = "TOP"; 
        }
        this.productList = productDAO.getFilteredProducts(this.currentCategory, dbGender);
        displayProducts(this.productList);
    }

    private void displayProducts(List<Product> products) {
        productGrid.getChildren().clear(); 
        try {
            for (Product product : products) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/product_card.fxml"));
                VBox productCard = loader.load();
                
                ImageView imgProduct = (ImageView) productCard.lookup("#imgProduct");
                Label lblName = (Label) productCard.lookup("#lblName");
                Label lblPrice = (Label) productCard.lookup("#lblPrice");
                ImageView iconHeart = (ImageView) productCard.lookup("#iconHeart");
                ImageView iconCart = (ImageView) productCard.lookup("#iconCart");

                lblName.setText(product.getName());
                lblPrice.setText(FormatterUtils.formatPrice(product.getPrice()) + " VND");
                
                String imageName = product.getImage();
                try {
                    if (imageName != null && !imageName.isEmpty()) {
                        imgProduct.setImage(new Image(getClass().getResourceAsStream("/images/" + imageName)));
                    } else { imgProduct.setImage(defaultImage); }
                } catch (Exception e) { imgProduct.setImage(defaultImage); }
                
                productCard.setOnMouseClicked(e -> openProductDetail(product, (Node) e.getSource()));
                productCard.setStyle("-fx-cursor: hand;"); 
                
                iconCart.setOnMouseClicked(e -> {
                    // Cập nhật Toast + Check tồn kho
                    Stage currentStage = (Stage) productGrid.getScene().getWindow();
                    if (product.getQuantity() <= 0) {
                        Toast.show("Sản phẩm đã hết hàng!", currentStage);
                    } else {
                        cartDAO.addToCart(product, 1, "M"); 
                        Toast.show("Đã thêm '" + product.getName() + "' vào giỏ!", currentStage);
                    }
                    e.consume();
                });
                
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

    @FXML
    private void handleFilterClick() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Tiêu biểu", "Tiêu biểu", "Giá từ thấp đến cao", "Giá từ cao đến thấp");
        dialog.setTitle("Lọc sản phẩm");
        dialog.setHeaderText("SẮP XẾP THEO");
        dialog.setContentText("Chọn tiêu chí:");

        dialog.showAndWait().ifPresent(result -> {
            if (result.equals("Giá từ thấp đến cao")) {
                productList.sort((p1, p2) -> Integer.compare(p1.getPrice(), p2.getPrice()));
            } else if (result.equals("Giá từ cao đến thấp")) {
                productList.sort((p1, p2) -> Integer.compare(p2.getPrice(), p1.getPrice()));
            } else {
                updateProductView(); 
                return; 
            }
            displayProducts(productList);
        });
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

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("E-Commerce - Trang chính");
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
    
    @FXML private void handleNavHomeClick() { handleBack(); }
    @FXML private void handleNavCategoryClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            MainController mainController = loader.getController();
            mainController.switchToCategoryView(); 
        } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML private void handleNavFavoriteClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/favorites_page.fxml"));
            BorderPane favoritePage = loader.load(); 
            Stage stage = (Stage) btnBack.getScene().getWindow(); 
            stage.setScene(new Scene(favoritePage));
        } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML private void handleNavPersonalClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            MainController mainController = loader.getController();
            mainController.handleNavPersonalClick();
        } catch (IOException e) { e.printStackTrace(); }
    }
}