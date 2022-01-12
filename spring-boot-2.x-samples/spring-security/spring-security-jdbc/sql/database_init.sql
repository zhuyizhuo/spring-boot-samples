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
-- Records of user  密码为 123 的密文
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 'mic', '$2a$10$IcrdV2W/6XdDxDo2p8Zwj.YlvggsDYyDvqmggMZHS5.gMnLLoxPoK', 'p1', '2020-08-09 13:57:06');
INSERT INTO `user` VALUES (2, 'jack', '$2a$10$IcrdV2W/6XdDxDo2p8Zwj.YlvggsDYyDvqmggMZHS5.gMnLLoxPoK', 'p2', '2020-08-09 08:39:24');
COMMIT;


-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求地址',
  `perms` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限标识',
  `type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单类型（D目录 M菜单 B按钮）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- ----------------------------
-- Records of menu
-- ----------------------------
BEGIN;
INSERT INTO `menu` VALUES (1, '用户管理', '/user/info/p1', 'p1', 'M');
INSERT INTO `menu` VALUES (2, '用户管理', '/user/info/p2', 'p2', 'M');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
