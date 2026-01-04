<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>精品购物商城 - 首页</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar">
        <div class="container">
            <div class="nav-brand">
                <i class="fas fa-shopping-bag"></i>
                <span>精品购物</span>
            </div>

            <div class="nav-search">
                <form action="${pageContext.request.contextPath}/product/list" method="get">
                    <input type="text" name="keyword" placeholder="搜索商品..." class="search-input">
                    <button type="submit" class="search-btn">
                        <i class="fas fa-search"></i>
                    </button>
                </form>
            </div>

            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/index" class="nav-link active">
                    <i class="fas fa-home"></i> 首页
                </a>
                <a href="${pageContext.request.contextPath}/product/list" class="nav-link">
                    <i class="fas fa-list"></i> 全部商品
                </a>
                <a href="${pageContext.request.contextPath}/cart" class="nav-link">
                    <i class="fas fa-shopping-cart"></i> 购物车
                    <c:if test="${cartItemCount > 0}">
                        <span class="cart-count">${cartItemCount}</span>
                    </c:if>
                </a>
                <c:choose>
                    <c:when test="${currentUser != null}">
                        <a href="${pageContext.request.contextPath}/profile" class="nav-link">
                            <i class="fas fa-user"></i> ${currentUser.username}
                        </a>
                        <a href="#" class="nav-link" onclick="logout()">
                            <i class="fas fa-sign-out-alt"></i> 退出
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="nav-link">
                            <i class="fas fa-sign-in-alt"></i> 登录
                        </a>
                        <a href="${pageContext.request.contextPath}/register" class="nav-link">
                            <i class="fas fa-user-plus"></i> 注册
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>

    <!-- 分类导航 -->
    <div class="category-nav">
        <div class="container">
            <div class="category-list">
                <a href="${pageContext.request.contextPath}/product/list" class="category-item">
                    <i class="fas fa-th"></i> 全部分类
                </a>
                <c:forEach items="${categories}" var="category">
                    <a href="${pageContext.request.contextPath}/product/list?categoryId=${category.id}"
                       class="category-item">
                        ${category.name}
                    </a>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- 广告位 -->
    <div class="container" style="margin-top: 20px;">
        <div id="ad-container" class="ad-banner">
            <!-- 广告内容将通过JavaScript动态加载 -->
            <div class="ad-placeholder">
                <i class="fas fa-ad"></i>
                <p>正在加载精准推荐...</p>
            </div>
        </div>
    </div>

    <!-- 商品展示区 -->
    <div class="container">
        <div class="section-title">
            <h2><i class="fas fa-fire"></i> 热门商品</h2>
            <p>精选优质商品，品质保证</p>
        </div>

        <div class="product-grid">
            <c:forEach items="${products}" var="product">
                <div class="product-card">
                    <div class="product-image">
                        <img src="${pageContext.request.contextPath}/${product.imageUrl}"
                             alt="${product.name}"
                             onerror="this.src='${pageContext.request.contextPath}/images/no-image.jpg'">
                        <span class="product-badge">${product.categoryName}</span>
                    </div>
                    <div class="product-info">
                        <h3 class="product-name">${product.name}</h3>
                        <p class="product-desc">${product.description}</p>
                        <div class="product-footer">
                            <div class="product-price">
                                <span class="price-symbol">¥</span>
                                <span class="price-value"><fmt:formatNumber value="${product.price}" pattern="#,##0.00"/></span>
                            </div>
                            <a href="${pageContext.request.contextPath}/product/detail?id=${product.id}"
                               class="btn-detail">
                                查看详情 <i class="fas fa-arrow-right"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- 页脚 -->
    <footer class="footer">
        <div class="container">
            <div class="footer-content">
                <div class="footer-section">
                    <h4><i class="fas fa-shopping-bag"></i> 精品购物</h4>
                    <p>为您提供优质的购物体验</p>
                </div>
                <div class="footer-section">
                    <h4>关于我们</h4>
                    <p>JavaWeb实验课作业项目</p>
                    <p>匿名用户精准广告投放系统</p>
                </div>
                <div class="footer-section">
                    <h4>联系方式</h4>
                    <p><i class="fas fa-envelope"></i> support@shopping.com</p>
                    <p><i class="fas fa-phone"></i> 400-888-8888</p>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2025 精品购物商城. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <!-- 广告加载脚本 -->
    <script src="${pageContext.request.contextPath}/js/ad-loader.js"></script>
    <script>
        function logout() {
            if (confirm('确定要退出登录吗？')) {
                fetch('${pageContext.request.contextPath}/api/user/logout', {
                    method: 'POST'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('退出成功');
                        window.location.href = '${pageContext.request.contextPath}/index';
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('退出时发生错误');
                });
            }
        }
    </script>
</body>
</html>

