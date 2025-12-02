package com.ecommerce.controller;

import com.ecommerce.dao.FavoriteDAO;
import com.ecommerce.model.Product;
import com.ecommerce.util.FormatterUtils; // <-- QUAN TRỌNG
import com.ecommerce.util.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node; 
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane; 

public class FavoritesController implements Initializable {

    @FXML private VBox favoritesList;
    private FavoriteDAO favoriteDAO = new FavoriteDAO();
    private Image defaultImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            defaultImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadFavorites();
    }

    private void loadFavorites() {
        int userId = SessionManager.getCurrentUser().getUserId();
        List<Product> productList = favoriteDAO.getFavoriteProductsByUserId(userId);

        if (productList.isEmpty()) {
            favoritesList.getChildren().add(new Label("Bạn chưa có sản phẩm yêu thích nào."));
            return;
        }
        for (Product product : productList) {
            favoritesList.getChildren().add(createFavoriteCard(product));
        }
    }
    
    private HBox createFavoriteCard(Product product) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 5;");
        
        ImageView imageView = new ImageView();
        String imageName = product.getImage();
        try {
            if (imageName != null && !imageName.isEmpty()) {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/" + imageName)));
            } else {
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            imageView.setImage(defaultImage);
        }
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setStyle("-fx-cursor: hand;"); 
        
        imageView.setOnMouseClicked(e -> {
            openProductDetail(product, (Node) e.getSource());
        });

        VBox infoBox = new VBox(5);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox priceAndHeartBox = new HBox();
        // --- ĐÃ SỬA GIÁ TIỀN ---
        Label priceLabel = new Label(FormatterUtils.formatPrice(product.getPrice()) + " VND");
        priceLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        
        VBox spacer = new VBox();
        HBox.setHgrow(spacer, Priority.ALWAYS); 
        
        ImageView heartIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/heart_filled.png")));
        heartIcon.setFitHeight(20);
        heartIcon.setFitWidth(20);
        heartIcon.setStyle("-fx-cursor: hand;");
        
        heartIcon.setOnMouseClicked(e -> {
            int userId = SessionManager.getCurrentUser().getUserId();
            favoriteDAO.removeFavorite(userId, product.getProductId());
            favoritesList.getChildren().remove(card);
        });
        
        priceAndHeartBox.getChildren().addAll(priceLabel, spacer, heartIcon);
        infoBox.getChildren().addAll(nameLabel, priceAndHeartBox);
        card.getChildren().addAll(imageView, infoBox);
        return card;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleNavHomeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) favoritesList.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleNavCategoryClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) favoritesList.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            MainController mainController = loader.getController();
            mainController.switchToCategoryView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleNavPersonalClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) favoritesList.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            MainController mainController = loader.getController();
            mainController.handleNavPersonalClick();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}