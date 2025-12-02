package com.ecommerce.controller;

import com.ecommerce.controller.CartController;
import com.ecommerce.controller.ProductDetailController;
import com.ecommerce.dao.CartDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.util.FormatterUtils; // <-- QUAN TRỌNG
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException; 

public class CartItemController {

    @FXML private HBox rootPane;
    @FXML private ImageView imgProduct;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private Label lblQuantity;
    @FXML private ImageView btnRemove;
    @FXML private Label lblSize;

    private CartItem cartItem;
    private CartController cartController;
    private Image defaultImage;

    public void setData(CartItem item, CartController controller) {
        this.cartItem = item;
        this.cartController = controller;
        
        try {
            defaultImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Product product = item.getProduct();
        lblName.setText(product.getName());
        // --- ĐÃ SỬA GIÁ TIỀN ---
        lblPrice.setText(FormatterUtils.formatPrice(product.getPrice()) + " VND");
        lblQuantity.setText(String.valueOf(item.getQuantity()));
        lblSize.setText("Size: " + item.getSize());

        String imageName = product.getImage();
        try {
            if (imageName != null && !imageName.isEmpty()) {
                imgProduct.setImage(new Image(getClass().getResourceAsStream("/images/" + imageName)));
            } else {
                imgProduct.setImage(defaultImage);
            }
        } catch (Exception e) {
            imgProduct.setImage(defaultImage);
        }
        
        btnRemove.setOnMouseClicked(e -> {
            handleRemoveItem();
            e.consume(); 
        });
        
        imgProduct.setStyle("-fx-cursor: hand;");
        lblName.setStyle("-fx-font-weight: bold; -fx-cursor: hand;");
        
        imgProduct.setOnMouseClicked(e -> openProductDetail(product, (Node) e.getSource()));
        lblName.setOnMouseClicked(e -> openProductDetail(product, (Node) e.getSource()));
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
    private void handleIncreaseQuantity() {
        CartDAO.getInstance().addToCart(cartItem.getProduct(), 1, cartItem.getSize());
        lblQuantity.setText(String.valueOf(cartItem.getQuantity()));
        cartController.updateTotals();
    }

    @FXML
    private void handleDecreaseQuantity() {
        int currentQuantity = cartItem.getQuantity();
        if (currentQuantity > 1) {
            CartDAO.getInstance().addToCart(cartItem.getProduct(), -1, cartItem.getSize());
            lblQuantity.setText(String.valueOf(cartItem.getQuantity()));
            cartController.updateTotals();
        } else {
            handleRemoveItem();
        }
    }

    private void handleRemoveItem() {
        CartDAO.getInstance().removeFromCart(cartItem);
        cartController.removeItemFromList(rootPane);
        cartController.updateTotals();
    }
}