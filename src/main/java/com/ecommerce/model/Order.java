package com.ecommerce.model;

import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private int userId;
    private int totalMoney;
    private String status;
    private String paymentMethod;
    private String note;
    private LocalDateTime createdAt;

    // Getters & setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTotalMoney() { return totalMoney; }
    public void setTotalMoney(int totalMoney) { this.totalMoney = totalMoney; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
