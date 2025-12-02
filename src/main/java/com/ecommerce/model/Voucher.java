package com.ecommerce.model;

import java.sql.Date; // Dùng java.sql.Date để khớp với CSDL

public class Voucher {
    private String code;
    private int discountPercent;
    private int quantity;
    private Date expiryDate;

    public Voucher() { }

    public Voucher(String code, int discountPercent, int quantity, Date expiryDate) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
}