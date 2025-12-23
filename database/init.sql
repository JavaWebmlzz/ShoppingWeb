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

-- 插入示例分类数据
INSERT INTO category (name, description) VALUES
('数码产品', '手机、电脑、平板等数码设备'),
('化妆品', '口红、粉底、眼影等美妆产品'),
('服装', '男装、女装、童装'),
('食品', '零食、饮料、生鲜'),
('图书', '小说、教材、杂志'),
('家电', '冰箱、洗衣机、空调'),
('运动户外', '运动服、运动器材、户外用品'),
('母婴', '奶粉、玩具、童车');

-- 插入示例商品数据
INSERT INTO product (name, category_id, price, stock, description, image_url) VALUES
-- 数码产品
('iPhone 15 Pro', 1, 7999.00, 100, '苹果最新旗舰手机，A17芯片，钛金属边框', 'images/products/iphone15.jpg'),
('华为Mate 60 Pro', 1, 6999.00, 150, '华为旗舰手机，麒麟芯片回归', 'images/products/mate60.jpg'),
('小米14', 1, 3999.00, 200, '小米旗舰手机，骁龙8 Gen3', 'images/products/mi14.jpg'),
('MacBook Pro', 1, 12999.00, 50, 'M3芯片，14英寸视网膜屏', 'images/products/macbook.jpg'),
('iPad Air', 1, 4599.00, 80, 'M1芯片，10.9英寸全面屏', 'images/products/ipad.jpg'),

-- 化妆品
('YSL圣罗兰口红', 2, 320.00, 300, '明彩口红，滋润显色', 'images/products/ysl.jpg'),
('MAC魅可口红', 2, 180.00, 250, '经典子弹头口红', 'images/products/mac.jpg'),
('雅诗兰黛粉底液', 2, 480.00, 150, '持久遮瑕，自然妆感', 'images/products/estee.jpg'),
('兰蔻小黑瓶', 2, 680.00, 100, '肌底液，修护精华', 'images/products/lancome.jpg'),
('SK-II神仙水', 2, 1090.00, 80, '护肤精华水，改善肌肤', 'images/products/skii.jpg'),

-- 服装
('优衣库羽绒服', 3, 599.00, 200, '轻薄保暖，多色可选', 'images/products/uniqlo.jpg'),
('ZARA大衣', 3, 899.00, 150, '时尚修身，欧美风格', 'images/products/zara.jpg'),
('Nike运动鞋', 3, 799.00, 180, 'Air Max系列，舒适透气', 'images/products/nike.jpg'),
('Adidas卫衣', 3, 459.00, 220, '经典三条纹，纯棉面料', 'images/products/adidas.jpg'),

-- 食品
('三只松鼠坚果礼盒', 4, 99.00, 500, '每日坚果，健康零食', 'images/products/squirrel.jpg'),
('良品铺子肉脯', 4, 59.00, 400, '猪肉脯，美味可口', 'images/products/liangpin.jpg'),
('可口可乐', 4, 3.50, 1000, '经典碳酸饮料，330ml', 'images/products/cola.jpg'),
('旺旺雪饼', 4, 12.00, 600, '童年回忆，香脆可口', 'images/products/wangwang.jpg'),

-- 图书
('活着', 5, 28.00, 300, '余华经典作品', 'images/products/alive.jpg'),
('三体', 5, 89.00, 250, '刘慈欣科幻巨作，全三册', 'images/products/threebody.jpg'),
('Java编程思想', 5, 108.00, 200, '经典Java教材', 'images/products/java.jpg');

COMMIT;

