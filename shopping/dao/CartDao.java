package com.shopping.dao;

import com.shopping.model.CartItem;
import com.shopping.model.Product;
import com.shopping.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车数据访问层
 */
public class CartDao {

    /**
     * 添加商品到购物车
     */
    public boolean addToCart(String userId, Integer productId, Integer quantity) {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, quantity);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 更新购物车中商品数量
     */
    public boolean updateQuantity(String userId, Integer productId, Integer quantity) {
        String sql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setString(2, userId);
            pstmt.setInt(3, productId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 从购物车删除商品
     */
    public boolean removeFromCart(String userId, Integer productId) {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, productId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 获取用户购物车中的所有商品
     */
    public List<CartItem> getCartItemsByUserId(String userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT c.id, c.user_id, c.product_id, c.quantity, c.create_time, " +
                     "p.name as product_name, p.image_url, p.price " +
                     "FROM cart c " +
                     "LEFT JOIN product p ON c.product_id = p.id " +
                     "WHERE c.user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                // 检查商品是否存在（如果商品被删除，p.name将为NULL）
                String productName = rs.getString("product_name");
                if (productName == null) {
                    // 商品已被删除，跳过这个购物车项目
                    continue;
                }
                
                CartItem cartItem = new CartItem();
                cartItem.setId(rs.getInt("id"));
                cartItem.setUserId(rs.getString("user_id"));
                cartItem.setProductId(rs.getInt("product_id"));
                cartItem.setProductName(productName);
                cartItem.setProductImageUrl(rs.getString("image_url"));
                
                // 安全获取价格，处理可能的空值
                BigDecimal price = rs.getBigDecimal("price");
                if (price == null) {
                    price = BigDecimal.ZERO;
                }
                cartItem.setPrice(price);
                
                // 安全获取数量，处理可能的空值
                int quantity = rs.getInt("quantity");
                // 检查wasNull()以确定数据库中是否为NULL
                if (rs.wasNull()) {
                    quantity = 1; // 默认数量为1
                }
                cartItem.setQuantity(quantity);
                
                // 计算小计
                BigDecimal subtotal = price.multiply(new BigDecimal(quantity));
                cartItem.setSubtotal(subtotal);
                
                cartItems.add(cartItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 重新抛出异常，以便上层可以处理
            throw new RuntimeException("获取购物车商品失败: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return cartItems;
    }

    /**
     * 清空用户购物车
     */
    public boolean clearCart(String userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 获取用户购物车商品总数
     */
    public int getCartItemCount(String userId) {
        String sql = "SELECT SUM(quantity) FROM cart WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                // 检查是否为NULL（使用wasNull()方法）
                if (rs.wasNull()) {
                    return 0;
                }
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取购物车商品数量失败: " + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return 0;
    }

    /**
     * 迁移购物车数据
     */
    public void migrateCartData(String fromUserId, String toUserId) {
        // 将原用户购物车中的商品合并到目标用户购物车
        String selectSql = "SELECT product_id, quantity FROM cart WHERE user_id = ?";
        String insertSql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE quantity = cart.quantity + ?";
        String deleteSql = "DELETE FROM cart WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement selectPstmt = null;
        PreparedStatement insertPstmt = null;
        PreparedStatement deletePstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 查询原用户购物车数据
            selectPstmt = conn.prepareStatement(selectSql);
            selectPstmt.setString(1, fromUserId);
            rs = selectPstmt.executeQuery();

            // 插入到目标用户购物车
            insertPstmt = conn.prepareStatement(insertSql);
            while (rs.next()) {
                insertPstmt.setString(1, toUserId);
                insertPstmt.setInt(2, rs.getInt("product_id"));
                int quantity = rs.getInt("quantity");
                insertPstmt.setInt(3, quantity);
                insertPstmt.setInt(4, quantity); // 用于更新时的增加数量
                insertPstmt.addBatch();
            }
            insertPstmt.executeBatch();

            // 删除原用户购物车数据
            deletePstmt = conn.prepareStatement(deleteSql);
            deletePstmt.setString(1, fromUserId);
            deletePstmt.executeUpdate();

            conn.commit(); // 提交事务
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBUtil.close(rs, selectPstmt, insertPstmt, deletePstmt, conn);
        }
    }
}