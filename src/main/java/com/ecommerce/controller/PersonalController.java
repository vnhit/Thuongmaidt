package com.ecommerce.controller;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PersonalController implements Initializable {

    @FXML
    private Label lblName;
    @FXML
    private Label lblEmail;
    @FXML
    private Label btnChangePassword;
    @FXML
    private Label btnEditProfile; 

    // ===== THÊM 2 DÒNG NÀY =====
    @FXML
    private Label btnOrderHistory;
    @FXML
    private Label btnPurchaseHistory;
    // ============================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = SessionManager.getCurrentUser();
        
        if (user != null) {
            lblName.setText(user.getUsername());
            lblEmail.setText(user.getEmail());
        } else {
            lblName.setText("Khách");
            lblEmail.setText("Không có email");
        }
    }

    @FXML
    private void handleLogoutClick() {
        CartDAO.getInstance().clearMemoryCart();
        SessionManager.logout();
        
        try {
            Stage currentStage = (Stage) lblName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/login.fxml"));
            Scene loginScene = new Scene(loader.load());
            currentStage.setScene(loginScene);
            currentStage.setTitle("E-Commerce Desktop App");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleChangePasswordClick() {
        try {
            Stage stage = (Stage) lblName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/change_password.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEditProfileClick() {
         try {
            Stage stage = (Stage) lblName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/edit_profile.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Hàm CŨ: Mở trang Lịch sử Đơn hàng
     */
    @FXML
    private void handleOrderHistoryClick() {
        try {
            Stage stage = (Stage) lblName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/order_history.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Lịch sử đơn hàng");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * HÀM MỚI: Mở trang Lịch sử Mua hàng
     */
    @FXML
    private void handlePurchaseHistoryClick() {
        try {
            Stage stage = (Stage) lblName.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/purchase_history.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Lịch sử mua hàng");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}