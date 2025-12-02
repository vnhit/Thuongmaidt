package com.ecommerce.model;

public class CartItem {
    private int cartId;
    private int productId;
    private String productName;
    private String productImage;
    private double productPrice;
    private int quantity;
    private String size;

    public CartItem() { }

    // Constructor đầy đủ
    public CartItem(int cartId, int productId, String productName, String productImage, double productPrice, int quantity, String size) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.size = size;
    }

    // Constructor tương thích ngược (Sửa lỗi OrderDetailDAO)
    public CartItem(Product product, int quantity, String size) {
        this.productId = product.getProductId();
        this.productName = product.getName();
        this.productImage = product.getImage();
        this.productPrice = product.getPrice();
        this.quantity = quantity;
        this.size = size;
    }

    // --- HÀM CỨU CÁNH: Giúp các Controller cũ không bị lỗi ---
    public Product getProduct() {
        Product p = new Product();
        p.setProductId(this.productId);
        p.setName(this.productName);
        p.setImage(this.productImage);
        p.setPrice((int)this.productPrice);
        return p;
    }
    // ---------------------------------------------------------

    // Getters & Setters chuẩn
    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}