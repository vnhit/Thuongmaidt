package com.ecommerce.util;

import java.text.DecimalFormat;

public class FormatterUtils {

    /**
     * Hàm chuyển số tiền (int) thành chuỗi có dấu chấm (String)
     * Ví dụ: 100000 -> "100.000"
     */
    public static String formatPrice(int price) {
        // Mẫu định dạng: ###,### (dùng dấu phẩy theo chuẩn quốc tế trước)
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        
        // Lấy kết quả (ví dụ 100,000) và đổi dấu phẩy thành dấu chấm (kiểu Việt Nam)
        return formatter.format(price).replace(",", ".");
    }
}