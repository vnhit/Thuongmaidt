package com.ecommerce.controller;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.FavoriteDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.dao.ReviewDAO;
import com.ecommerce.model.Product;
import com.ecommerce.util.FormatterUtils;
import com.ecommerce.util.SessionManager;
import com.ecommerce.util.Toast; // <-- IMPORT TOAST
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProductDetailController implements Initializable {

    @FXML private ImageView btnBack;
    @FXML private ImageView btnCart;
    @FXML private ImageView imgProduct;
    @FXML private Label lblProductNameTop;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private ImageView iconHeart;
    @FXML private Label lblDescription;
    @FXML private ChoiceBox<String> choiceSize;
    @FXML private TextField txtQuantity;
    @FXML private Button btnDecrease;
    @FXML private Button btnIncrease;
    @FXML private Button btnAddToCart;
    @FXML private Label lblStatus;
    @FXML private VBox reviewBox;
    @FXML private TextField txtReviewContent;
    @FXML private ChoiceBox<Integer> choiceRating;

    private Product product;
    private int currentUserId;
    private CartDAO cartDAO;
    private FavoriteDAO favoriteDAO;
    private ReviewDAO reviewDAO;
    private ProductDAO productDAO;
    
    private Image heartEmptyImage;
    private Image heartFilledImage;
    private Image defaultImage;
    private int currentQuantity = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.cartDAO = CartDAO.getInstance();
        this.favoriteDAO = new FavoriteDAO();
        this.reviewDAO = new ReviewDAO();
        this.productDAO = new ProductDAO();
        
        try {
            if (SessionManager.getCurrentUser() != null) {
                this.currentUserId = SessionManager.getCurrentUser().getUserId();
            } else {
                this.currentUserId = 1; 
            }
        } catch (Exception e) {
            this.currentUserId = 1;
        }
        
        try {
            heartEmptyImage = new Image(getClass().getResourceAsStream("/images/heart.png"));
            heartFilledImage = new Image(getClass().getResourceAsStream("/images/heart_filled.png"));
            defaultImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        } catch (Exception e) { }
        
        txtQuantity.setText(String.valueOf(currentQuantity));
        choiceSize.getItems().addAll("S", "M", "L", "XL", "XXL");
        choiceSize.setValue("M"); 
        
        btnIncrease.setOnAction(e -> updateQuantity(1));
        btnDecrease.setOnAction(e -> updateQuantity(-1));
        
        choiceRating.getItems().addAll(5, 4, 3, 2, 1);
        choiceRating.setValue(5);
        
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) { 
                txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (!txtQuantity.getText().isEmpty()) {
                try {
                    int val = Integer.parseInt(txtQuantity.getText());
                    if (val > 0) {
                         if (product != null && val > product.getQuantity()) {
                             txtQuantity.setText(String.valueOf(product.getQuantity())); 
                             currentQuantity = product.getQuantity();
                         } else {
                             currentQuantity = val;
                         }
                    }
                } catch (NumberFormatException e) {}
            }
        });
    }
    
    private void updateQuantity(int amount) {
        int newQuantity = currentQuantity + amount;
        if (newQuantity > 0) {
            if (product != null && newQuantity > product.getQuantity()) {
                 showAlert(Alert.AlertType.WARNING, "Thông báo", "Chỉ còn " + product.getQuantity() + " sản phẩm trong kho!");
                 return;
            }
            currentQuantity = newQuantity;
            txtQuantity.setText(String.valueOf(currentQuantity));
        }
    }

    public void initData(Product passedProduct) {
        Product realProduct = productDAO.getProductById(passedProduct.getProductId());
        if (realProduct != null) {
            this.product = realProduct;
        } else {
            this.product = passedProduct;
        }

        lblName.setText(product.getName());
        lblProductNameTop.setText(product.getName());
        lblPrice.setText(FormatterUtils.formatPrice(product.getPrice()) + " VND");
        lblDescription.setText(product.getDescription());

        try {
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                imgProduct.setImage(new Image(getClass().getResourceAsStream("/images/" + product.getImage())));
            } else {
                imgProduct.setImage(defaultImage);
            }
        } catch (Exception e) {
            if (defaultImage != null) imgProduct.setImage(defaultImage);
        }
        
        if (product.getQuantity() > 0) {
            lblStatus.setText("Còn hàng (" + product.getQuantity() + ")");
            lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");
            btnAddToCart.setDisable(false);
        } else {
            lblStatus.setText("Hết hàng");
            lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
            btnAddToCart.setDisable(true);
        }
        
        updateFavoriteIcon();
        loadReviews();
    }
    
    private void loadReviews() {
        reviewBox.getChildren().clear();
        List<String> reviews = reviewDAO.getReviews(product.getProductId());
        
        if (reviews.isEmpty()) {
            Label lbl = new Label("Chưa có đánh giá nào.");
            lbl.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            reviewBox.getChildren().add(lbl);
        } else {
            for (String r : reviews) {
                Label lbl = new Label(r);
                lbl.setWrapText(true);
                reviewBox.getChildren().add(lbl);
                reviewBox.getChildren().add(new Separator());
            }
        }
    }

    @FXML
    private void handleSubmitReview() {
        String content = txtReviewContent.getText().trim();
        if (content.isEmpty()) {
            // Lỗi nhập liệu thì vẫn dùng Alert hoặc Toast đỏ tùy bạn, ở đây dùng Toast cho nhất quán
            Toast.show("Vui lòng nhập nội dung bình luận!", (Stage) btnAddToCart.getScene().getWindow());
            return;
        }
        
        int rating = choiceRating.getValue();
        reviewDAO.addReview(currentUserId, product.getProductId(), rating, content);
        txtReviewContent.clear();
        loadReviews();
        
        // --- TOAST THAY VÌ ALERT ---
        Toast.show("Đánh giá đã được gửi!", (Stage) btnAddToCart.getScene().getWindow());
    }

    @FXML
    private void handleAddToCart(ActionEvent event) {
        if (product.getQuantity() <= 0) {
             Toast.show("Sản phẩm này đã hết hàng!", (Stage) btnAddToCart.getScene().getWindow());
             return;
        }
        try {
             int val = Integer.parseInt(txtQuantity.getText());
             if (val > 0) currentQuantity = val;
        } catch (Exception e) { currentQuantity = 1; }
        
        String size = choiceSize.getValue();
        if (size == null || size.isEmpty()) {
            Toast.show("Vui lòng chọn Kích cỡ!", (Stage) btnAddToCart.getScene().getWindow());
            return;
        }

        cartDAO.addToCart(this.product, currentQuantity, size);
        
        // --- TOAST THAY VÌ ALERT ---
        Toast.show("Đã thêm vào giỏ hàng!", (Stage) btnAddToCart.getScene().getWindow());
    }

    @FXML
    private void handleToggleFavorite() {
        boolean isCurrentlyFavorite = favoriteDAO.isFavorite(currentUserId, product.getProductId());
        if (isCurrentlyFavorite) {
            favoriteDAO.removeFavorite(currentUserId, product.getProductId());
            Toast.show("Đã xóa khỏi yêu thích", (Stage) iconHeart.getScene().getWindow());
        } else {
            favoriteDAO.addFavorite(currentUserId, product.getProductId());
            Toast.show("Đã thêm vào yêu thích", (Stage) iconHeart.getScene().getWindow());
        }
        updateFavoriteIcon(); 
    }

    private void updateFavoriteIcon() {
        boolean isFav = favoriteDAO.isFavorite(currentUserId, product.getProductId());
        if (heartFilledImage != null && heartEmptyImage != null) {
            iconHeart.setImage(isFav ? heartFilledImage : heartEmptyImage);
        }
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
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}