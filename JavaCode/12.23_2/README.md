# 图书管理系统 + MySQL 持久化

**GitHub 仓库地址**: [https://github.com/kuonyuma/-Library-Management-System](https://github.com/kuonyuma/-Library-Management-System)

这个项目已经接入 MySQL，用于永久保存书籍信息。

## 1. 数据库准备

先执行 `src/db/schema.sql`：

```sql
CREATE DATABASE IF NOT EXISTS library_db DEFAULT CHARACTER SET utf8mb4;
USE library_db;

CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    author VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    price INT NOT NULL,
    count INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 2. 配置连接

编辑 `src/db/DBUtil.java`：

- `URL`
- `USER`
- `PASSWORD`

默认配置是：

- 数据库：`library_db`
- 用户名：`root`
- 密码：`123456`

## 3. 添加 JDBC 驱动

本项目使用 MySQL 8 驱动：`mysql-connector-j`。

如果你是 IntelliJ IDEA：

1. 下载 `mysql-connector-j-8.x.x.jar`
2. `File -> Project Structure -> Modules -> Dependencies`
3. 点击 `+` 添加这个 jar

## 4. 运行效果

- 程序启动时：从 `books` 表加载图书
- 如果表为空：自动写入少量默认图书
- 新增图书：写入数据库
- 删除图书：数据库同步删除
- 借书/还书：`count` 库存同步更新

这样即使重启程序，图书数据也不会丢失。

