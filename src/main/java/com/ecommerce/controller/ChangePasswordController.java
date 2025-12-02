package com.ecommerce.controller;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.SessionManager;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane; // <-- DÒNG IMPORT ĐÃ THÊM

public class ChangePasswordController implements Initializable {

    @FXML
    private ImageView btnBack;
    @FXML
    private PasswordField txtOldPassword;
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Label lblMessage;

    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.currentUser = SessionManager.getCurrentUser();
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
        String oldPass = txtOldPassword.getText();
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            lblMessage.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            lblMessage.setText("Mật khẩu mới không khớp!");
            return;
        }
        if (currentUser == null) {
            lblMessage.setText("Lỗi: Không tìm thấy người dùng. Vui lòng đăng nhập lại.");
            return;
        }

        boolean success = userDAO.changePassword(currentUser.getUsername(), oldPass, newPass);

        if (success) {
            lblMessage.setText("Đổi mật khẩu thành công!");
            txtOldPassword.clear();
            txtNewPassword.clear();
            txtConfirmPassword.clear();
        } else {
            lblMessage.setText("Mật khẩu cũ không chính xác!");
        }
    }

    /**
     * Xử lý khi nhấn nút QUAY LẠI (về trang cá nhân)
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            
            MainController mainController = loader.getController();
            mainController.handleNavPersonalClick();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // ========================================================
    // CÁC HÀM ĐIỀU HƯỚNG CHO THANH DƯỚI CÙNG
    // ========================================================
    
    @FXML
    private void handleNavHomeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleNavCategoryClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            
            MainController mainController = loader.getController();
            mainController.switchToCategoryView(); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleNavPersonalClick() {
        handleBack(); // Quay về trang cá nhân
    }

    // Hàm cho nút "YÊU THÍCH"
    @FXML
    private void handleNavFavoriteClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/favorites_page.fxml"));
            BorderPane favoritePage = loader.load(); // <-- Lỗi xảy ra vì thiếu import
            
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(favoritePage));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}