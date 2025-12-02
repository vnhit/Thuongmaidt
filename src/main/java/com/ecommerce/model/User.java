package com.ecommerce.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String gender;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String password;
    private LocalDateTime createdAt;
    
    // --- CÁC TRƯỜNG MỚI CHO QUẢN LÝ ---
    private boolean isLocked; // Trạng thái khóa
    private int totalOrders;  // Tổng số đơn hàng (chỉ để hiển thị)

    // Getters & Setters Cũ
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // --- GETTER & SETTER CHO TRƯỜNG MỚI ---
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    
    // Helper để hiển thị lên bảng Admin
    public String getStatusStr() {
        return isLocked ? "Đã khóa" : "Hoạt động";
    }
}