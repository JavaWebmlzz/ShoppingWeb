package com.shopping.servlet;

import com.google.gson.Gson;
import com.shopping.dao.*;
import com.shopping.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关操作的Servlet
 */
@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {

    private UserDao userDao = new UserDao();
    private CartDao cartDao = new CartDao();
    private BrowseHistoryDao browseHistoryDao = new BrowseHistoryDao();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if ("/login".equals(pathInfo)) {
            // 用户登录
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            if (username == null || password == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户名和密码不能为空");
                out.print(gson.toJson(result));
                return;
            }

            boolean isValid = userDao.validateUser(username, password);
            if (isValid) {
                User user = userDao.findByUsername(username);
                
                // 将用户信息存储到session中
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // 迁移匿名购物车数据到登录用户
                String anonymousUserId = getAnonymousUserId(request);
                if (anonymousUserId != null && !anonymousUserId.startsWith("temp_")) {
                    // 将匿名用户的数据迁移到当前登录用户
                    migrateUserData(anonymousUserId, user.getId().toString());
                }
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "登录成功");
                result.put("user", user);
                out.print(gson.toJson(result));
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                out.print(gson.toJson(result));
            }
        } else if ("/register".equals(pathInfo)) {
            // 用户注册
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");

            if (username == null || password == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户名和密码不能为空");
                out.print(gson.toJson(result));
                return;
            }

            // 检查用户名是否已存在
            if (userDao.findByUsername(username) != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户名已存在");
                out.print(gson.toJson(result));
                return;
            }

            // 创建新用户
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setPhone(phone);

            boolean success = userDao.createUser(user);
            if (success) {
                // 将用户信息存储到session中
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // 迁移匿名购物车数据到新注册用户
                String anonymousUserId = getAnonymousUserId(request);
                if (anonymousUserId != null && !anonymousUserId.startsWith("temp_")) {
                    // 将匿名用户的数据迁移到当前注册用户
                    migrateUserData(anonymousUserId, user.getId().toString());
                }
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "注册成功");
                result.put("user", user);
                out.print(gson.toJson(result));
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "注册失败");
                out.print(gson.toJson(result));
            }
        } else if ("/logout".equals(pathInfo)) {
            // 用户登出
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登出成功");
            out.print(gson.toJson(result));
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "不支持的路径");
            out.print(gson.toJson(result));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if ("/profile".equals(pathInfo)) {
            // 获取用户信息
            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("user", user);
                    out.print(gson.toJson(result));
                    return;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户未登录");
            out.print(gson.toJson(result));
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "不支持的路径");
            out.print(gson.toJson(result));
        }
    }

    /**
     * 获取匿名用户ID
     */
    private String getAnonymousUserId(HttpServletRequest request) {
        String userId = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("user_id".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }
        return userId;
    }

    /**
     * 迁移用户数据（购物车、浏览历史等）
     */
    private void migrateUserData(String fromUserId, String toUserId) {
        // 迁移购物车数据
        cartDao.migrateCartData(fromUserId, toUserId);
        
        // 迁移浏览历史数据
        browseHistoryDao.migrateBrowseHistory(fromUserId, toUserId);
    }
}