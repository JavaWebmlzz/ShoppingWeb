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
 * 首页Servlet
 */
@WebServlet(urlPatterns = {"/index", "/"})
public class IndexServlet extends HttpServlet {

    private ProductDao productDao = new ProductDao();
    private CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 获取所有分类
        List<Category> categories = categoryDao.findAll();
        request.setAttribute("categories", categories);

        // 获取所有商品
        List<Product> products = productDao.findAll();
        request.setAttribute("products", products);

        // 转发到首页
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

