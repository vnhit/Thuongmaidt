package com.ecommerce.dao;

import com.ecommerce.model.User;
import com.ecommerce.util.DBConnection;
import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // --- 1. Lấy danh sách User + Đếm số đơn hàng (CHO ADMIN) ---
    public List<User> getAllUsersWithStats() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, COUNT(o.order_id) as order_count " +
                     "FROM user u " +
                     "LEFT JOIN orders o ON u.user_id = o.user_id " +
                     "GROUP BY u.user_id";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPhoneNumber(rs.getString("phone_number"));
                u.setAddress(rs.getString("address"));
                u.setCity(rs.getString("city"));
                u.setGender(rs.getString("gender"));
                
                // Lấy trạng thái khóa và số đơn
                u.setLocked(rs.getBoolean("is_locked"));
                u.setTotalOrders(rs.getInt("order_count"));
                
                list.add(u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // --- 2. Khóa / Mở khóa tài khoản ---
    public boolean setLockStatus(int userId, boolean lock) {
        String sql = "UPDATE user SET is_locked = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, lock);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    // --- 3. Xóa tài khoản ---
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM user WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // --- CÁC HÀM CŨ (LOGIN, REGISTER...) GIỮ NGUYÊN ---
    
    public User login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPass = rs.getString("password");
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setGender(rs.getString("gender"));
                u.setPhoneNumber(rs.getString("phone_number"));
                u.setAddress(rs.getString("address"));
                u.setCity(rs.getString("city"));
                u.setLocked(rs.getBoolean("is_locked")); // Lấy trạng thái khóa

                String hashedInput = hashPassword(password);
                if (storedPass.equals(hashedInput)) return u;
                if (storedPass.equals(password)) { // Tự động update hash nếu pass chưa băm
                    updatePassword(username, hashedInput);
                    return u; 
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean register(User u) {
        String sql = "INSERT INTO user(username, gender, phone_number, email, address, city, password, is_locked) VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String hashed = hashPassword(u.getPassword());
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getGender());
            ps.setString(3, u.getPhoneNumber());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getAddress());
            ps.setString(6, u.getCity());
            ps.setString(7, hashed);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE user SET gender = ?, phone_number = ?, email = ?, address = ?, city = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getGender());
            ps.setString(2, user.getPhoneNumber());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getCity());
            ps.setInt(6, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = login(username, oldPassword); 
        if (user == null) return false;
        try {
            String hashedNew = hashPassword(newPassword);
            updatePassword(username, hashedNew);
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private void updatePassword(String username, String hashedPassword) {
        String sql = "UPDATE user SET password=? WHERE username=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}