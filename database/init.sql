-- 购物网站数据库初始化脚本

CREATE DATABASE IF NOT EXISTS shopping_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopping_db;

-- 商品分类表
CREATE TABLE IF NOT EXISTS category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE IF NOT EXISTS product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category_id INT,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    description TEXT,
    image_url VARCHAR(200),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

-- 用户浏览记录表（匿名用户追踪）
CREATE TABLE IF NOT EXISTS browse_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_cookie VARCHAR(100) NOT NULL,
    category_id INT,
    product_id INT,
    browse_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_cookie (user_cookie),
    INDEX idx_category (category_id)
);

-- 购物车表
CREATE TABLE IF NOT EXISTS cart (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(100) NOT NULL,
    product_id INT NOT NULL,
    quantity INT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单项表
CREATE TABLE IF NOT EXISTS order_item (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入示例分类数据
INSERT INTO category (name, description) VALUES
('教育', '教育用品、学习资料、教具等'),
('数码产品', '手机、电脑、平板等数码设备'),
('运动户外', '运动服、运动器材、户外用品'),
('玩具', '各类玩具、益智玩具、儿童玩具'),
('服装', '男装、女装、童装'),
('化妆品', '口红、粉底、眼影等美妆产品');

-- 插入示例商品数据
INSERT INTO product (name, category_id, price, stock, description, image_url) VALUES
-- 教育 (category_id = 1)
('儿童学习机', 1, 1299.00, 100, '智能学习机，支持多学科辅导', 'images/products/learning-machine.jpg'),
('早教益智玩具', 1, 189.00, 200, '开发儿童智力的教育玩具', 'images/products/educational-toy.jpg'),
('儿童绘本', 1, 35.00, 300, '适合3-8岁儿童的精美绘本', 'images/products/children-book.jpg'),

-- 数码产品 (category_id = 2)
('iPhone 15 Pro', 2, 7999.00, 100, '苹果最新旗舰手机，A17芯片，钛金属边框', 'images/products/iphone15.jpg'),
('华为Mate 60 Pro', 2, 6999.00, 150, '华为旗舰手机，麒麟芯片回归', 'images/products/mate60.jpg'),
('小米14', 2, 3999.00, 200, '小米旗舰手机，骁龙8 Gen3', 'images/products/mi14.jpg'),
('MacBook Pro', 2, 12999.00, 50, 'M3芯片，14英寸视网膜屏', 'images/products/macbook.jpg'),
('iPad Air', 2, 4599.00, 80, 'M1芯片，10.9英寸全面屏', 'images/products/ipad.jpg'),

-- 运动户外 (category_id = 3)
('Nike运动鞋', 3, 799.00, 180, 'Air Max系列，舒适透气', 'images/products/nike.jpg'),
('Adidas运动服', 3, 459.00, 220, '经典三条纹运动服，纯棉面料', 'images/products/adidas-sport.jpg'),
('户外登山包', 3, 299.00, 150, '专业户外登山包，防水耐磨', 'images/products/outdoor-bag.jpg'),
('瑜伽垫', 3, 89.00, 250, '防滑瑜伽垫，环保材质', 'images/products/yoga-mat.jpg'),

-- 玩具 (category_id = 4)
('乐高积木', 4, 299.00, 120, '经典乐高积木，培养创造力', 'images/products/lego.jpg'),
('遥控汽车', 4, 159.00, 180, '遥控玩具汽车，适合儿童', 'images/products/remote-car.jpg'),
('拼图玩具', 4, 79.00, 200, '1000片拼图，锻炼思维', 'images/products/puzzle.jpg'),
('毛绒玩具', 4, 69.00, 300, '可爱毛绒玩具，适合送礼', 'images/products/stuffed-toy.jpg'),

-- 服装 (category_id = 5)
('优衣库羽绒服', 5, 599.00, 200, '轻薄保暖，多色可选', 'images/products/uniqlo.jpg'),
('ZARA大衣', 5, 899.00, 150, '时尚修身，欧美风格', 'images/products/zara.jpg'),
('Adidas卫衣', 5, 299.00, 220, '经典三条纹卫衣，舒适面料', 'images/products/adidas-sweater.jpg'),
('牛仔裤', 5, 199.00, 250, '经典牛仔裤，百搭款式', 'images/products/jeans.jpg'),

-- 化妆品 (category_id = 6)
('YSL圣罗兰口红', 6, 320.00, 300, '明彩口红，滋润显色', 'images/products/ysl.jpg'),
('MAC魅可口红', 6, 180.00, 250, '经典子弹头口红', 'images/products/mac.jpg'),
('雅诗兰黛粉底液', 6, 480.00, 150, '持久遮瑕，自然妆感', 'images/products/estee.jpg'),
('兰蔻小黑瓶', 6, 680.00, 100, '肌底液，修护精华', 'images/products/lancome.jpg');

COMMIT;
