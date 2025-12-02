package com.ecommerce.controller;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.VoucherDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.util.FormatterUtils;
import com.ecommerce.util.SessionManager;
import com.ecommerce.util.Toast; // Import Toast
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    @FXML private VBox cartContainer;
    @FXML private Button btnBack;
    
    @FXML private TextField txtVoucherCode;
    @FXML private Label lblDiscountMessage;
    @FXML private Label lblSubTotal;
    @FXML private Label lblDiscountAmount;
    @FXML private Label lblFinalTotal;

    private CartDAO cartDAO;
    private VoucherDAO voucherDAO;
    private int currentUserId;
    private double subTotal = 0;
    private int discountPercent = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.cartDAO = CartDAO.getInstance();
        this.voucherDAO = new VoucherDAO();
        try {
            if (SessionManager.getCurrentUser() != null) {
                this.currentUserId = SessionManager.getCurrentUser().getUserId();
            } else {
                this.currentUserId = 1; 
            }
        } catch (Exception e) { this.currentUserId = 1; }
        loadCartData();
    }

    public void loadCartData() {
        cartContainer.getChildren().clear();
        List<CartItem> cartItems = cartDAO.getCartItems(currentUserId);
        subTotal = 0;

        if (cartItems.isEmpty()) {
            Label emptyLabel = new Label("Giỏ hàng trống!");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #95a5a6; -fx-padding: 20;");
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            emptyLabel.setAlignment(Pos.CENTER);
            cartContainer.getChildren().add(emptyLabel);
        } else {
            for (CartItem item : cartItems) {
                subTotal += item.getProductPrice() * item.getQuantity();
                HBox itemBox = createCartItemRow(item);
                cartContainer.getChildren().add(itemBox);
                
                // Dùng separator nhưng có padding để không dính
                Separator sep = new Separator();
                sep.setPadding(new javafx.geometry.Insets(5, 0, 5, 0));
                cartContainer.getChildren().add(sep);
            }
        }
        updateTotals();
    }

    // --- HÀM QUAN TRỌNG: TẠO GIAO DIỆN DÒNG SẢN PHẨM ---
    private HBox createCartItemRow(CartItem item) {
        HBox hbox = new HBox();
        hbox.setSpacing(15);
        hbox.setAlignment(Pos.CENTER_LEFT);
        // Padding rộng rãi (10px)
        hbox.setPadding(new javafx.geometry.Insets(10));

        // 1. ẢNH SẢN PHẨM
        ImageView img = new ImageView();
        img.setFitWidth(80); img.setFitHeight(80); img.setPreserveRatio(true);
        try {
            String imgName = item.getProductImage();
            if (imgName == null || imgName.isEmpty()) imgName = "logo.png";
            img.setImage(new Image(getClass().getResourceAsStream("/images/" + imgName)));
        } catch (Exception e) { }

        // ===> SỰ KIỆN CLICK ẢNH <===
        img.setStyle("-fx-cursor: hand;"); 
        img.setOnMouseClicked(e -> openProductDetail(item.getProduct(), (Node) e.getSource()));

        // 2. THÔNG TIN
        VBox infoBox = new VBox(5);
        Label lblName = new Label(item.getProductName());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblName.setWrapText(true);
        
        // ===> SỰ KIỆN CLICK TÊN <===
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        lblName.setOnMouseClicked(e -> openProductDetail(item.getProduct(), (Node) e.getSource()));
        
        Label lblSize = new Label("Size: " + item.getSize());
        lblSize.setStyle("-fx-text-fill: #7f8c8d;");

        // 3. CỤM TĂNG GIẢM SỐ LƯỢNG
        HBox qtyBox = new HBox(0); 
        qtyBox.setAlignment(Pos.CENTER_LEFT);
        
        Button btnMinus = new Button("-");
        btnMinus.setStyle("-fx-background-color: #f0f0f0; -fx-cursor: hand; -fx-min-width: 30px; -fx-border-color: #ccc; -fx-border-radius: 3 0 0 3;");
        btnMinus.setOnAction(e -> {
            if (item.getQuantity() > 1) {
                cartDAO.addToCart(item.getProduct(), -1, item.getSize());
                loadCartData();
            }
        });

        TextField txtQty = new TextField(String.valueOf(item.getQuantity()));
        txtQty.setPrefWidth(40); txtQty.setAlignment(Pos.CENTER); txtQty.setEditable(false); 
        txtQty.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1 0 1 0; -fx-border-radius: 0;");

        Button btnPlus = new Button("+");
        btnPlus.setStyle("-fx-background-color: #f0f0f0; -fx-cursor: hand; -fx-min-width: 30px; -fx-border-color: #ccc; -fx-border-radius: 0 3 3 0;");
        btnPlus.setOnAction(e -> {
            cartDAO.addToCart(item.getProduct(), 1, item.getSize());
            loadCartData();
        });

        qtyBox.getChildren().addAll(btnMinus, txtQty, btnPlus);
        infoBox.getChildren().addAll(lblName, lblSize, qtyBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // 4. GIÁ VÀ XÓA
        VBox priceActionBox = new VBox(10);
        priceActionBox.setAlignment(Pos.CENTER_RIGHT);
        
        int itemTotal = (int)(item.getProductPrice() * item.getQuantity());
        Label lblPrice = new Label(FormatterUtils.formatPrice(itemTotal) + " VND");
        lblPrice.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnRemove = new Button("Xóa");
        btnRemove.setStyle("-fx-background-color: white; -fx-text-fill: red; -fx-border-color: red; -fx-border-radius: 3; -fx-cursor: hand;");
        btnRemove.setOnAction(e -> {
            cartDAO.removeCartItem(item.getCartId());
            loadCartData();
        });

        priceActionBox.getChildren().addAll(lblPrice, btnRemove);
        hbox.getChildren().addAll(img, infoBox, priceActionBox);
        return hbox;
    }

    // --- HÀM MỞ CHI TIẾT SẢN PHẨM ---
    private void openProductDetail(Product product, Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/product_detail.fxml"));
            Scene scene = new Scene(loader.load());
            ProductDetailController controller = loader.getController();
            controller.initData(product); 
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Chi tiết sản phẩm");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleApplyVoucher(ActionEvent event) {
        String code = txtVoucherCode.getText().trim();
        if (code.isEmpty()) {
            Toast.show("Vui lòng nhập mã giảm giá!", (Stage) txtVoucherCode.getScene().getWindow());
            return;
        }
        int percent = voucherDAO.getDiscountPercent(code);
        if (percent > 0) {
            this.discountPercent = percent;
            lblDiscountMessage.setText("Mã '" + code + "': Giảm " + percent + "%");
            lblDiscountMessage.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            Toast.show("Áp mã thành công!", (Stage) txtVoucherCode.getScene().getWindow());
        } else {
            this.discountPercent = 0;
            lblDiscountMessage.setText("Mã không hợp lệ!");
            lblDiscountMessage.setStyle("-fx-text-fill: red;");
            Toast.show("Mã giảm giá sai!", (Stage) txtVoucherCode.getScene().getWindow());
        }
        updateTotals();
    }

    public void updateTotals() {
        double discountVal = subTotal * discountPercent / 100;
        double finalVal = subTotal - discountVal;
        lblSubTotal.setText(FormatterUtils.formatPrice((int)subTotal) + " VND");
        if (discountPercent > 0) {
            lblDiscountAmount.setText("- " + FormatterUtils.formatPrice((int)discountVal) + " VND");
        } else {
            lblDiscountAmount.setText("- 0 VND");
        }
        lblFinalTotal.setText(FormatterUtils.formatPrice((int)finalVal) + " VND");
    }

    public void removeItemFromList(javafx.scene.layout.HBox itemPane) {
        cartContainer.getChildren().remove(itemPane);
        if (cartContainer.getChildren().isEmpty()) loadCartData();
    }

    @FXML
    private void handleCheckout() {
        if (subTotal <= 0) {
            Toast.show("Giỏ hàng đang trống!", (Stage) btnBack.getScene().getWindow());
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/checkout.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Thanh toán");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("E-Commerce - Trang chính");
        } catch (IOException e) { e.printStackTrace(); }
    }
}