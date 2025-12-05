-- 创建demo schema（如果不存在）
CREATE SCHEMA IF NOT EXISTS demo;

-- 创建用户表（指定demo schema）
CREATE TABLE IF NOT EXISTS demo.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(50),
    phone VARCHAR(20)
);

-- 插入示例数据（指定demo schema）
INSERT INTO demo.users (username, password, email, name, phone) VALUES
('admin', '123456', 'admin@example.com', '管理员', '13800138000'),
('user1', 'password1', 'user1@example.com', '用户1', '13800138001'),
('user2', 'password2', 'user2@example.com', '用户2', '13800138002');
