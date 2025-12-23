package com.shopping.dao;

import com.shopping.model.Product;
import com.shopping.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品数据访问层
 */
public class ProductDao {

    /**
     * 查询所有商品
     */
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM product p " +
                     "LEFT JOIN category c ON p.category_id = c.id " +
                     "ORDER BY p.create_time DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = extractProduct(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return products;
    }

    /**
     * 根据分类查询商品
     */
    public List<Product> findByCategory(Integer categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM product p " +
                     "LEFT JOIN category c ON p.category_id = c.id " +
                     "WHERE p.category_id = ? ORDER BY p.create_time DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, categoryId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = extractProduct(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return products;
    }

    /**
     * 根据ID查询商品
     */
    public Product findById(Integer id) {
        String sql = "SELECT p.*, c.name as category_name FROM product p " +
                     "LEFT JOIN category c ON p.category_id = c.id " +
                     "WHERE p.id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return null;
    }

    /**
     * 搜索商品
     */
    public List<Product> search(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM product p " +
                     "LEFT JOIN category c ON p.category_id = c.id " +
                     "WHERE p.name LIKE ? OR p.description LIKE ? " +
                     "ORDER BY p.create_time DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = extractProduct(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return products;
    }

    /**
     * 从ResultSet提取Product对象
     */
    private Product extractProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStock(rs.getInt("stock"));
        product.setDescription(rs.getString("description"));
        product.setImageUrl(rs.getString("image_url"));
        product.setCreateTime(rs.getTimestamp("create_time"));
        return product;
    }
}

