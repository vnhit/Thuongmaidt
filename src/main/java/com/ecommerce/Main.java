package com.ecommerce;

import atlantafx.base.theme.PrimerLight; // <-- Import giao diện Sáng
// import atlantafx.base.theme.PrimerDark; // <-- Import giao diện Tối (nếu muốn dùng thì bỏ comment dòng này)
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // --- BƯỚC 1: KÍCH HOẠT GIAO DIỆN ATLANTAFX ---
        // Dòng lệnh này sẽ biến toàn bộ giao diện App thành chuẩn hiện đại (Windows 11/MacOS)
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        
        // Nếu bạn thích màu tối (Dark Mode) thì dùng dòng dưới đây thay cho dòng trên:
        // Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        // ---------------------------------------------

        // Load màn hình đăng nhập
        Parent root = FXMLLoader.load(getClass().getResource("/com/ecommerce/view/login.fxml"));
        
        primaryStage.setTitle("TechShop - Hệ thống quản lý");
        primaryStage.setScene(new Scene(root));
        
        
        // Không cho phép thay đổi kích thước cửa sổ login để tránh vỡ giao diện (Optional)
        primaryStage.setResizable(false); 
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}