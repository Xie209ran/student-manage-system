-- 详细检查admin账号
USE edum;

-- 1. 查看完整密码和长度
SELECT 
    id,
    username,
    password,
    CHAR_LENGTH(password) as password_length,
    login_fail_count,
    locked_until,
    status
FROM sys_user 
WHERE username = 'admin';

-- 2. 如果password_length不是60，说明密码被截断了
-- BCrypt hash格式：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
