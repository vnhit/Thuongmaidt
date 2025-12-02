package com.ecommerce.model;

import java.time.LocalDateTime;

public class Review {
    private int reviewId;
    private String username;    // Tên người review
    private String productName; // Tên sản phẩm được review
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review() { }

    public Review(int reviewId, String username, String productName, int rating, String comment, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.username = username;
        this.productName = productName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}