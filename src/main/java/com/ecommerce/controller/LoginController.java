package com.ecommerce.controller;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMessage;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // 1. Kiểm tra nhập liệu
        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // 2. Kiểm tra lối tắt vào ADMIN (Hardcode)
        if (username.equals("admin") && password.equals("admin")) {
            // --- FIX LỖI NULL: TẠO ADMIN ẢO ĐỂ LƯU SESSION ---
            User adminUser = new User();
            adminUser.setUserId(0); // ID đặc biệt
            adminUser.setUsername("Administrator");
            adminUser.setEmail("admin@techshop.com");
            adminUser.setAddress("Hệ thống quản trị");
            adminUser.setCity("Server");
            adminUser.setPhoneNumber("0999999999");
            adminUser.setGender("Nam");
            
            // Lưu vào Session để các trang sau có thể gọi getCurrentUser()
            SessionManager.login(adminUser);
            // -------------------------------------------------
            
            openAdminPage();
            return; // Dừng lại, không chạy logic user bên dưới
        }

        // 3. Kiểm tra đăng nhập USER thường
        User user = userDAO.login(username, password);

        if (user != null) {
            // Kiểm tra khóa
            if (user.isLocked()) {
                lblMessage.setText("Tài khoản của bạn đã bị khóa!");
                lblMessage.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                return;
            }
            
            lblMessage.setText("Đăng nhập thành công!");
            SessionManager.login(user); // Lưu session
            CartDAO.getInstance().loadCartFromDB(user.getUserId()); // Load giỏ hàng
            openMainPage();
        } else {
            lblMessage.setText("Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    private void openMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("E-Commerce - Trang chính");
            stage.show();
            ((Stage) txtUsername.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAdminPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/admin.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Hệ thống Quản trị - Admin");
            stage.show();
            ((Stage) txtUsername.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Lỗi: Không thể mở trang Admin!");
        }
    }

    @FXML
    private void openRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/register.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Đăng ký tài khoản");
            stage.show();
            ((Stage) txtUsername.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}