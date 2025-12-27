package com.shopping.servlet;

import com.google.gson.Gson;
import com.shopping.dao.CartDao;
import com.shopping.dao.ProductDao;
import com.shopping.model.CartItem;
import com.shopping.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车Servlet
 */
@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {

    private CartDao cartDao = new CartDao();
    private ProductDao productDao = new ProductDao();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String userId = getUserId(request);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if ("/items".equals(pathInfo)) {
            // 获取购物车商品列表
            try {
                List<CartItem> cartItems = cartDao.getCartItemsByUserId(userId);
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("data", cartItems);
                result.put("totalItems", cartItems != null ? cartItems.size() : 0);
                out.print(gson.toJson(result));
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "获取购物车商品失败: " + e.getMessage());
                out.print(gson.toJson(result));
            }
        } else if ("/count".equals(pathInfo)) {
            // 获取购物车商品总数
            try {
                int count = cartDao.getCartItemCount(userId);
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("count", count);
                out.print(gson.toJson(result));
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "获取购物车商品数量失败: " + e.getMessage());
                out.print(gson.toJson(result));
            }
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "不支持的路径");
            out.print(gson.toJson(result));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String userId = getUserId(request);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if ("/add".equals(pathInfo)) {
            // 添加商品到购物车
            String productIdStr = request.getParameter("productId");
            String quantityStr = request.getParameter("quantity");

            if (productIdStr == null || quantityStr == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "参数不完整");
                out.print(gson.toJson(result));
                return;
            }

            Integer productId = Integer.parseInt(productIdStr);
            Integer quantity = Integer.parseInt(quantityStr);

            // 验证商品是否存在
            Product product = productDao.findById(productId);
            if (product == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "商品不存在");
                out.print(gson.toJson(result));
                return;
            }

            // 验证库存
            if (quantity > product.getStock()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "库存不足");
                out.print(gson.toJson(result));
                return;
            }

            boolean success = cartDao.addToCart(userId, productId, quantity);
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "添加成功" : "添加失败");
            out.print(gson.toJson(result));
        } else if ("/update".equals(pathInfo)) {
            // 更新购物车商品数量
            String productIdStr = request.getParameter("productId");
            String quantityStr = request.getParameter("quantity");

            if (productIdStr == null || quantityStr == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "参数不完整");
                out.print(gson.toJson(result));
                return;
            }

            Integer productId = Integer.parseInt(productIdStr);
            Integer quantity = Integer.parseInt(quantityStr);

            // 验证商品是否存在
            Product product = productDao.findById(productId);
            if (product == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "商品不存在");
                out.print(gson.toJson(result));
                return;
            }

            // 验证库存
            if (quantity > product.getStock()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "库存不足");
                out.print(gson.toJson(result));
                return;
            }

            boolean success = cartDao.updateQuantity(userId, productId, quantity);
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "更新成功" : "更新失败");
            out.print(gson.toJson(result));
        } else if ("/remove".equals(pathInfo)) {
            // 从购物车删除商品
            String productIdStr = request.getParameter("productId");

            if (productIdStr == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "参数不完整");
                out.print(gson.toJson(result));
                return;
            }

            Integer productId = Integer.parseInt(productIdStr);
            boolean success = cartDao.removeFromCart(userId, productId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "删除成功" : "删除失败");
            out.print(gson.toJson(result));
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "不支持的路径");
            out.print(gson.toJson(result));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String userId = getUserId(request);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if ("/clear".equals(pathInfo)) {
            // 清空购物车
            boolean success = cartDao.clearCart(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "清空成功" : "清空失败");
            out.print(gson.toJson(result));
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "不支持的路径");
            out.print(gson.toJson(result));
        }
    }

    /**
     * 获取用户ID（优先使用登录用户ID，否则使用匿名ID）
     */
    private String getUserId(HttpServletRequest request) {
        // 检查用户是否已登录
        Object userObj = request.getSession().getAttribute("currentUser");
        if (userObj != null) {
            com.shopping.model.User user = (com.shopping.model.User) userObj;
            return String.valueOf(user.getId());
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