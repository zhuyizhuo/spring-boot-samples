-- 创建用户表
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(6) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `username` varchar(32) DEFAULT '' COMMENT '账号',
  `password` varchar(32) DEFAULT '' COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入一条数据 密码为 123456 MD5 加密的 32 位大写
INSERT INTO `users`(username, password, create_time) VALUES('张三', 'E10ADC3949BA59ABBE56E057F20F883E', now());