package com.shopping.servlet;

import com.shopping.dao.CategoryDao;
import com.shopping.dao.ProductDao;
import com.shopping.model.Category;
import com.shopping.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 商品列表Servlet（按分类）
 */
@WebServlet("/product/list")
public class ProductListServlet extends HttpServlet {

    private ProductDao productDao = new ProductDao();
    private CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String categoryIdStr = request.getParameter("categoryId");
        String keyword = request.getParameter("keyword");

        // 获取所有分类
        List<Category> categories = categoryDao.findAll();
        request.setAttribute("categories", categories);

        List<Product> products;

        // 搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productDao.search(keyword);
            request.setAttribute("keyword", keyword);
        }
        // 按分类查询
        else if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            products = productDao.findByCategory(categoryId);

            Category category = categoryDao.findById(categoryId);
            request.setAttribute("currentCategory", category);
        }
        // 查询所有
        else {
            products = productDao.findAll();
        }

        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/views/product-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

