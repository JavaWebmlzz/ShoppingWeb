<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - 服务器错误</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .error-container {
            min-height: 70vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
            padding: 40px 20px;
        }
        .error-code {
            font-size: 120px;
            font-weight: bold;
            background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 20px;
        }
        .error-message {
            font-size: 24px;
            color: #666;
            margin-bottom: 30px;
        }
        .error-icon {
            font-size: 80px;
            color: #f5576c;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="container">
            <div class="nav-brand">
                <i class="fas fa-shopping-bag"></i>
                <span>精品购物</span>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="error-container">
            <i class="fas fa-bug error-icon"></i>
            <div class="error-code">500</div>
            <div class="error-message">服务器遇到了一些问题</div>
            <a href="${pageContext.request.contextPath}/index" class="btn btn-primary">
                <i class="fas fa-home"></i> 返回首页
            </a>
        </div>
    </div>
</body>
</html>

