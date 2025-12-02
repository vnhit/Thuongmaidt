package com.ecommerce.dao;

import com.ecommerce.model.Product;
import com.ecommerce.util.DBConnection; // Đảm bảo import đúng file kết nối của bạn
import java.sql.*;
import java.util.*;

public class ProductDAO {

    // 1. Lấy tất cả sản phẩm
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Lấy sản phẩm có phân trang (Cho trang chủ load nhanh)
    public List<Product> getProductsWithPagination(int limit, int offset) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. Lấy theo Giới tính (Nam/Nữ/KIDs...)
    public List<Product> getProductsByGender(String gender) {
        if (gender == null || gender.equalsIgnoreCase("TOP")) return getAll();
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE gender = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gender);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToProduct(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    // 4. Lấy theo Danh mục (Áo/Quần/Giày...)
    public List<Product> getProductsByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE category = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToProduct(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 5. Lọc nâng cao (Kết hợp Danh mục + Giới tính)
    public List<Product> getFilteredProducts(String category, String gender) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (gender != null && !gender.equalsIgnoreCase("TOP")) {
            sql.append(" AND gender = ?");
            params.add(gender);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    // 6. Tìm kiếm sản phẩm theo tên
    public List<Product> searchProducts(String keyword) {
        List<Product> allProducts = getAll();
        List<Product> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Product p : allProducts) {
            if (p.getName().toLowerCase().contains(lowerKeyword)) result.add(p);
        }
        return result;
    }

    // --- CÁC HÀM QUẢN TRỊ (ADMIN) ---

    public boolean insert(Product p) {
        String sql = "INSERT INTO product (name, description, price, quantity, gender, image, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setInt(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setString(5, p.getGender());
            ps.setString(6, p.getImage());
            ps.setString(7, p.getCategory());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean update(Product p) {
        String sql = "UPDATE product SET name=?, description=?, price=?, quantity=?, gender=?, image=?, category=? WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setInt(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setString(5, p.getGender());
            ps.setString(6, p.getImage());
            ps.setString(7, p.getCategory());
            ps.setInt(8, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int productId) {
        String sql = "DELETE FROM product WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    /**
     * HÀM QUAN TRỌNG: Lấy chi tiết 1 sản phẩm theo ID
     * (Dùng để lấy thông tin Real-time khi bấm vào xem chi tiết)
     */
    public Product getProductById(int id) {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Hàm phụ trợ: Map dữ liệu từ SQL vào Object Java
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getInt("price"));
        p.setQuantity(rs.getInt("quantity")); // Đảm bảo cột này đúng tên trong DB
        p.setGender(rs.getString("gender"));
        p.setImage(rs.getString("image"));
        p.setCategory(rs.getString("category"));
        return p;
    }
}