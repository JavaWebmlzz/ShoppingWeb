<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} - 商品详情 - 精品购物商城</title>
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
                <a href="${pageContext.request.contextPath}/index" class="nav-link">
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

    <div class="container">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/index">
                <i class="fas fa-home"></i> 首页
            </a>
            <span class="separator">/</span>
            <a href="${pageContext.request.contextPath}/product/list?categoryId=${product.categoryId}">
                ${product.categoryName}
            </a>
            <span class="separator">/</span>
            <span class="current">${product.name}</span>
        </div>

        <!-- 商品详情 -->
        <div class="product-detail">
            <div class="detail-image">
                <img src="${pageContext.request.contextPath}/${product.imageUrl}"
                     alt="${product.name}"
                     onerror="this.src='${pageContext.request.contextPath}/images/no-image.jpg'">
            </div>

            <div class="detail-info">
                <h1 class="detail-title">${product.name}</h1>

                <div class="detail-category">
                    <i class="fas fa-tag"></i>
                    <span>分类：</span>
                    <a href="${pageContext.request.contextPath}/product/list?categoryId=${product.categoryId}">
                        ${product.categoryName}
                    </a>
                </div>

                <div class="detail-price-box">
                    <div class="price-label">价格</div>
                    <div class="price-main">
                        <span class="price-symbol">¥</span>
                        <span class="price-value"><fmt:formatNumber value="${product.price}" pattern="#,##0.00"/></span>
                    </div>
                </div>

                <div class="detail-stock">
                    <i class="fas fa-box"></i>
                    <span>库存：${product.stock} 件</span>
                </div>

                <div class="detail-description">
                    <h3><i class="fas fa-info-circle"></i> 商品描述</h3>
                    <p>${product.description}</p>
                </div>

                <div class="detail-actions">
                    <button class="btn btn-primary" onclick="addToCart(${product.id}, 1)">
                        <i class="fas fa-shopping-cart"></i> 加入购物车
                    </button>
                    <button class="btn btn-danger" onclick="buyNow(${product.id}, 1)">
                        <i class="fas fa-bolt"></i> 立即购买
                    </button>
                </div>
            </div>
        </div>

        <!-- 广告位（侧边栏广告） -->
        <div class="sidebar-ad">
            <div id="ad-container" class="ad-banner">
                <div class="ad-placeholder">
                    <i class="fas fa-ad"></i>
                    <p>相关推荐</p>
                </div>
            </div>
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

        // 立即购买
        function buyNow(productId, quantity) {
            if (confirm('确定要立即购买吗？')) {
                fetch('${pageContext.request.contextPath}/api/order/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'productId=' + productId + '&quantity=' + quantity
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('订单创建成功！订单号：' + data.orderNo);
                    } else {
                        alert('购买失败：' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('立即购买时发生错误');
                });
            }
        }
    </script>
</body>
</html>

