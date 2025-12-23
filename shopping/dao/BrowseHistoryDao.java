package com.shopping.dao;

import com.shopping.util.DBUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 浏览记录数据访问层（用于匿名用户追踪）
 */
public class BrowseHistoryDao {

    /**
     * 保存浏览记录
     */
    public void saveBrowseHistory(String userCookie, Integer categoryId, Integer productId) {
        String sql = "INSERT INTO browse_history (user_cookie, category_id, product_id) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userCookie);
            pstmt.setInt(2, categoryId != null ? categoryId : 0);
            pstmt.setInt(3, productId != null ? productId : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 获取用户最常浏览的分类（用于精准广告推荐）
     */
    public Map<Integer, Integer> getUserCategoryPreference(String userCookie) {
        Map<Integer, Integer> categoryCount = new HashMap<>();
        String sql = "SELECT category_id, COUNT(*) as count FROM browse_history " +
                     "WHERE user_cookie = ? AND category_id > 0 " +
                     "GROUP BY category_id ORDER BY count DESC LIMIT 5";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userCookie);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                categoryCount.put(rs.getInt("category_id"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        
        return categoryCount;
    }

    /**
     * 获取用户最感兴趣的分类ID
     */
    public Integer getMostInterestCategory(String userCookie) {
        String sql = "SELECT category_id FROM browse_history " +
                     "WHERE user_cookie = ? AND category_id > 0 " +
                     "GROUP BY category_id ORDER BY COUNT(*) DESC LIMIT 1";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userCookie);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("category_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        
        return null;
    }
}

