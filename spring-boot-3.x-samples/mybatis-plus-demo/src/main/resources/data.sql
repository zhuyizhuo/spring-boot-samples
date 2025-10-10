-- 插入角色数据
INSERT INTO role (name, description) VALUES
('ADMIN', '管理员角色'),
('USER', '普通用户角色'),
('GUEST', '访客角色');

-- 插入用户数据
INSERT INTO "user" (username, password, email, phone, status) VALUES
('admin', 'admin123', 'admin@example.com', '13800138001', 1),
('user1', 'user123', 'user1@example.com', '13800138002', 1),
('user2', 'user123', 'user2@example.com', '13800138003', 1),
('guest', 'guest123', 'guest@example.com', '13800138004', 0);

-- 插入用户角色关联数据
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1), -- admin拥有ADMIN角色
(1, 2), -- admin拥有USER角色
(2, 2), -- user1拥有USER角色
(3, 2), -- user2拥有USER角色
(4, 3); -- guest拥有GUEST角色