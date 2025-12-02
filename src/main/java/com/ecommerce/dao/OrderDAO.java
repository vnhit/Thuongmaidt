package com.ecommerce.dao;

import com.ecommerce.model.Order;
import com.ecommerce.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    public int create(Order order) {
        String sql = "INSERT INTO orders(user_id, total_money, status, payment_method, note) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setInt(2, order.getTotalMoney());
            ps.setString(3, order.getStatus());
            ps.setString(4, order.getPaymentMethod());
            ps.setString(5, order.getNote());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    public List<Order> getOrdersByUserId(int userId) {
        return getOrdersGeneric("SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC", userId);
    }

    // --- CÁC HÀM CHO ADMIN ---

    public List<Order> getAllOrders() {
        return getOrdersGeneric("SELECT * FROM orders ORDER BY created_at DESC", -1);
    }

    public boolean updateStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cancelOrder(int orderId) {
        return updateStatus(orderId, "Đã hủy");
    }

    /**
     * HÀM QUAN TRỌNG: Lấy dữ liệu vẽ biểu đồ
     */
    public Map<String, Integer> getRevenueByDate() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT DATE(created_at) as date, SUM(total_money) as total " +
                     "FROM orders WHERE status = 'Đã giao hàng' " +
                     "GROUP BY DATE(created_at) ORDER BY date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getString("date"), rs.getInt("total"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }
    
    private List<Order> getOrdersGeneric(String sql, int userIdParam) {
        List<Order> orderList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userIdParam != -1) ps.setInt(1, userIdParam);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalMoney(rs.getInt("total_money"));
                order.setStatus(rs.getString("status"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setNote(rs.getString("note"));
                order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                orderList.add(order);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return orderList;
    }
}