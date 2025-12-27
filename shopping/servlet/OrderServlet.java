package com.shopping.servlet;

import com.google.gson.Gson;
import com.shopping.dao.CartDao;
import com.shopping.dao.OrderDao;
import com.shopping.dao.ProductDao;
import com.shopping.model.CartItem;
import com.shopping.model.OrderItem;
import com.shopping.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单Servlet
 */
@WebServlet("/api/order/*")
public class OrderServlet extends HttpServlet {

    private OrderDao orderDao = new OrderDao();
    private CartDao cartDao = new CartDao();
    private ProductDao productDao = new ProductDao();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String userId = getUserId(request);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if ("/create".equals(pathInfo)) {
            // 创建订单（立即购买）
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

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(productId);
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getImageUrl());
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());

            List<OrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem);

            // 创建订单
            String orderNo = orderDao.createOrder(userId, orderItems);
            Map<String, Object> result = new HashMap<>();
            if (orderNo != null) {
                result.put("success", true);
                result.put("message", "订单创建成功");
                result.put("orderNo", orderNo);
            } else {
                result.put("success", false);
                result.put("message", "订单创建失败");
            }
            out.print(gson.toJson(result));
        } else if ("/create-from-cart".equals(pathInfo)) {
            // 从购物车创建订单
            // 获取购物车中的商品
            List<CartItem> cartItems = cartDao.getCartItemsByUserId(userId);
            if (cartItems.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "购物车为空");
                out.print(gson.toJson(result));
                return;
            }

            // 将购物车项转换为订单项
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setProductName(cartItem.getProductName());
                orderItem.setProductImageUrl(cartItem.getProductImageUrl());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());
                orderItems.add(orderItem);
            }

            // 创建订单
            String orderNo = orderDao.createOrder(userId, orderItems);
            Map<String, Object> result = new HashMap<>();
            if (orderNo != null) {
                // 清空购物车
                cartDao.clearCart(userId);
                result.put("success", true);
                result.put("message", "订单创建成功");
                result.put("orderNo", orderNo);
            } else {
                result.put("success", false);
                result.put("message", "订单创建失败");
            }
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
        String userId = getUserId(request);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (pathInfo != null && pathInfo.startsWith("/detail/")) {
            // 获取订单详情
            String orderNo = pathInfo.substring("/detail/".length());
            // 由于当前实现不包含订单详情查询，这里仅返回订单基本信息
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单查询功能待实现");
            out.print(gson.toJson(result));
        } else if ("/list".equals(pathInfo)) {
            // 获取用户订单列表
            List<com.shopping.model.Order> orders = orderDao.getOrdersByUserId(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", orders);
            result.put("total", orders.size());
            out.print(gson.toJson(result));
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "不支持的路径");
            out.print(gson.toJson(result));
        }
    }

    /**
     * 获取用户ID
     */
    private String getUserId(HttpServletRequest request) {
        // 优先从会话中获取登录用户ID
        Object userObj = request.getSession().getAttribute("currentUser");
        if (userObj != null) {
            com.shopping.model.User user = (com.shopping.model.User) userObj;
            return String.valueOf(user.getId());
        }
        
        // 如果用户未登录，从Cookie获取匿名用户ID
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