package com.ecommerce.dao;

import com.ecommerce.model.Voucher; // Import Model mới
import com.ecommerce.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {

    // 1. Lấy tất cả Voucher (Cho Admin)
    public List<Voucher> getAllVouchers() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers ORDER BY expiry_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Voucher(
                    rs.getString("code"),
                    rs.getInt("discount_percent"),
                    rs.getInt("quantity"),
                    rs.getDate("expiry_date")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Thêm Voucher mới
    public boolean insert(Voucher v) {
        String sql = "INSERT INTO vouchers (code, discount_percent, quantity, expiry_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getCode().toUpperCase()); // Mã luôn viết hoa
            ps.setInt(2, v.getDiscountPercent());
            ps.setInt(3, v.getQuantity());
            ps.setDate(4, v.getExpiryDate());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 3. Xóa Voucher
    public boolean delete(String code) {
        String sql = "DELETE FROM vouchers WHERE code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 4. (Giữ nguyên) Check mã giảm giá cho User
    public int getDiscountPercent(String code) {
        String sql = "SELECT discount_percent, quantity FROM vouchers WHERE code = ? AND quantity > 0 AND expiry_date >= CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("discount_percent");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    
    // 5. (Giữ nguyên) Trừ số lượng khi dùng
    public void useVoucher(String code) {
        String sql = "UPDATE vouchers SET quantity = quantity - 1 WHERE code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}