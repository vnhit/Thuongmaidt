package com.ecommerce.util;

import com.ecommerce.model.User;

/**
 * Lớp static đơn giản để "ghi nhớ" người dùng đã đăng nhập
 */
public class SessionManager {

    private static User currentUser;

    // Lưu người dùng khi đăng nhập
    public static void login(User user) {
        currentUser = user;
    }

    // Lấy người dùng hiện tại
    public static User getCurrentUser() {
        return currentUser;
    }

    // Xóa người dùng khi đăng xuất
    public static void logout() {
        currentUser = null;
    }

    // Kiểm tra xem đã đăng nhập chưa
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}