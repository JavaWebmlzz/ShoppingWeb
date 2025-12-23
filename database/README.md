# 数据库配置说明

## 快速开始

### 1. 创建数据库

在MySQL中执行以下命令：

```sql
CREATE DATABASE IF NOT EXISTS shopping_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 导入数据

```bash
mysql -u root -p shopping_db < init.sql
```

或者在MySQL客户端中：

```sql
USE shopping_db;
SOURCE init.sql;
```

### 3. 验证安装

```sql
-- 查看所有表
SHOW TABLES;

-- 查看商品数据
SELECT * FROM product LIMIT 5;

-- 查看分类数据
SELECT * FROM category;
```

## 数据库结构

### category（商品分类表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键，自增 |
| name | VARCHAR(50) | 分类名称 |
| description | VARCHAR(200) | 分类描述 |
| create_time | TIMESTAMP | 创建时间 |

### product（商品表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键，自增 |
| name | VARCHAR(100) | 商品名称 |
| category_id | INT | 分类ID（外键） |
| price | DECIMAL(10,2) | 价格 |
| stock | INT | 库存 |
| description | TEXT | 商品描述 |
| image_url | VARCHAR(200) | 图片路径 |
| create_time | TIMESTAMP | 创建时间 |

### browse_history（浏览记录表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键，自增 |
| user_cookie | VARCHAR(100) | 用户Cookie（匿名追踪） |
| category_id | INT | 浏览的分类ID |
| product_id | INT | 浏览的商品ID |
| browse_time | TIMESTAMP | 浏览时间 |

## 初始数据

### 商品分类（8个）

1. 数码产品
2. 化妆品
3. 服装
4. 食品
5. 图书
6. 家电
7. 运动户外
8. 母婴

### 示例商品（20+件）

包括手机、电脑、化妆品、服装、食品、图书等各类商品。

## 修改数据库连接

如果您的MySQL配置不同，请修改以下文件：

**文件位置：** `src/main/java/com/shopping/util/DBUtil.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/shopping_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";  // 修改为您的密码
```

## 常见问题

### 1. 连接失败

- 检查MySQL服务是否启动
- 确认用户名和密码是否正确
- 检查端口是否为3306

### 2. 时区错误

如果遇到时区错误，在URL中添加：
```
serverTimezone=Asia/Shanghai
```

### 3. 字符编码问题

确保数据库、表和连接都使用UTF-8编码：
```sql
ALTER DATABASE shopping_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 权限问题

确保MySQL用户有足够的权限：
```sql
GRANT ALL PRIVILEGES ON shopping_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

