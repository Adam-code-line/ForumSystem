# ForumSystem - 论坛管理系统

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)

## 项目简介

ForumSystem 是一个基于 Java 开发的控制台论坛管理系统，采用分层架构和工厂设计模式，支持用户注册登录、论坛板块管理、主题发布回复、权限管理、用户拉黑等完整的论坛功能。

## 主要特性

### 🔐 用户系统
- **三级权限体系**：普通用户、版主、管理员
- **用户注册登录**：安全的密码加密存储
- **个人中心管理**：信息修改、密码更改、发帖统计

### 📋 论坛功能
- **板块管理**：创建、编辑、删除板块
- **主题管理**：发布、编辑、置顶、锁定主题
- **回复系统**：多层回复、内容管理
- **搜索功能**：主题关键词搜索

### 👥 权限管理
- **工厂模式权限控制**：不同角色拥有不同操作权限
- **板块版主系统**：创建板块自动成为版主
- **内容审核机制**：版主和管理员可审核内容

### 🚫 用户拉黑系统
- **互相拉黑**：用户之间可以相互拉黑
- **版主拉黑**：版主可拉黑板块内违规用户
- **内容过滤**：被拉黑用户的内容自动隐藏
- **发言限制**：被拉黑用户无法在相关板块发言

### 🛡️ 内容管理
- **敏感词过滤**：自动检测和过滤敏感内容
- **批量管理**：支持批量删除主题和回复
- **内容审核**：多级审核机制

### 📊 数据统计
- **用户统计**：注册数量、活跃度分析
- **板块统计**：主题数量、回复统计
- **系统统计**：全站数据汇总

## 技术栈

- **编程语言**：Java 8+
- **数据库**：MySQL 8.0
- **数据库驱动**：MySQL Connector/J 8.0.33
- **设计模式**：工厂模式、分层架构
- **开发工具**：IntelliJ IDEA

## 项目结构

```
ForumSystem/
├── src/
│   ├── database.properties          # 数据库配置
│   ├── Main.java                   # 程序入口
│   └── main/forumsystem/src/
│       ├── controller/             # 控制层
│       │   ├── AuthController.java
│       │   ├── ForumController.java
│       │   ├── MainController.java
│       │   └── menu/              # 菜单控制器
│       │       ├── AdminMenuController.java      # 管理员菜单
│       │       ├── ForumMenuController.java      # 论坛功能菜单
│       │       ├── MainMenuController.java       # 主菜单
│       │       ├── ModeratorMenuController.java  # 版主菜单
│       │       ├── UserBlockMenuController.java  # 拉黑管理菜单
│       │       └── UserMenuController.java       # 用户个人中心
│       ├── dao/                   # 数据访问层
│       │   ├── BaseDao.java
│       │   ├── UserDao.java
│       │   ├── ForumDao.java
│       │   ├── TopicDao.java
│       │   ├── ReplyDao.java
│       │   ├── UserBlockDao.java
│       │   └── impl/              # DAO实现类
│       ├── entity/                # 实体类
│       │   ├── User.java          # 用户实体
│       │   ├── Forum.java         # 板块实体
│       │   ├── Topic.java         # 主题实体
│       │   ├── Reply.java         # 回复实体
│       │   ├── UserBlock.java     # 用户拉黑实体
│       │   ├── BanRecord.java     # 封禁记录实体
│       │   └── SensitiveWord.java # 敏感词实体
│       ├── factory/               # 工厂模式
│       │   ├── UserFactory.java
│       │   ├── UserOperationFactory.java
│       │   └── impl/
│       │       ├── NormalUserOperationFactory.java
│       │       ├── ModeratorOperationFactory.java
│       │       └── AdminOperationFactory.java
│       ├── service/               # 服务层
│       │   ├── AuthService.java
│       │   ├── ForumService.java
│       │   ├── UserService.java
│       │   ├── UserBlockService.java
│       │   ├── AdminService.java
│       │   └── impl/              # 服务实现类
│       └── util/                  # 工具类
│           ├── DatabaseUtil.java
│           ├── PasswordUtil.java
│           └── ValidationUtil.java
├── mysql-connector-j-8.0.33.jar   # MySQL驱动
└── README.md                      # 项目说明
```

## 快速开始

### 环境要求

- Java 8 或更高版本
- MySQL 8.0 或更高版本
- IntelliJ IDEA（推荐）

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/Adam-code-line/ForumSystem.git
   cd ForumSystem
   ```

2. **配置数据库**
   
   创建 MySQL 数据库：
   ```sql
   CREATE DATABASE forum_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **创建数据表**
   
   执行以下 SQL 脚本创建所需的数据表：

   ```sql
   -- 用户表
   CREATE TABLE users (
       user_id INT PRIMARY KEY AUTO_INCREMENT,
       username VARCHAR(50) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       email VARCHAR(100) UNIQUE NOT NULL,
       nick_name VARCHAR(50),
       avatar VARCHAR(255) DEFAULT NULL,
       role ENUM('USER', 'MODERATOR', 'ADMIN') DEFAULT 'USER',
       status ENUM('ACTIVE', 'BANNED', 'INACTIVE') DEFAULT 'ACTIVE',
       post_count INT DEFAULT 0,
       reputation INT DEFAULT 0,
       register_time DATETIME DEFAULT CURRENT_TIMESTAMP,
       last_login_time DATETIME,
       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
   );

   -- 板块表
   CREATE TABLE forums (
       forum_id INT PRIMARY KEY AUTO_INCREMENT,
       forum_name VARCHAR(100) NOT NULL,
       description TEXT,
       moderator_id INT,
       topic_count INT DEFAULT 0,
       post_count INT DEFAULT 0,
       status ENUM('ACTIVE', 'HIDDEN', 'DELETED') DEFAULT 'ACTIVE',
       sort_order INT DEFAULT 0,
       create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
       FOREIGN KEY (moderator_id) REFERENCES users(user_id)
   );

   -- 主题表
   CREATE TABLE topics (
       topic_id INT PRIMARY KEY AUTO_INCREMENT,
       title VARCHAR(200) NOT NULL,
       content TEXT NOT NULL,
       user_id INT NOT NULL,
       forum_id INT NOT NULL,
       is_pinned BOOLEAN DEFAULT FALSE,
       is_locked BOOLEAN DEFAULT FALSE,
       reply_count INT DEFAULT 0,
       view_count INT DEFAULT 0,
       status ENUM('NORMAL', 'HIDDEN', 'DELETED') DEFAULT 'NORMAL',
       create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
       last_reply_time DATETIME,
       last_reply_user_id INT,
       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
       FOREIGN KEY (user_id) REFERENCES users(user_id),
       FOREIGN KEY (forum_id) REFERENCES forums(forum_id)
   );

   -- 回复表
   CREATE TABLE replies (
       reply_id INT PRIMARY KEY AUTO_INCREMENT,
       content TEXT NOT NULL,
       user_id INT NOT NULL,
       topic_id INT NOT NULL,
       status ENUM('NORMAL', 'HIDDEN', 'DELETED') DEFAULT 'NORMAL',
       create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
       FOREIGN KEY (user_id) REFERENCES users(user_id),
       FOREIGN KEY (topic_id) REFERENCES topics(topic_id)
   );

   -- 用户拉黑表
   CREATE TABLE user_blocks (
       block_id INT PRIMARY KEY AUTO_INCREMENT,
       blocker_id INT NOT NULL,
       blocked_id INT NOT NULL,
       block_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
       status ENUM('active', 'removed') NOT NULL DEFAULT 'active',
       reason VARCHAR(500),
       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
       FOREIGN KEY (blocker_id) REFERENCES users(user_id) ON DELETE CASCADE,
       FOREIGN KEY (blocked_id) REFERENCES users(user_id) ON DELETE CASCADE,
       UNIQUE KEY uk_blocker_blocked (blocker_id, blocked_id)
   );

   -- 敏感词表
   CREATE TABLE sensitive_words (
       word_id INT PRIMARY KEY AUTO_INCREMENT,
       word VARCHAR(100) NOT NULL UNIQUE,
       create_time DATETIME DEFAULT CURRENT_TIMESTAMP
   );

   -- 封禁记录表
   CREATE TABLE ban_records (
       ban_id INT PRIMARY KEY AUTO_INCREMENT,
       user_id INT NOT NULL,
       admin_id INT NOT NULL,
       reason TEXT NOT NULL,
       ban_start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
       ban_end_time DATETIME,
       status ENUM('ACTIVE', 'LIFTED') DEFAULT 'ACTIVE',
       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
       FOREIGN KEY (user_id) REFERENCES users(user_id),
       FOREIGN KEY (admin_id) REFERENCES users(user_id)
   );
   ```

4. **配置数据库连接**
   
   修改 `src/database.properties` 文件：
   ```properties
   db.url=jdbc:mysql://localhost:3306/forum_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
   db.username=your_username
   db.password=your_password
   db.driver=com.mysql.cj.jdbc.Driver
   ```

5. **运行项目**
   
   在 IntelliJ IDEA 中：
   - 导入项目
   - 添加 MySQL 驱动依赖
   - 运行 `Main.java`

   或使用命令行：
   ```bash
   javac -cp ".:mysql-connector-j-8.0.33.jar" src/Main.java
   java -cp ".:mysql-connector-j-8.0.33.jar:src" Main
   ```

## 使用指南

### 初次使用

1. **启动系统**
   - 运行程序后会显示欢迎界面
   - 首次使用需要先注册账号

2. **注册账号**
   ```
   选择 [2] 注册
   输入用户名、密码、邮箱
   系统会创建普通用户账号
   ```

3. **登录系统**
   ```
   选择 [1] 登录
   输入用户名和密码
   登录成功后进入主菜单
   ```

### 主要功能使用

#### 📋 板块管理
- **查看板块**：主菜单 → [1] 查看所有板块
- **创建板块**：主菜单 → [2] 创建新板块（创建者自动成为版主）
- **进入板块**：主菜单 → [3] 进入板块

#### ✍️ 内容发布
- **发布主题**：主菜单 → [4] 发布主题
- **搜索主题**：主菜单 → [5] 搜索主题
- **回复主题**：进入板块 → 查看主题详情 → 发表回复

#### 👤 个人中心
- **个人信息**：主菜单 → [12] 个人中心
- **拉黑管理**：个人中心 → [6] 拉黑管理
- **修改密码**：个人中心 → [3] 修改密码

#### 🛡️ 版主功能（需要版主权限）
- **版主管理**：主菜单 → [7] 版主管理
- **管理板块**：查看我管理的板块 → 进入板块管理
- **主题管理**：置顶、锁定、删除主题
- **用户管理**：板块内用户拉黑

#### ⚙️ 管理员功能（需要管理员权限）
- **用户管理**：主菜单 → [8] 用户管理
- **内容审核**：主菜单 → [9] 内容审核
- **敏感词管理**：主菜单 → [10] 敏感词管理
- **系统统计**：主菜单 → [11] 系统统计

### 拉黑功能详解

#### 如何使用拉黑功能

1. **进入拉黑管理**
   ```
   主菜单 → [12] 个人中心 → [6] 拉黑管理
   ```

2. **拉黑用户**
   ```
   拉黑管理 → [1] 拉黑用户
   输入要拉黑的用户名和原因
   ```

3. **查看拉黑列表**
   ```
   拉黑管理 → [3] 查看拉黑列表
   ```

4. **取消拉黑**
   ```
   拉黑管理 → [2] 取消拉黑
   输入要取消拉黑的用户名
   ```

#### 拉黑效果

- ✅ **内容过滤**：被拉黑用户的主题和回复对您不可见
- ✅ **发言限制**：被您拉黑的用户无法回复您的主题
- ✅ **板块限制**：被版主拉黑的用户无法在该板块发言
- ✅ **双向生效**：拉黑关系对双方都有效

## 系统架构

### 分层架构

```
┌─────────────────┐
│   控制层 (Controller)  │  ← 处理用户输入，调用服务层
├─────────────────┤
│   服务层 (Service)     │  ← 业务逻辑处理
├─────────────────┤
│   数据层 (DAO)         │  ← 数据库访问
├─────────────────┤
│   实体层 (Entity)      │  ← 数据模型
└─────────────────┘
```

### 工厂模式权限控制

```java
UserOperationFactory
├── NormalUserOperationFactory    // 普通用户权限
├── ModeratorOperationFactory     // 版主权限  
└── AdminOperationFactory         // 管理员权限
```

### 核心设计模式

- **工厂模式**：根据用户角色创建不同的操作权限
- **分层架构**：清晰的职责分离
- **策略模式**：不同用户角色的不同行为策略

## 数据库设计

### 核心表关系

```
users (用户表)
├── forums (板块表) - moderator_id → users.user_id
├── topics (主题表) - user_id → users.user_id
├── replies (回复表) - user_id → users.user_id
├── user_blocks (拉黑表) - blocker_id/blocked_id → users.user_id
└── ban_records (封禁表) - user_id/admin_id → users.user_id
```

### 权限等级

| 角色 | 权限描述 |
|------|---------|
| **普通用户** | 发帖、回复、创建板块（自动成为版主）、拉黑其他用户 |
| **版主** | 普通用户权限 + 管理自己的板块、审核内容、拉黑板块用户 |
| **管理员** | 所有权限 + 用户管理、系统管理、敏感词管理 |

## 常见问题

### Q: 如何成为版主？
A: 创建板块后自动成为该板块的版主，或由管理员提升权限。

### Q: 拉黑功能不生效？
A: 确保数据库中 `user_blocks` 表已创建，且用户名输入正确。

### Q: 数据库连接失败？
A: 检查 `database.properties` 配置，确保数据库服务运行正常。

### Q: 忘记管理员密码？
A: 可直接在数据库中修改用户角色或重置密码。

### Q: 如何添加初始管理员？
A: 在数据库中手动插入管理员用户或修改现有用户角色为 'ADMIN'。

## 开发计划

### 已完成功能 ✅
- [x] 用户注册登录系统
- [x] 论坛板块管理
- [x] 主题发布回复
- [x] 权限管理系统
- [x] 用户拉黑功能
- [x] 内容搜索功能
- [x] 敏感词过滤
- [x] 系统统计功能

### 待完善功能 🚧
- [ ] 主题分页显示
- [ ] 回复楼层显示
- [ ] 用户头像系统
- [ ] 私信功能
- [ ] 主题标签系统
- [ ] 积分奖励系统
- [ ] 邮件通知功能

### 未来计划 📅
- [ ] Web 界面版本
- [ ] RESTful API
- [ ] 文件上传功能
- [ ] 实时通知系统
- [ ] 移动端适配

## 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目地址：[https://github.com/Adam-code-line/ForumSystem](https://github.com/Adam-code-line/ForumSystem)
- 问题反馈：[Issues](https://github.com/Adam-code-line/ForumSystem/issues)
- 邮箱：......

## 致谢

感谢所有为这个项目做出贡献的开发者们！

---

⭐ 如果这个项目对您有帮助，请给它一个 Star！