-- 检查admin密码是否完整
USE edum;

SELECT 
    id,
    username,
    password,
    CHAR_LENGTH(password) as password_length,
    login_fail_count,
    locked_until
FROM sys_user 
WHERE username = 'admin';

-- BCrypt hash应该是60个字符
-- 如果长度不是60，说明密码被截断了
