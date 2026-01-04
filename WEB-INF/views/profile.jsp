<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户中心 - 精品购物商城</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .profile-container {
            max-width: 800px;
            margin: 30px auto;
            padding: 30px;
            background: white;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        .profile-title {
            text-align: center;
            margin-bottom: 30px;
            color: #333;
            border-bottom: 2px solid #eee;
            padding-bottom: 15px;
        }
        
        .user-info {
            margin-bottom: 30px;
        }
        
        .info-item {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        
        .info-label {
            font-weight: bold;
            color: #555;
        }
        
        .info-value {
            color: #333;
        }
        
        .profile-actions {
            display: flex;
            gap: 15px;
            margin-top: 30px;
        }
        
        .btn-profile {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-danger {
            background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
            color: white;
        }
        
        .btn-profile:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
    </style>
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
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="profile-container">
            <h2 class="profile-title">用户中心</h2>
            
            <div class="user-info">
                <h3><i class="fas fa-user-circle"></i> 个人信息</h3>
                
                <div class="info-item">
                    <span class="info-label">用户名：</span>
                    <span class="info-value">${currentUser.username}</span>
                </div>
                
                <div class="info-item">
                    <span class="info-label">邮箱：</span>
                    <span class="info-value">${currentUser.email}</span>
                </div>
                
                <div class="info-item">
                    <span class="info-label">手机号：</span>
                    <span class="info-value">${currentUser.phone}</span>
                </div>
                
                <div class="info-item">
                    <span class="info-label">注册时间：</span>
                    <span class="info-value">${currentUser.createTime}</span>
                </div>
            </div>
            
            <div class="profile-actions">
                <a href="${pageContext.request.contextPath}/index" class="btn-profile btn-primary">
                    <i class="fas fa-arrow-left"></i> 返回首页
                </a>
                <a href="#" class="btn-profile btn-danger" onclick="logout()">
                    <i class="fas fa-sign-out-alt"></i> 退出登录
                </a>
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