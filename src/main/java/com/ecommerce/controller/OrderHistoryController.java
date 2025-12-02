// ... (Import giữ nguyên) ...
package com.ecommerce.controller;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.OrderDetailDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.util.FormatterUtils;
import com.ecommerce.util.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar.ButtonData;

public class OrderHistoryController implements Initializable {
    // ... (Giữ nguyên phần đầu) ...
    @FXML private ImageView btnBack;
    @FXML private VBox orderListVBox; 
    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orderDAO = new OrderDAO();
        orderDetailDAO = new OrderDetailDAO();
        loadOrderHistory();
    }

    // ... (Hàm loadOrderHistory giữ nguyên) ...
    private void loadOrderHistory() {
        orderListVBox.getChildren().clear(); 
        int userId = SessionManager.getCurrentUser().getUserId();
        List<Order> orderList = orderDAO.getOrdersByUserId(userId);
        if (orderList.isEmpty()) { orderListVBox.getChildren().add(new Label("Bạn chưa có đơn hàng nào.")); return; }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (Order order : orderList) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-cursor: hand;");
            Label orderIdLabel = new Label("Đơn hàng #" + order.getOrderId());
            orderIdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label dateLabel = new Label("Ngày đặt: " + order.getCreatedAt().format(formatter));
            Label statusLabel = new Label("Trạng thái: " + order.getStatus());
            if (order.getStatus().equals("Đang xử lý")) { statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #007bff;"); } 
            else if (order.getStatus().equals("Đã hủy")) { statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;"); } 
            else { statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: green;"); }
            Label totalLabel = new Label("Tổng tiền: " + FormatterUtils.formatPrice(order.getTotalMoney()) + " VND");
            totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
            card.getChildren().addAll(orderIdLabel, dateLabel, statusLabel, totalLabel);
            card.setOnMouseClicked(e -> showOrderDetailPopup(order));
            orderListVBox.getChildren().add(card);
        }
    }
    
    private void showOrderDetailPopup(Order order) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết đơn hàng #" + order.getOrderId());
        dialog.setHeaderText("Trạng thái: " + order.getStatus());
        ButtonType closeButtonType = new ButtonType("Đóng", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setPrefWidth(400);

        List<CartItem> items = orderDetailDAO.getItemsByOrderId(order.getOrderId());
        
        VBox itemsBox = new VBox(10);
        for (CartItem item : items) {
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            
            ImageView img = new ImageView();
            try { img.setImage(new Image(getClass().getResourceAsStream("/images/" + item.getProduct().getImage()))); } catch (Exception e) {}
            img.setFitWidth(50); img.setFitHeight(50);
            
            VBox info = new VBox(3);
            Label name = new Label(item.getProduct().getName());
            name.setStyle("-fx-font-weight: bold;");
            
            // HIỆN THỊ THÔNG TIN RÕ RÀNG
            // Sử dụng item.getQuantity() (đã được sửa trong DAO để lấy đúng số lượng đặt)
            Label detail = new Label("Size: " + item.getSize() + " | SL: " + item.getQuantity());
            Label price = new Label(FormatterUtils.formatPrice(item.getProduct().getPrice()) + " đ");
            price.setStyle("-fx-text-fill: red;");
            
            info.getChildren().addAll(name, detail, price);
            itemRow.getChildren().addAll(img, info);
            itemsBox.getChildren().add(itemRow);
        }
        
        ScrollPane scroll = new ScrollPane(itemsBox);
        scroll.setFitToWidth(true);
        scroll.setMaxHeight(300);
        content.getChildren().add(scroll);
        
        if (order.getStatus().equals("Đang xử lý")) {
            Button btnCancel = new Button("HỦY ĐƠN HÀNG");
            btnCancel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
            btnCancel.setMaxWidth(Double.MAX_VALUE);
            btnCancel.setOnAction(e -> {
                boolean success = orderDAO.cancelOrder(order.getOrderId());
                if (success) { showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã hủy đơn hàng thành công!"); dialog.close(); loadOrderHistory(); } 
                else { showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể hủy đơn hàng này."); }
            });
            content.getChildren().add(btnCancel);
        }

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    // ... (Các hàm còn lại giữ nguyên) ...
    private void showAlert(Alert.AlertType type, String title, String content) { Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.showAndWait(); }
    @FXML private void handleBack() { try { FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml")); Stage stage = (Stage) btnBack.getScene().getWindow(); stage.setScene(new Scene(loader.load())); MainController mainController = loader.getController(); mainController.handleNavPersonalClick(); } catch (IOException e) { e.printStackTrace(); } }
    @FXML private void handleNavHomeClick() { navigateTo("/com/ecommerce/view/main.fxml"); }
    @FXML private void handleNavCategoryClick() { try { FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml")); Stage stage = (Stage) btnBack.getScene().getWindow(); stage.setScene(new Scene(loader.load())); MainController mainController = loader.getController(); mainController.switchToCategoryView(); } catch (Exception e) { e.printStackTrace(); } }
    @FXML private void handleNavFavoriteClick() { navigateTo("/com/ecommerce/view/favorites_page.fxml"); }
    @FXML private void handleNavPersonalClick() { handleBack(); }
    private void navigateTo(String fxmlPath) { try { FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath)); Stage stage = (Stage) btnBack.getScene().getWindow(); stage.setScene(new Scene(loader.load())); } catch (IOException e) { e.printStackTrace(); } }
}