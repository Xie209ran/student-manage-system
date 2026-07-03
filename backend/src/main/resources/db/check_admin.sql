-- 检查admin账号信息
USE edum;

SELECT 
    id,
    username,
    password,
    real_name,
    role,
    status,
    login_fail_count,
    locked_until
FROM sys_user 
WHERE username = 'admin';

-- 如果密码不对，重置为 admin123
-- UPDATE sys_user 
-- SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'
-- WHERE username = 'admin';
