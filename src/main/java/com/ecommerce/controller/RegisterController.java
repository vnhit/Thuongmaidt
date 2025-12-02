package com.ecommerce.controller;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField txtUsername, txtGender, txtPhone, txtEmail, txtAddress, txtCity;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblMessage;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister(ActionEvent event) {
        if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            lblMessage.setText("Vui lòng nhập đầy đủ thông tin bắt buộc!");
            return;
        }

        User u = new User();
        u.setUsername(txtUsername.getText());
        u.setPassword(txtPassword.getText());
        u.setGender(txtGender.getText());
        u.setPhoneNumber(txtPhone.getText());
        u.setEmail(txtEmail.getText());
        u.setAddress(txtAddress.getText());
        u.setCity(txtCity.getText());

        boolean success = userDAO.register(u);

        if (success) {
            lblMessage.setText("Đăng ký thành công! Quay lại đăng nhập...");
            openLoginPage();
        } else {
            lblMessage.setText("Tên đăng nhập hoặc email đã tồn tại!");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        openLoginPage();
    }

    private void openLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Đăng nhập hệ thống");
            stage.show();

            // Đóng trang hiện tại
            Stage current = (Stage) txtUsername.getScene().getWindow();
            current.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
