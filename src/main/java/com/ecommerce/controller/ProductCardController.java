package com.ecommerce.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ProductCardController {

    @FXML
    private VBox productCard;

    // Style mặc định: Viền xám nhạt #e0e0e0
    private final String NORMAL_STYLE = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-cursor: hand;";
    
    // Style khi Hover: Viền xám đậm #999999 (Tạo cảm giác focus)
    private final String HOVER_STYLE = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #999999; -fx-border-width: 1; -fx-cursor: hand;";

    @FXML
    private void handleHoverIn() {
        productCard.setStyle(HOVER_STYLE);
    }

    @FXML
    private void handleHoverOut() {
        productCard.setStyle(NORMAL_STYLE);
    }
}