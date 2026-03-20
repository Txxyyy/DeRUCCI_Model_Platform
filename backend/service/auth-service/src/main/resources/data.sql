-- 初始化管理员账号（仅在不存在时插入）
INSERT INTO t_user (username, password, nickname, role, status, create_time, update_time)
SELECT 'admin', 'admin123', '管理员', 'ADMIN', 'ENABLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'admin');

-- 确保已有admin用户有ADMIN角色
UPDATE t_user SET role = 'ADMIN' WHERE username = 'admin' AND (role IS NULL OR role = '');
