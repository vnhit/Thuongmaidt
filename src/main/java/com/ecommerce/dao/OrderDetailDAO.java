package com.ecommerce.dao;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.OrderDetail;
import com.ecommerce.model.Product;
import com.ecommerce.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO {

    /**
     * ĐÃ CẬP NHẬT: Lưu thêm Size
     */
    public boolean add(OrderDetail detail) {
        String sql = "INSERT INTO order_detail(order_id, product_id, quantity, price, size) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, detail.getOrderId());
            ps.setInt(2, detail.getProductId());
            ps.setInt(3, detail.getQuantity());
            ps.setInt(4, detail.getPrice());
            ps.setString(5, detail.getSize()); // Lưu size
            ps.executeUpdate();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ĐÃ CẬP NHẬT: Lấy thêm Size
     */
    public List<OrderDetail> getAllDetailsByUserId(int userId) {
        List<OrderDetail> detailsList = new ArrayList<>();
        String sql = "SELECT od.* FROM order_detail od JOIN orders o ON od.order_id = o.order_id WHERE o.user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setId(rs.getInt("id"));
                detail.setOrderId(rs.getInt("order_id"));
                detail.setProductId(rs.getInt("product_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setPrice(rs.getInt("price"));
                detail.setSize(rs.getString("size")); // Lấy size
                detailsList.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detailsList;
    }
    
    /**
     * ĐÃ SỬA LỖI: Dùng ALIAS để phân biệt số lượng mua và số lượng kho
     */
    public List<CartItem> getItemsByOrderId(int orderId) {
        List<CartItem> items = new ArrayList<>();
        
        // QUAN TRỌNG: od.quantity AS bought_quantity để không bị nhầm với p.quantity (kho)
        String sql = "SELECT p.*, od.quantity AS bought_quantity, od.price AS sold_price, od.size AS sold_size " +
                     "FROM order_detail od " +
                     "JOIN product p ON od.product_id = p.product_id " +
                     "WHERE od.order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setImage(rs.getString("image"));
                p.setPrice(rs.getInt("sold_price")); 
                
                int quantity = rs.getInt("bought_quantity"); // Lấy đúng số lượng đã đặt
                String size = rs.getString("sold_size");     // Lấy đúng size đã đặt
                
                items.add(new CartItem(p, quantity, size)); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}