-- 用户表结构SQL
-- 适用于MyBatis-Plus高级特性示例项目

-- 创建数据库（如果需要）
-- CREATE DATABASE IF NOT EXISTS springboot DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换到springboot数据库
-- USE springboot;

-- 删除已存在的user表（如果需要）
-- DROP TABLE IF EXISTS user;

-- 创建user表
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(30) DEFAULT NULL COMMENT '姓名',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(1) DEFAULT 0 COMMENT '逻辑删除标记（0：未删除，1：已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_age` (`age`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试数据
INSERT INTO `user` (`name`, `age`, `email`, `create_time`, `update_time`, `deleted`) VALUES
('张三', 28, 'zhangsan@example.com', NOW(), NOW(), 0),
('李四', 32, 'lisi@example.com', NOW(), NOW(), 0),
('王五', 25, 'wangwu@example.com', NOW(), NOW(), 0),
('Jessica', 22, 'jessica@example.com', NOW(), NOW(), 0),
('John', 30, 'john@example.com', NOW(), NOW(), 0);

-- 查看表结构
-- DESC user;

-- 查看插入的数据
-- SELECT * FROM user WHERE deleted = 0;