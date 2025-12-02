// ... (Import giữ nguyên) ...
package com.ecommerce.controller;

import com.ecommerce.dao.OrderDetailDAO;
import com.ecommerce.dao.ProductDAO; 
import com.ecommerce.model.OrderDetail;
import com.ecommerce.model.Product;
import com.ecommerce.util.FormatterUtils;
import com.ecommerce.util.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PurchaseHistoryController implements Initializable {
    // ... (Giữ nguyên phần đầu) ...
    @FXML private ImageView btnBack;
    @FXML private VBox purchaseListVBox; 
    private OrderDetailDAO orderDetailDAO;
    private ProductDAO productDAO; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orderDetailDAO = new OrderDetailDAO();
        productDAO = new ProductDAO(); 
        loadPurchaseHistory();
    }

    private void loadPurchaseHistory() {
        int userId = SessionManager.getCurrentUser().getUserId();
        List<OrderDetail> detailsList = orderDetailDAO.getAllDetailsByUserId(userId);

        if (detailsList.isEmpty()) {
            purchaseListVBox.getChildren().add(new Label("Bạn chưa mua sản phẩm nào."));
            return;
        }

        List<Product> allProducts = productDAO.getAll();

        for (OrderDetail detail : detailsList) {
            Product product = allProducts.stream()
                .filter(p -> p.getProductId() == detail.getProductId())
                .findFirst()
                .orElse(null); 
            
            if (product != null) {
                VBox card = new VBox(5);
                card.setPadding(new Insets(10));
                card.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

                Label productName = new Label(product.getName());
                productName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                // HIỂN THỊ RÕ RÀNG: Size và Số lượng đặt
                Label detailLabel = new Label("Size: " + detail.getSize() + " | Số lượng đặt: " + detail.getQuantity());
                
                Label priceLabel = new Label("Đơn giá: " + FormatterUtils.formatPrice(detail.getPrice()) + " VND");
                priceLabel.setStyle("-fx-text-fill: red;");
                
                Label orderIdLabel = new Label("(Từ Đơn hàng #" + detail.getOrderId() + ")");
                orderIdLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

                card.getChildren().addAll(productName, detailLabel, priceLabel, orderIdLabel);
                purchaseListVBox.getChildren().add(card);
            }
        }
    }
    // ... (Các hàm điều hướng giữ nguyên) ...
    @FXML private void handleBack() { try { FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml")); Stage stage = (Stage) btnBack.getScene().getWindow(); stage.setScene(new Scene(loader.load())); MainController mainController = loader.getController(); mainController.handleNavPersonalClick(); } catch (IOException e) { e.printStackTrace(); } }
    @FXML private void handleNavHomeClick() { try { FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml")); Stage stage = (Stage) btnBack.getScene().getWindow(); stage.setScene(new Scene(loader.load())); } catch (IOException e) { e.printStackTrace(); } }
    @FXML private void handleNavCategoryClick() { try { FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml")); Stage stage = (Stage) btnBack.getScene().getWindow(); stage.setScene(new Scene(loader.load())); MainController mainController = loader.getController(); mainController.switchToCategoryView(); } catch (IOException e) { e.printStackTrace(); } }
    @FXML private void handleNavFavoriteClick() { try { FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/favorites_page.fxml")); BorderPane favoritePage = loader.load(); Stage stage = (Stage) btnBack.getScene().getWindow(); stage.setScene(new Scene(favoritePage)); } catch (IOException e) { e.printStackTrace(); } }
    @FXML private void handleNavPersonalClick() { handleBack(); }
}