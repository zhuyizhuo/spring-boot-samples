
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int(11) NOT NULL, -- 安装顺序，从 1 开始递增。
  `version` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL, -- 版本号
  `description` varchar(200) COLLATE utf8mb4_bin NOT NULL, -- 迁移脚本描述
  `type` varchar(20) COLLATE utf8mb4_bin NOT NULL, -- 脚本类型，目前有 SQL 和 Java 。
  `script` varchar(1000) COLLATE utf8mb4_bin NOT NULL, -- 脚本地址
  `checksum` int(11) DEFAULT NULL, -- 脚本校验码。避免已经执行的脚本，被人变更了。
  `installed_by` varchar(100) COLLATE utf8mb4_bin NOT NULL, -- 执行脚本的数据库用户
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 安装时间
  `execution_time` int(11) NOT NULL, -- 执行时长，单位毫秒
  `success` tinyint(1) NOT NULL, -- 执行结果是否成功。1-成功。0-失败
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;