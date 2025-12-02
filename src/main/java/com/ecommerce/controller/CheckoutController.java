package com.ecommerce.controller;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.OrderDetailDAO;
import com.ecommerce.dao.VoucherDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderDetail;
import com.ecommerce.model.User;
import com.ecommerce.util.FormatterUtils;
import com.ecommerce.util.SessionManager;
import com.ecommerce.util.Toast; 
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CheckoutController implements Initializable {

    // --- FXML Components ---
    @FXML private ImageView btnBack;
    @FXML private Label lblName, lblPhone, lblAddress;
    @FXML private VBox orderItemsList; // VBox chứa danh sách tóm tắt
    @FXML private ToggleGroup paymentGroup;
    @FXML private Label lblTotal; // Tổng tiền cuối cùng
    
    // --- Voucher ---
    @FXML private TextField txtVoucher;
    @FXML private Label lblDiscount;

    // --- DAO & State ---
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private VoucherDAO voucherDAO;
    
    private User currentUser;
    private double originalTotal = 0;
    private double finalTotal = 0;
    private String appliedVoucherCode = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.cartDAO = CartDAO.getInstance();
        this.orderDAO = new OrderDAO();
        this.orderDetailDAO = new OrderDetailDAO();
        this.voucherDAO = new VoucherDAO();
        
        try {
            this.currentUser = SessionManager.getCurrentUser();
            
            // 1. Hiển thị thông tin người nhận (Lấy từ User đang đăng nhập)
            if (currentUser != null) {
                lblName.setText(currentUser.getUsername());
                lblPhone.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Chưa có SĐT");
                
                String addr = (currentUser.getAddress() != null ? currentUser.getAddress() : "") + 
                              (currentUser.getCity() != null ? ", " + currentUser.getCity() : "");
                
                if (addr.trim().equals(",") || addr.trim().isEmpty()) {
                    lblAddress.setText("Chưa cập nhật địa chỉ nhận hàng");
                    lblAddress.setStyle("-fx-text-fill: red;");
                } else {
                    lblAddress.setText(addr);
                }
            }

            // 2. Load danh sách sản phẩm vào VBox tóm tắt
            loadOrderItems();

            // 3. Tính tổng tiền ban đầu
            this.originalTotal = cartDAO.getTotal(); 
            this.finalTotal = originalTotal;
            
            updateTotalUI();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khởi tạo CheckoutController: " + e.getMessage());
        }
    }
    
    /**
     * Load danh sách sản phẩm từ giỏ hàng ra giao diện tóm tắt
     */
    private void loadOrderItems() {
        orderItemsList.getChildren().clear();
        List<CartItem> items = cartDAO.getItems();
        
        if (items.isEmpty()) {
            orderItemsList.getChildren().add(new Label("Không có sản phẩm nào."));
            return;
        }

        for (CartItem item : items) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            
            // Tên SP + Size + Số lượng
            String itemInfo = item.getProductName() + "\n(Size: " + item.getSize() + " | x" + item.getQuantity() + ")";
            Label lblName = new Label(itemInfo);
            lblName.setStyle("-fx-font-size: 13px;");
            lblName.setWrapText(true);
            
            // Giá tiền
            int totalItemPrice = (int)(item.getProductPrice() * item.getQuantity());
            Label lblPrice = new Label(FormatterUtils.formatPrice(totalItemPrice) + " VND");
            lblPrice.setStyle("-fx-font-weight: bold;");
            
            // Spacer đẩy giá sang phải
            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            row.getChildren().addAll(lblName, spacer, lblPrice);
            orderItemsList.getChildren().add(row);
            orderItemsList.getChildren().add(new Separator());
        }
    }

    @FXML
    private void handleApplyVoucher() {
        String code = txtVoucher.getText().trim();
        if (code.isEmpty()) {
            Toast.show("Vui lòng nhập mã!", (Stage) txtVoucher.getScene().getWindow());
            return;
        }
        
        int percent = voucherDAO.getDiscountPercent(code);
        if (percent > 0) {
            double discountAmount = (originalTotal * percent) / 100.0;
            finalTotal = originalTotal - discountAmount;
            
            lblDiscount.setText("Giảm " + percent + "% (-" + FormatterUtils.formatPrice((int)discountAmount) + " VND)");
            lblDiscount.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 13px;");
            
            appliedVoucherCode = code;
            Toast.show("Áp mã thành công!", (Stage) txtVoucher.getScene().getWindow());
        } else {
            Toast.show("Mã không hợp lệ hoặc hết hạn!", (Stage) txtVoucher.getScene().getWindow());
            appliedVoucherCode = "";
            finalTotal = originalTotal;
            lblDiscount.setText("");
        }
        updateTotalUI();
    }
    
    private void updateTotalUI() {
        lblTotal.setText(FormatterUtils.formatPrice((int)finalTotal) + " VND");
    }

    @FXML
    private void handlePlaceOrder(ActionEvent event) {
        // Kiểm tra giỏ hàng rỗng
        if (finalTotal <= 0) {
             Toast.show("Đơn hàng không hợp lệ!", (Stage) btnBack.getScene().getWindow());
             return;
        }
        
        // Kiểm tra địa chỉ (bắt buộc)
        if (currentUser.getAddress() == null || currentUser.getAddress().isEmpty()) {
             Toast.show("Vui lòng cập nhật địa chỉ trong phần Cá nhân!", (Stage) btnBack.getScene().getWindow());
             return;
        }

        // Lấy phương thức thanh toán
        RadioButton selectedRadio = (RadioButton) paymentGroup.getSelectedToggle();
        String paymentMethod = (selectedRadio != null) ? selectedRadio.getText() : "Thanh toán khi nhận hàng (COD)";

        // Tạo đối tượng Order
        Order order = new Order();
        order.setUserId(currentUser.getUserId());
        order.setTotalMoney((int)finalTotal); 
        order.setStatus("Đang xử lý"); 
        order.setPaymentMethod(paymentMethod);
        
        if (!appliedVoucherCode.isEmpty()) {
            order.setNote("Voucher: " + appliedVoucherCode);
        } else {
            order.setNote("");
        }
        
        // Lưu vào Database
        int newOrderId = orderDAO.create(order); 

        if (newOrderId != -1) {
            // 1. Trừ Voucher (nếu có)
            if (!appliedVoucherCode.isEmpty()) voucherDAO.useVoucher(appliedVoucherCode);
            
            // 2. Lưu chi tiết đơn hàng
            List<CartItem> items = cartDAO.getItems();
            for (CartItem item : items) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderId(newOrderId);
                detail.setProductId(item.getProduct().getProductId());
                detail.setQuantity(item.getQuantity());
                detail.setPrice((int)item.getProduct().getPrice());
                detail.setSize(item.getSize());
                orderDetailDAO.add(detail);
            }
            
            // 3. Xóa giỏ hàng
            cartDAO.clearDBCart(currentUser.getUserId()); 
            
            // 4. Thông báo và chuyển trang
            Toast.show("Đặt hàng thành công!", (Stage) btnBack.getScene().getWindow());
            handleBackToHome(); // Chuyển về trang chủ
        } else {
            Toast.show("Lỗi hệ thống, không thể tạo đơn!", (Stage) btnBack.getScene().getWindow());
        }
    }
    
    // --- QUAN TRỌNG: PHẢI CÓ @FXML ĐỂ NÚT BACK HOẠT ĐỘNG ---
    @FXML 
    private void handleBackToHome() { 
        try { 
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/view/main.fxml")); 
            Stage stage = (Stage) btnBack.getScene().getWindow(); 
            stage.setScene(new Scene(loader.load())); 
            stage.setTitle("E-Commerce - Trang chính");
        } catch(Exception e) {
            e.printStackTrace();
        } 
    }
}