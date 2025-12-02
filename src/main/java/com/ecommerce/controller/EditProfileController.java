package com.ecommerce.controller;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane; // <-- DÒNG IMPORT ĐÃ THÊM

public class EditProfileController implements Initializable {

    @FXML
    private ImageView btnBack;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtGender;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtCity;
    @FXML
    private Label lblMessage;

    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.currentUser = SessionManager.getCurrentUser();
        
        if (currentUser != null) {
            txtUsername.setText(currentUser.getUsername());
            txtUsername.setDisable(true); 
            
            txtEmail.setText(currentUser.getEmail());
            txtGender.setText(currentUser.getGender());
            txtPhone.setText(currentUser.getPhoneNumber());
            txtAddress.setText(currentUser.getAddress());
            txtCity.setText(currentUser.getCity());
        }
    }

    @FXML
    private void handleSaveProfile(ActionEvent event) {
        if (currentUser == null) {
            lblMessage.setText("Lỗi: Không tìm thấy người dùng!");
            return;
        }

        String newEmail = txtEmail.getText();
        String newGender = txtGender.getText();
        String newPhone = txtPhone.getText();
        String newAddress = txtAddress.getText();
        String newCity = txtCity.getText();

        currentUser.setEmail(newEmail);
        currentUser.setGender(newGender);
        currentUser.setPhoneNumber(newPhone);
        currentUser.setAddress(newAddress);
        currentUser.setCity(newCity);

        boolean success = userDAO.updateUser(currentUser);

        if (success) {
            SessionManager.login(currentUser);
            lblMessage.setText("Cập nhật hồ sơ thành công!");
        } else {
            lblMessage.setText("Cập nhật thất bại! (Email có thể đã tồn tại)");
        }
    }

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