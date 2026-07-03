-- 修复password字段长度问题
USE edum;

-- 1. 检查当前password字段长度
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'edum'
AND TABLE_NAME = 'sys_user'
AND COLUMN_NAME = 'password';

-- 2. 修改password字段为VARCHAR(255)（BCrypt hash需要60个字符）
ALTER TABLE sys_user 
MODIFY COLUMN password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）';

-- 3. 重置admin密码
UPDATE sys_user 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    login_fail_count = 0,
    locked_until = NULL,
    status = 1,
    update_time = NOW()
WHERE username = 'admin';

-- 4. 验证修复结果
SELECT 
    id,
    username,
    password,
    CHAR_LENGTH(password) as password_length
FROM sys_user 
WHERE username = 'admin';

-- password_length应该等于60
