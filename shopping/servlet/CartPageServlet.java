package com.shopping.servlet;

import com.shopping.dao.CartDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 购物车页面Servlet
 */
@WebServlet("/cart")
public class CartPageServlet extends HttpServlet {

    private CartDao cartDao = new CartDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // 获取用户ID（优先使用登录用户ID，否则使用匿名ID）
            String userId = getEffectiveUserId(request);
            
            // 获取购物车商品数量
            int cartItemCount = cartDao.getCartItemCount(userId);
            request.setAttribute("cartItemCount", cartItemCount);
            
            // 转发到购物车页面
            request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            // 记录错误日志
            System.err.println("访问购物车页面时发生错误: " + e.getMessage());
            
            // 转发到错误页面或返回错误信息
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * 获取用户ID（优先使用登录用户ID，否则使用匿名ID）
     */
    private String getEffectiveUserId(HttpServletRequest request) {
        // 检查用户是否已登录
        HttpSession session = request.getSession(false);
        if (session != null) {
            com.shopping.model.User user = (com.shopping.model.User) session.getAttribute("currentUser");
            if (user != null) {
                // 如果用户已登录，返回用户ID
                return String.valueOf(user.getId());
            }
        }
        
        // 如果未登录，使用匿名用户ID
        String userId = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("user_id".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }
        
        // 如果没有用户ID，则创建一个临时ID
        if (userId == null || userId.isEmpty()) {
            userId = "temp_" + System.currentTimeMillis();
        }
        
        return userId;
    }
    
    /**
     * 获取用户ID（兼容旧方法）
     */
    private String getUserId(HttpServletRequest request) {
        return getEffectiveUserId(request);
    }
}