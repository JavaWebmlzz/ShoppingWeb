<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>购物车 - 精品购物商城</title>
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
                <a href="${pageContext.request.contextPath}/cart" class="nav-link active">
                    <i class="fas fa-shopping-cart"></i> 购物车
                    <c:if test="${cartItemCount > 0}">
                        <span class="cart-count">${cartItemCount}</span>
                    </c:if>
                </a>
                
                <!-- 用户导航 -->
                <c:choose>
                    <c:when test="${sessionScope.currentUser != null}">
                        <div class="user-menu">
                            <a href="${pageContext.request.contextPath}/profile" class="nav-link user-link">
                                <i class="fas fa-user"></i> ${sessionScope.currentUser.username}
                            </a>
                            <a href="#" onclick="logout()" class="nav-link">
                                <i class="fas fa-sign-out-alt"></i> 退出
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="auth-menu">
                            <a href="${pageContext.request.contextPath}/login" class="nav-link">
                                <i class="fas fa-sign-in-alt"></i> 登录
                            </a>
                            <a href="${pageContext.request.contextPath}/register" class="nav-link">
                                <i class="fas fa-user-plus"></i> 注册
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>

    <script>
        function logout() {
            if (confirm('确定要退出登录吗？')) {
                fetch('${pageContext.request.contextPath}/api/user/logout', {
                    method: 'POST',
                    credentials: 'same-origin'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        window.location.href = '${pageContext.request.contextPath}/index';
                    } else {
                        alert('退出登录失败：' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('退出登录时发生错误');
                });
            }
        }
    </script>

    <div class="container">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/index">
                <i class="fas fa-home"></i> 首页
            </a>
            <span class="separator">/</span>
            <span class="current">购物车</span>
        </div>

        <h2><i class="fas fa-shopping-cart"></i> 我的购物车</h2>

        <!-- 购物车内容 -->
        <div id="cart-container">
            <div class="cart-placeholder">
                <i class="fas fa-spinner fa-spin"></i> 正在加载购物车...
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
        // 加载购物车内容
        function loadCart() {
            fetch('${pageContext.request.contextPath}/api/cart/items')
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        displayCartItems(data.data);
                    } else {
                        document.getElementById('cart-container').innerHTML = 
                            '<div class="empty-result"><i class="fas fa-shopping-cart"></i><p>购物车加载失败: ' + (data.message || '未知错误') + '</p></div>';
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    document.getElementById('cart-container').innerHTML = 
                        '<div class="empty-result"><i class="fas fa-shopping-cart"></i><p>购物车加载失败</p></div>';
                });
        }

        // 显示购物车商品
        function displayCartItems(items) {
            const container = document.getElementById('cart-container');
            
            if (items.length === 0) {
                container.innerHTML = `
                    <div class="empty-result">
                        <i class="fas fa-shopping-cart"></i>
                        <p>购物车是空的</p>
                        <a href="${pageContext.request.contextPath}/index" class="btn btn-primary">
                            <i class="fas fa-shopping-bag"></i> 去逛逛
                        </a>
                    </div>
                `;
                return;
            }

            let totalAmount = 0;
            let cartHtml = `
                <div class="cart-items">
                    <table class="cart-table">
                        <thead>
                            <tr>
                                <th>商品信息</th>
                                <th>单价</th>
                                <th>数量</th>
                                <th>小计</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
            `;

            items.forEach(item => {
                const subtotal = parseFloat(item.price) * item.quantity;
                totalAmount += subtotal;
                
                // 安全转义可能包含特殊字符的内容
                const productName = item.productName ? item.productName.replace(/[\"'&<>]/g, function(match) {
                    return {
                        '"': '&quot;',
                        "'": '&#x27;',
                        '&': '&amp;',
                        '<': '&lt;',
                        '>': '&gt;'
                    }[match];
                }) : '';
                
                cartHtml += `
                    <tr>
                        <td class="cart-product">
                            <img src="${pageContext.request.contextPath}/\${item.productImageUrl.replace(/[\"'&<>]/g, function(match) {
                                return {
                                    '"': '&quot;',
                                    "'": '&#x27;',
                                    '&': '&amp;',
                                    '<': '&lt;',
                                    '>': '&gt;'
                                }[match];
                            })}" 
                                 alt="\${productName}" 
                                 onerror="this.src='${pageContext.request.contextPath}/images/no-image.jpg'">
                            <div class="product-info">
                                <h4>\${productName}</h4>
                            </div>
                        </td>
                        <td class="cart-price">¥\${parseFloat(item.price).toFixed(2)}</td>
                        <td class="cart-quantity">
                            <button class="quantity-btn" onclick="updateQuantity(\${item.productId}, -1)">-</button>
                            <input type="number" id="quantity-\${item.productId}" 
                                   value="\${item.quantity}" min="1" 
                                   onchange="updateQuantityDirect(\${item.productId})">
                            <button class="quantity-btn" onclick="updateQuantity(\${item.productId}, 1)">+</button>
                        </td>
                        <td class="cart-subtotal">¥\${subtotal.toFixed(2)}</td>
                        <td class="cart-actions">
                            <button class="btn btn-danger btn-sm" onclick="removeFromCart(\${item.productId})">
                                <i class="fas fa-trash"></i> 删除
                            </button>
                        </td>
                    </tr>
                `;
            });

            cartHtml += `
                        </tbody>
                    </table>
                </div>
                <div class="cart-summary">
                    <div class="summary-total">
                        <span>总计：</span>
                        <strong>¥\${totalAmount.toFixed(2)}</strong>
                    </div>
                    <button class="btn btn-success btn-lg" onclick="checkout()">
                        <i class="fas fa-credit-card"></i> 去结算
                    </button>
                </div>
            `;

            container.innerHTML = cartHtml;
        }

        // 更新商品数量
        function updateQuantity(productId, change) {
            const input = document.getElementById('quantity-' + productId);
            let quantity = parseInt(input.value) + change;
            
            if (quantity < 1) quantity = 1;
            
            updateQuantityDirect(productId, quantity);
        }

        // 直接更新商品数量
        function updateQuantityDirect(productId, newQuantity) {
            if (newQuantity === undefined) {
                const input = document.getElementById('quantity-' + productId);
                newQuantity = parseInt(input.value);
                
                if (isNaN(newQuantity) || newQuantity < 1) {
                    newQuantity = 1;
                    input.value = 1;
                }
            }

            fetch('${pageContext.request.contextPath}/api/cart/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'productId=' + productId + '&quantity=' + newQuantity
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    loadCart(); // 重新加载购物车
                } else {
                    alert('更新失败：' + data.message);
                    loadCart(); // 重新加载以恢复原值
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('更新数量时发生错误');
                loadCart(); // 重新加载以恢复原值
            });
        }

        // 从购物车删除商品
        function removeFromCart(productId) {
            if (confirm('确定要从购物车中删除这个商品吗？')) {
                fetch('${pageContext.request.contextPath}/api/cart/remove', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'productId=' + productId
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        loadCart(); // 重新加载购物车
                    } else {
                        alert('删除失败：' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('删除商品时发生错误');
                });
            }
        }

        // 结算
        function checkout() {
            fetch('${pageContext.request.contextPath}/api/order/create-from-cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('订单创建成功！订单号：' + data.orderNo);
                    loadCart(); // 重新加载购物车
                } else {
                    alert('结算失败：' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('结算时发生错误');
            });
        }

        // 页面加载完成后加载购物车
        document.addEventListener('DOMContentLoaded', function() {
            loadCart();
        });
    </script>
    <style>
        .cart-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        .cart-table th,
        .cart-table td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        .cart-table th {
            background-color: #f8f9fa;
            font-weight: bold;
            color: #333;
        }

        .cart-product {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .cart-product img {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 5px;
        }

        .product-info h4 {
            margin: 0;
            font-size: 16px;
            font-weight: normal;
        }

        .cart-quantity {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .quantity-btn {
            width: 30px;
            height: 30px;
            border: 1px solid #ddd;
            background: white;
            cursor: pointer;
            border-radius: 5px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .quantity-btn:hover {
            background: #f5f5f5;
        }

        .cart-quantity input {
            width: 60px;
            height: 30px;
            text-align: center;
            border: 1px solid #ddd;
            border-radius: 5px;
        }

        .cart-actions {
            white-space: nowrap;
        }

        .btn-sm {
            padding: 5px 10px;
            font-size: 12px;
        }

        .cart-summary {
            border-top: 2px solid #eee;
            padding-top: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
        }

        .summary-total {
            font-size: 18px;
            font-weight: bold;
        }

        .summary-total strong {
            color: #f5576c;
        }

        .cart-placeholder {
            text-align: center;
            padding: 40px;
            font-size: 18px;
            color: #666;
        }

        @media (max-width: 768px) {
            .cart-summary {
                flex-direction: column;
                text-align: center;
            }

            .cart-table {
                display: block;
                overflow-x: auto;
            }

            .cart-table th,
            .cart-table td {
                min-width: 120px;
            }
        }
    </style>
</body>
</html>