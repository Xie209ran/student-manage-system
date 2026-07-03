-- 重置admin密码为 admin123
USE edum;

-- 1. 先查看当前密码（确认是否存在）
SELECT id, username, LEFT(password, 30) as password_preview, login_fail_count, locked_until 
FROM sys_user 
WHERE username = 'admin';

-- 2. 重置密码为 admin123（BCrypt加密后的hash）
UPDATE sys_user 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    login_fail_count = 0,
    locked_until = NULL,
    status = 1,
    update_time = NOW()
WHERE username = 'admin';

-- 3. 验证重置结果
SELECT id, username, LEFT(password, 30) as password_preview, login_fail_count, locked_until, status
FROM sys_user 
WHERE username = 'admin';
