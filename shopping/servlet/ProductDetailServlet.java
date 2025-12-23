package com.shopping.servlet;

import com.shopping.dao.BrowseHistoryDao;
import com.shopping.dao.CategoryDao;
import com.shopping.dao.ProductDao;
import com.shopping.model.Category;
import com.shopping.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 商品详情Servlet
 */
@WebServlet("/product/detail")
public class ProductDetailServlet extends HttpServlet {

    private ProductDao productDao = new ProductDao();
    private BrowseHistoryDao browseHistoryDao = new BrowseHistoryDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 获取商品ID
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        Integer productId = Integer.parseInt(idStr);

        // 查询商品详情
        Product product = productDao.findById(productId);
        if (product == null) {
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        // 获取或创建用户Cookie（用于匿名追踪）
        String userCookie = getUserCookie(request, response);

        // 保存浏览记录
        browseHistoryDao.saveBrowseHistory(userCookie, product.getCategoryId(), productId);

        request.setAttribute("product", product);
        request.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * 获取或创建用户Cookie
     */
    private String getUserCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("user_id".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 创建新的用户Cookie
        String userId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("user_id", userId);
        cookie.setMaxAge(365 * 24 * 60 * 60); // 1年
        cookie.setPath("/");
        response.addCookie(cookie);

        return userId;
    }
}