SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户权限表';

-- ----------------------------
-- Records of user  密码为 123 的加密串
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 'zhangsan', '$2a$10$IcrdV2W/6XdDxDo2p8Zwj.YlvggsDYyDvqmggMZHS5.gMnLLoxPoK', 'admin', '2020-08-08 13:53:09');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
