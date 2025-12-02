package com.ecommerce.dao;

import com.ecommerce.model.Product;
import com.ecommerce.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDAO {

    /**
     * Kiểm tra xem sản phẩm đã được yêu thích chưa
     */
    public boolean isFavorite(int userId, int productId) {
        String sql = "SELECT * FROM user_favorites WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // Trả về true nếu tìm thấy
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm vào yêu thích
     */
    public boolean addFavorite(int userId, int productId) {
        if (isFavorite(userId, productId)) {
            return true; // Đã có rồi
        }
        String sql = "INSERT INTO user_favorites (user_id, product_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa khỏi yêu thích
     */
    public boolean removeFavorite(int userId, int productId) {
        String sql = "DELETE FROM user_favorites WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tất cả sản phẩm yêu thích của 1 người dùng
     */
    public List<Product> getFavoriteProductsByUserId(int userId) {
        List<Product> list = new ArrayList<>();
        // Dùng JOIN để lấy thông tin sản phẩm từ bảng 'product'
        String sql = "SELECT p.* FROM product p " +
                     "JOIN user_favorites f ON p.product_id = f.product_id " +
                     "WHERE f.user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getInt("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setGender(rs.getString("gender"));
                p.setImage(rs.getString("image"));
                p.setCategory(rs.getString("category"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}