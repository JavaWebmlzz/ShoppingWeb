package com.shopping.servlet;

import com.google.gson.Gson;
import com.shopping.dao.BrowseHistoryDao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 广告推荐API接口（根据用户浏览历史推荐精准广告）
 */
@WebServlet("/api/ad/recommend")
public class AdRecommendServlet extends HttpServlet {

    private BrowseHistoryDao browseHistoryDao = new BrowseHistoryDao();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter out = response.getWriter();

        // 获取用户Cookie
        String userCookie = getUserCookie(request);

        Map<String, Object> result = new HashMap<>();

        if (userCookie != null) {
            // 获取用户最感兴趣的分类
            Integer categoryId = browseHistoryDao.getMostInterestCategory(userCookie);

            if (categoryId != null) {
                result.put("categoryId", categoryId);
                result.put("categoryName", getCategoryName(categoryId));
            }
        }

        out.print(gson.toJson(result));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String getUserCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("user_id".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getCategoryName(Integer categoryId) {
        Map<Integer, String> categoryMap = new HashMap<>();
        categoryMap.put(1, "数码产品");
        categoryMap.put(2, "化妆品");
        categoryMap.put(3, "服装");
        categoryMap.put(4, "食品");
        categoryMap.put(5, "图书");
        categoryMap.put(6, "家电");
        categoryMap.put(7, "运动户外");
        categoryMap.put(8, "母婴");
        return categoryMap.getOrDefault(categoryId, "推荐");
    }
}

