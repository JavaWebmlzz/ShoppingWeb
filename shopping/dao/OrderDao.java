package com.shopping.dao;

import com.shopping.model.Order;
import com.shopping.model.OrderItem;
import com.shopping.model.Product;
import com.shopping.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单数据访问层
 */
public class OrderDao {

    /**
     * 创建订单
     */
    public String createOrder(String userId, List<OrderItem> orderItems) {
        String orderNo = "ORDER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String orderSql = "INSERT INTO orders (order_no, user_id, total_amount, status) VALUES (?, ?, ?, ?)";
        String orderItemSql = "INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        String updateStockSql = "UPDATE product SET stock = stock - ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement orderPstmt = null;
        PreparedStatement itemPstmt = null;
        PreparedStatement stockPstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 插入订单
            orderPstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderItem item : orderItems) {
                totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
            orderPstmt.setString(1, orderNo);
            orderPstmt.setString(2, userId);
            orderPstmt.setBigDecimal(3, totalAmount);
            orderPstmt.setString(4, "PENDING");
            orderPstmt.executeUpdate();

            // 获取生成的订单ID
            rs = orderPstmt.getGeneratedKeys();
            Integer orderId = null;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            if (orderId == null) {
                conn.rollback();
                return null;
            }

            // 插入订单项
            itemPstmt = conn.prepareStatement(orderItemSql);
            for (OrderItem item : orderItems) {
                itemPstmt.setInt(1, orderId);
                itemPstmt.setInt(2, item.getProductId());
                itemPstmt.setInt(3, item.getQuantity());
                itemPstmt.setBigDecimal(4, item.getPrice());
                itemPstmt.addBatch();
            }
            itemPstmt.executeBatch();

            // 更新库存
            stockPstmt = conn.prepareStatement(updateStockSql);
            for (OrderItem item : orderItems) {
                stockPstmt.setInt(1, item.getQuantity());
                stockPstmt.setInt(2, item.getProductId());
                stockPstmt.addBatch();
            }
            stockPstmt.executeBatch();

            conn.commit(); // 提交事务
            return orderNo;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBUtil.close(rs, orderPstmt, itemPstmt, stockPstmt, conn);
        }
    }

    /**
     * 根据订单号获取订单
     */
    public Order getOrderByOrderNo(String orderNo) {
        String sql = "SELECT * FROM orders WHERE order_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orderNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderNo(rs.getString("order_no"));
                order.setUserId(rs.getString("user_id"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setCreateTime(rs.getTimestamp("create_time"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return null;
    }

    /**
     * 获取用户的所有订单
     */
    public List<Order> getOrdersByUserId(String userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY create_time DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderNo(rs.getString("order_no"));
                order.setUserId(rs.getString("user_id"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setCreateTime(rs.getTimestamp("create_time"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return orders;
    }
}