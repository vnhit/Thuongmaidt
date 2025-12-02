package com.ecommerce.dao;

import com.ecommerce.model.Review; // <-- Import Model mới
import com.ecommerce.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    // --- 1. Hàm cho ADMIN: Lấy tất cả đánh giá ---
    public List<Review> getAllReviews() {
        List<Review> list = new ArrayList<>();
        // JOIN 3 bảng để lấy tên User và tên Product
        String sql = "SELECT r.review_id, r.rating, r.comment, r.created_at, " +
                     "u.username, p.name AS product_name " +
                     "FROM reviews r " +
                     "JOIN user u ON r.user_id = u.user_id " +
                     "JOIN product p ON r.product_id = p.product_id " +
                     "ORDER BY r.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Review r = new Review();
                r.setReviewId(rs.getInt("review_id"));
                r.setUsername(rs.getString("username"));
                r.setProductName(rs.getString("product_name"));
                r.setRating(rs.getInt("rating"));
                r.setComment(rs.getString("comment"));
                r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(r);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // --- 2. Hàm cho ADMIN: Xóa đánh giá ---
    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // --- 3. Hàm cũ (Get list string cho trang chi tiết) - GIỮ NGUYÊN ---
    public List<String> getReviews(int productId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT u.username, r.rating, r.comment FROM reviews r JOIN user u ON r.user_id = u.user_id WHERE product_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("username") + " (" + rs.getInt("rating") + " sao): " + rs.getString("comment"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // --- 4. Hàm cũ (Add review) - GIỮ NGUYÊN ---
    public void addReview(int userId, int productId, int rating, String comment) {
        String sql = "INSERT INTO reviews (user_id, product_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, productId); ps.setInt(3, rating); ps.setString(4, comment);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}