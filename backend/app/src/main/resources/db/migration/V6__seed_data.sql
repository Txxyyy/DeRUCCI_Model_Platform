-- V6: Seed data
INSERT INTO t_user (username, password, nickname, role, status, create_time, update_time)
SELECT 'admin', 'admin123', '管理员', 'ADMIN', 'ENABLED', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'admin');
