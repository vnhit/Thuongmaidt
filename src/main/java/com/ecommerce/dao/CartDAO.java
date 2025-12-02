package com.ecommerce.dao;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.util.DBConnection;
import com.ecommerce.util.SessionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    private static CartDAO instance;

    public static CartDAO getInstance() {
        if (instance == null) instance = new CartDAO();
        return instance;
    }

    // 1. Lấy danh sách giỏ hàng (Đã sửa tên bảng thành user_cart)
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> list = new ArrayList<>();
        // SỬA: FROM user_cart, SELECT cart_item_id
        String sql = "SELECT c.cart_item_id, c.product_id, p.name, p.image, p.price, c.quantity, c.size " +
                     "FROM user_cart c " +
                     "JOIN product p ON c.product_id = p.product_id " + 
                     "WHERE c.user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(new CartItem(
                    rs.getInt("cart_item_id"), // SỬA: Lấy đúng cột cart_item_id
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("image"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("size")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm vào giỏ (Đã sửa tên bảng thành user_cart)
    public void addToCart(Product product, int quantity, String size) {
        if (SessionManager.getCurrentUser() == null) return;
        int userId = SessionManager.getCurrentUser().getUserId();

        // SỬA: FROM user_cart
        String checkSql = "SELECT quantity FROM user_cart WHERE user_id = ? AND product_id = ? AND size = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, userId);
            checkPs.setInt(2, product.getProductId());
            checkPs.setString(3, size);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // SỬA: UPDATE user_cart
                int oldQty = rs.getInt("quantity");
                String updateSql = "UPDATE user_cart SET quantity = ? WHERE user_id = ? AND product_id = ? AND size = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setInt(1, oldQty + quantity);
                updatePs.setInt(2, userId);
                updatePs.setInt(3, product.getProductId());
                updatePs.setString(4, size);
                updatePs.executeUpdate();
            } else {
                // SỬA: INSERT INTO user_cart
                String insertSql = "INSERT INTO user_cart (user_id, product_id, quantity, size) VALUES (?, ?, ?, ?)";
                PreparedStatement insertPs = conn.prepareStatement(insertSql);
                insertPs.setInt(1, userId);
                insertPs.setInt(2, product.getProductId());
                insertPs.setInt(3, quantity);
                insertPs.setString(4, size);
                insertPs.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. Xóa 1 món (Đã sửa cart_item_id)
    public void removeCartItem(int cartId) {
        // SỬA: DELETE FROM user_cart WHERE cart_item_id
        String sql = "DELETE FROM user_cart WHERE cart_item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    // 3.5 Xóa món theo Object
    public void removeFromCart(CartItem item) {
        removeCartItem(item.getCartId());
    }

    // 4. Xóa hết giỏ (Đã sửa user_cart)
    public void clearCart(int userId) {
        String sql = "DELETE FROM user_cart WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- CÁC HÀM HỖ TRỢ ---
    public List<CartItem> getItems() {
        try { return getCartItems(SessionManager.getCurrentUser().getUserId()); } 
        catch (Exception e) { return new ArrayList<>(); }
    }

    public int getTotal() {
        List<CartItem> items = getItems();
        int total = 0;
        for (CartItem item : items) {
            total += item.getProductPrice() * item.getQuantity();
        }
        return total;
    }

    public void clearMemoryCart() {} 
    public void loadCartFromDB(int userId) {} 
    public void clearDBCart(int userId) { clearCart(userId); }
}