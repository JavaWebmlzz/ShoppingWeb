<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:if test="${not empty currentCategory}">${currentCategory.name} - </c:if>
        <c:if test="${not empty keyword}">搜索: ${keyword} - </c:if>
        商品列表 - 精品购物商城
    </title>
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
                    <input type="text" name="keyword" value="${keyword}" placeholder="搜索商品..." class="search-input">
                    <button type="submit" class="search-btn">
                        <i class="fas fa-search"></i>
                    </button>
                </form>
            </div>

            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/index" class="nav-link">
                    <i class="fas fa-home"></i> 首页
                </a>
                <a href="${pageContext.request.contextPath}/product/list" class="nav-link active">
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
                <a href="${pageContext.request.contextPath}/product/list"
                   class="category-item ${empty currentCategory ? 'active' : ''}">
                    <i class="fas fa-th"></i> 全部分类
                </a>
                <c:forEach items="${categories}" var="category">
                    <a href="${pageContext.request.contextPath}/product/list?categoryId=${category.id}"
                       class="category-item ${currentCategory.id == category.id ? 'active' : ''}">
                        ${category.name}
                    </a>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- 广告位 -->
    <div class="container" style="margin-top: 20px;">
        <div id="ad-container" class="ad-banner">
            <div class="ad-placeholder">
                <i class="fas fa-ad"></i>
                <p>正在加载精准推荐...</p>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/index">
                <i class="fas fa-home"></i> 首页
            </a>
            <span class="separator">/</span>
            <c:if test="${not empty currentCategory}">
                <span class="current">${currentCategory.name}</span>
            </c:if>
            <c:if test="${not empty keyword}">
                <span class="current">搜索: ${keyword}</span>
            </c:if>
            <c:if test="${empty currentCategory && empty keyword}">
                <span class="current">全部商品</span>
            </c:if>
        </div>

        <!-- 商品列表标题 -->
        <div class="section-title">
            <h2>
                <c:if test="${not empty currentCategory}">
                    <i class="fas fa-tag"></i> ${currentCategory.name}
                </c:if>
                <c:if test="${not empty keyword}">
                    <i class="fas fa-search"></i> 搜索结果
                </c:if>
                <c:if test="${empty currentCategory && empty keyword}">
                    <i class="fas fa-list"></i> 全部商品
                </c:if>
            </h2>
            <p>共找到 ${products.size()} 件商品</p>
        </div>

        <!-- 商品网格 -->
        <div class="product-grid">
            <c:choose>
                <c:when test="${empty products}">
                    <div class="empty-result">
                        <i class="fas fa-inbox"></i>
                        <p>暂无商品</p>
                    </div>
                </c:when>
                <c:otherwise>
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
                                    <div class="product-actions">
                                        <a href="${pageContext.request.contextPath}/product/detail?id=${product.id}"
                                           class="btn-detail">
                                            查看详情 <i class="fas fa-arrow-right"></i>
                                        </a>
                                        <button class="btn btn-primary btn-sm" 
                                                onclick="addToCart(${product.id}, 1)" 
                                                style="margin-top: 10px;">
                                            <i class="fas fa-shopping-cart"></i> 加入购物车
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
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

    <script src="${pageContext.request.contextPath}/js/ad-loader.js"></script>
    <script>
        // 添加到购物车
        function addToCart(productId, quantity) {
            fetch('${pageContext.request.contextPath}/api/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'productId=' + productId + '&quantity=' + quantity
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('商品已添加到购物车！');
                } else {
                    alert('添加失败：' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('添加到购物车时发生错误');
            });
        }
    </script>
</body>
</html>

