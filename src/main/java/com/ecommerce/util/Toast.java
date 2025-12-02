package com.ecommerce.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Toast {

    public static void show(String msg, Stage ownerStage) {
        // 1. Tạo một cửa sổ (Stage) không viền, trong suốt
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        // 2. Tạo nội dung thông báo
        Label text = new Label(msg);
        text.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14)); // Font chữ đẹp
        text.setTextFill(Color.WHITE); // Chữ trắng
        
        // Style: Nền đen mờ, bo tròn góc
        text.setStyle("-fx-background-color: rgba(33, 33, 33, 0.85); " +
                      "-fx-background-radius: 10; " +
                      "-fx-padding: 15px 25px; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        StackPane root = new StackPane(text);
        root.setStyle("-fx-background-color: transparent;"); // Nền gốc trong suốt
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // Scene trong suốt
        toastStage.setScene(scene);
        
        // 3. Tính toán vị trí hiển thị
        // Hiện ở giữa màn hình nhưng lệch xuống dưới một chút
        toastStage.setOnShown(e -> {
            double x = ownerStage.getX() + (ownerStage.getWidth() / 2) - (toastStage.getWidth() / 2);
            double y = ownerStage.getY() + (ownerStage.getHeight() / 2) + 150; // +150 để nằm ở nửa dưới
            toastStage.setX(x);
            toastStage.setY(y);
        });
        
        toastStage.show();

        // 4. Tạo hiệu ứng mờ dần (Fade out) và tự tắt
        Timeline timeline = new Timeline();
        
        // Giữ nguyên trong 1.5 giây đầu
        KeyFrame key1 = new KeyFrame(Duration.millis(1500), 
                new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 1)); 
        
        // Mờ dần trong 0.5 giây tiếp theo
        KeyFrame key2 = new KeyFrame(Duration.millis(2000), 
                new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 0)); 
        
        timeline.getKeyFrames().addAll(key1, key2);
        
        // Khi chạy xong thì đóng cửa sổ để giải phóng bộ nhớ
        timeline.setOnFinished((ae) -> toastStage.close()); 
        timeline.play();
    }
}