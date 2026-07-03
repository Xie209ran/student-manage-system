-- ============================================
-- 数据库状态检查脚本
-- ============================================
-- 用途：快速检查数据库是否完整
-- 使用方法：mysql -u root -p edum < check_database.sql
-- ============================================

USE edum;

SELECT '========================================' AS '';
SELECT '  数据库状态检查' AS '';
SELECT '========================================' AS '';
SELECT '' AS '';

-- 1. 检查表数量
SELECT '📊 表检查' AS '检查项';
SELECT 
    COUNT(*) as table_count,
    CASE 
        WHEN COUNT(*) = 8 THEN '✅ 正常（8张表）'
        WHEN COUNT(*) < 8 THEN CONCAT('❌ 缺失（', 8-COUNT(*), '张表）')
        ELSE CONCAT('⚠️ 过多（', COUNT(*), '张表）')
    END AS status
FROM information_schema.tables
WHERE table_schema = 'edum'
AND table_type = 'BASE TABLE';

SELECT '' AS '';
SELECT '表列表：' AS '';
SHOW TABLES;

SELECT '' AS '';

-- 2. 检查admin账号
SELECT '👤 admin账号检查' AS '检查项';
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ admin账号存在'
        ELSE '❌ admin账号缺失'
    END AS status
FROM sys_user 
WHERE username = 'admin';

SELECT '' AS '';
SELECT 'admin账号详情：' AS '';
SELECT id, username, real_name, role, status FROM sys_user WHERE username = 'admin';

SELECT '' AS '';

-- 3. 检查索引数量
SELECT '📈 索引检查' AS '检查项';
SELECT 
    COUNT(*) as index_count,
    CASE 
        WHEN COUNT(*) >= 40 THEN '✅ 正常'
        WHEN COUNT(*) >= 30 THEN '⚠️ 偏少'
        ELSE '❌ 不足'
    END AS status
FROM information_schema.statistics 
WHERE table_schema = 'edum';

SELECT '' AS '';
SELECT '各表索引数量：' AS '';
SELECT 
    table_name AS '表名',
    COUNT(*) AS '索引数'
FROM information_schema.statistics
WHERE table_schema = 'edum'
GROUP BY table_name
ORDER BY table_name;

SELECT '' AS '';
SELECT '========================================' AS '';
SELECT '  检查完成！' AS '';
SELECT '========================================' AS '';
SELECT '' AS '';
SELECT '登录信息：admin / admin123' AS '';
