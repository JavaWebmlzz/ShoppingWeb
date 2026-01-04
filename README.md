# ShoppingWeb
购物网站 --- zyb
# 购物网站 - JavaWeb实验课作业

## 项目简介

这是一个基于JavaWeb技术栈开发的购物网站系统，实现了匿名用户精准广告投放功能。该项目是实验课大作业的一部分，与广告管理网站、新闻网站、视频分享网站共同构成完整的广告投放生态系统。

## 主要功能

### 1. 商品展示
- ✅ 精美的商品展示页面
- ✅ 商品分类浏览
- ✅ 商品搜索功能
- ✅ 商品详情页

### 2. 匿名用户追踪
- ✅ 基于Cookie的用户识别
- ✅ 浏览历史记录保存
- ✅ 用户偏好分析

### 3. 精准广告投放
- ✅ 调用广告管理网站API接口
- ✅ 根据用户浏览历史推荐相关广告
- ✅ 支持文字/图片/视频广告展示
- ✅ 分类精准匹配（如：浏览手机→推送数码产品广告）

### 4. 响应式设计
- ✅ 支持PC和移动端访问
- ✅ 现代化UI设计
- ✅ 流畅的用户体验

## 技术栈

### 后端
- Java 8
- Servlet 4.0
- JSP/JSTL
- MySQL 8.0
- Maven

### 前端
- HTML5/CSS3
- JavaScript (ES6)
- Font Awesome 图标库

## 项目结构

```
News_shopping_web/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/shopping/
│   │   │       ├── dao/              # 数据访问层
│   │   │       │   ├── ProductDao.java
│   │   │       │   ├── CategoryDao.java
│   │   │       │   └── BrowseHistoryDao.java
│   │   │       ├── model/            # 实体类
│   │   │       │   ├── Product.java
│   │   │       │   └── Category.java
│   │   │       ├── servlet/          # 控制器
│   │   │       │   ├── IndexServlet.java
│   │   │       │   ├── ProductListServlet.java
│   │   │       │   ├── ProductDetailServlet.java
│   │   │       │   └── AdRecommendServlet.java
│   │   │       ├── filter/           # 过滤器
│   │   │       │   └── CharacterEncodingFilter.java
│   │   │       └── util/             # 工具类
│   │   │           └── DBUtil.java
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── views/            # JSP页面
│   │       │   │   ├── index.jsp
│   │       │   │   ├── product-list.jsp
│   │       │   │   ├── product-detail.jsp
│   │       │   │   └── error/
│   │       │   └── web.xml
│   │       ├── css/                  # 样式文件
│   │       │   └── style.css
│   │       ├── js/                   # JavaScript文件
│   │       │   └── ad-loader.js
│   │       └── images/               # 图片资源
├── database/
│   └── init.sql                      # 数据库初始化脚本
├── pom.xml                           # Maven配置
└── README.md                         # 项目文档
```

## 环境要求

- JDK 1.8+
- Apache Tomcat 9.0+
- MySQL 8.0+
- Maven 3.6+

## 部署步骤

### 1. 数据库配置

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source database/init.sql
```

或者直接在MySQL客户端中执行 `database/init.sql` 文件。

### 2. 修改数据库连接

编辑 `src/main/java/com/shopping/util/DBUtil.java`，修改数据库连接信息：

```java
private static final String URL = "jdbc:mysql://localhost:3306/shopping_db?...";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
```

### 3. Maven构建

```bash
# 清理并打包
mvn clean package
```

### 4. 部署到Tomcat

**方式一：使用war包部署**
```bash
# 将生成的war包复制到Tomcat的webapps目录
copy target\shopping-web.war %TOMCAT_HOME%\webapps\
```

**方式二：IDE部署**
- 在IDEA中配置Tomcat服务器
- 添加本项目为部署项目
- 点击运行

### 5. 访问应用

启动Tomcat后，访问：
```
http://localhost:8080/shopping-web/index
```

## API接口说明

### 广告推荐API

**接口地址：** `/api/ad/recommend`

**请求方法：** GET

**响应示例：**
```json
{
  "categoryId": 1,
  "categoryName": "数码产品"
}
```

**说明：** 该接口根据用户的浏览历史（通过Cookie追踪），返回用户最感兴趣的商品分类，供广告系统进行精准投放。

## 广告集成说明

### JavaScript API调用

在页面中引入广告加载脚本：

```html
<div id="ad-container" class="ad-banner"></div>
<script src="${pageContext.request.contextPath}/js/ad-loader.js"></script>
```

### 精准匹配逻辑

1. **用户浏览行为追踪**
   - 用户访问商品详情页时，系统自动记录分类信息
   - 通过Cookie识别匿名用户

2. **偏好分析**
   - 统计用户浏览次数最多的分类
   - 调用 `/api/ad/recommend` 获取推荐分类

3. **广告匹配**
   - 根据分类信息调用广告管理网站API
   - 获取对应分类的广告并展示

### 分类映射

| 商品分类 | 推荐广告类型 |
|---------|-------------|
| 数码产品 | 手机、电脑等数码广告 |
| 化妆品 | 美妆、护肤品广告 |
| 服装 | 时尚服饰广告 |
| 食品 | 零食、饮料广告 |

## Git分支管理

### 分支策略

```bash
# 主分支
main/master        # 生产环境代码

# 开发分支
develop            # 开发环境代码

# 功能分支
feature/product-list    # 商品列表功能
feature/ad-system       # 广告系统集成
feature/user-tracking   # 用户追踪功能
```

### 常用命令

```bash
# 创建并切换到新分支
git checkout -b feature/new-feature

# 提交代码
git add .
git commit -m "Add new feature"

# 合并分支
git checkout develop
git merge feature/new-feature

# 回滚到指定版本
git reset --hard commit_id
```

## 多服务器部署

### 服务器角色分配

1. **服务器1：广告管理网站**
   - 端口：8080
   - 路径：/ad-management

2. **服务器2：购物网站（本项目）**
   - 端口：8081
   - 路径：/shopping-web

3. **服务器3：新闻网站**
   - 端口：8082
   - 路径：/news-web

4. **服务器4：视频分享网站**
   - 端口：8083
   - 路径：/video-web

### 修改广告API地址

编辑 `src/main/webapp/js/ad-loader.js`：

```javascript
// 修改为实际的广告管理网站地址
const AD_API_URL = 'http://server1:8080/ad-management/api/ads';
```

## 注意事项

1. **数据库权限**：确保MySQL用户有创建数据库和表的权限
2. **端口冲突**：如果8080端口被占用，需要修改Tomcat端口
3. **跨域问题**：如果广告API调用失败，需要配置CORS
4. **图片路径**：商品图片放在 `webapp/images/products/` 目录下

## 功能演示

### 1. 首页展示
- 顶部导航栏（搜索、分类）
- 广告横幅（精准推荐）
- 商品网格展示

### 2. 分类浏览
- 点击分类标签
- 过滤对应商品
- 动态更新广告

### 3. 商品搜索
- 输入关键词
- 模糊匹配商品名称和描述
- 展示搜索结果

### 4. 商品详情
- 商品图片
- 详细信息
- 价格和库存
- 相关推荐广告

## 扩展功能（可选）

- [ ] 购物车功能
- [ ] 用户登录/注册
- [ ] 订单管理
- [ ] 支付集成
- [ ] 商品评论
- [ ] 收藏功能

## 作者信息

JavaWeb实验课作业项目
2025年12月

## 许可证

本项目仅用于教学和学习目的。

