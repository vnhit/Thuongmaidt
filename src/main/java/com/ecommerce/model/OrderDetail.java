package com.ecommerce.model;

public class OrderDetail {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private int price;
    private String size; // <-- THÊM MỚI

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    
    public String getSize() { return size; } // <-- THÊM
    public void setSize(String size) { this.size = size; } // <-- THÊM
}