-- 检查admin账号是否被锁定
USE edum;

SELECT 
    id,
    username,
    login_fail_count,
    locked_until,
    status,
    CASE 
        WHEN login_fail_count >= 5 THEN '⚠️ 可能被锁定（>=5次失败）'
        ELSE '✅ 正常'
    END as fail_status,
    CASE 
        WHEN locked_until > NOW() THEN '🔒 当前被锁定'
        WHEN locked_until IS NOT NULL THEN '📅 曾被锁定（已过期）'
        ELSE '✅ 未被锁定'
    END as lock_status
FROM sys_user 
WHERE username = 'admin';

-- 如果login_fail_count >= 5，重置它
UPDATE sys_user 
SET login_fail_count = 0,
    locked_until = NULL
WHERE username = 'admin' 
AND login_fail_count >= 5;
