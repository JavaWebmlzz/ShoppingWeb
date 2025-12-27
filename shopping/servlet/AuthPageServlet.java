package com.shopping.servlet;

import com.shopping.dao.CartDao;
import com.shopping.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 认证页面Servlet（登录、注册、个人中心）
 */
@WebServlet({"/login", "/register", "/profile"})
public class AuthPageServlet extends HttpServlet {

    private CartDao cartDao = new CartDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        String userId = getEffectiveUserId(request);
        
        // 获取购物车商品数量
        int cartItemCount = cartDao.getCartItemCount(userId);
        request.setAttribute("cartItemCount", cartItemCount);
        
        // 检查用户是否已登录
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            request.setAttribute("currentUser", user);
        }
        
        // 根据路径转发到不同页面
        switch (path) {
            case "/login":
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                break;
            case "/register":
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                break;
            case "/profile":
                // 检查用户是否已登录
                if (session == null || session.getAttribute("user") == null) {
                    response.sendRedirect(request.getContextPath() + "/login?error=not_logged_in");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
            com.shopping.model.User user = (com.shopping.model.User) session.getAttribute("user");
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
}