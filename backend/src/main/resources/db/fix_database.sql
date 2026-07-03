-- ============================================
-- 数据库修复脚本
-- ============================================
-- 用途：修复缺失的表和admin账号
-- 使用方法：mysql -u root -p edum < fix_database.sql
-- ============================================

USE edum;

-- ============================================
-- 1. 检查并创建缺失的表
-- ============================================

-- 检查所有8张表是否存在
SELECT 
    expected.TABLE_NAME,
    CASE 
        WHEN actual.TABLE_NAME IS NOT NULL THEN '✅ 存在'
        ELSE '❌ 缺失'
    END AS status
FROM (
    SELECT 'sys_user' AS TABLE_NAME
    UNION ALL SELECT 'class_entity'
    UNION ALL SELECT 'student'
    UNION ALL SELECT 'course_schedule'
    UNION ALL SELECT 'attendance'
    UNION ALL SELECT 'score'
    UNION ALL SELECT 'homework'
    UNION ALL SELECT 'homework_submission'
) AS expected
LEFT JOIN information_schema.tables AS actual
ON expected.TABLE_NAME = actual.TABLE_NAME
AND actual.TABLE_SCHEMA = 'edum';

-- 如果score表缺失，创建它
CREATE TABLE IF NOT EXISTS score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    subject VARCHAR(50) NOT NULL COMMENT '科目',
    exam_name VARCHAR(100) NOT NULL COMMENT '考试名称',
    score DECIMAL(5,2) NOT NULL COMMENT '成绩',
    full_score DECIMAL(5,2) NOT NULL DEFAULT 100 COMMENT '满分',
    exam_date DATE NOT NULL COMMENT '考试日期',
    `rank` INT COMMENT '排名',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    UNIQUE KEY uk_student_subject_exam (student_id, subject, exam_name),
    INDEX idx_class_subject (class_id, subject),
    INDEX idx_exam_date (exam_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- ============================================
-- 2. 修复或创建admin账号
-- ============================================

-- 检查admin账号是否存在
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ admin账号已存在'
        ELSE '❌ admin账号不存在，将创建'
    END AS admin_status
FROM sys_user 
WHERE username = 'admin';

-- 插入或更新admin账号（使用较短的姓名避免字符集问题）
INSERT INTO sys_user (username, password, real_name, role, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Admin', 'admin', 1)
ON DUPLICATE KEY UPDATE 
    password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    real_name = 'Admin',
    role = 'admin',
    status = 1;

-- 验证admin账号
SELECT id, username, real_name, role, status FROM sys_user WHERE username = 'admin';

-- ============================================
-- 3. 检查索引数量
-- ============================================

SELECT 
    COUNT(*) as total_indexes,
    CASE 
        WHEN COUNT(*) >= 40 THEN '✅ 索引数量正常'
        WHEN COUNT(*) >= 30 THEN '⚠️ 索引数量偏少'
        ELSE '❌ 索引数量不足'
    END AS index_status
FROM information_schema.statistics 
WHERE table_schema = 'edum';

-- 查看每个表的索引数量
SELECT 
    table_name,
    COUNT(*) as index_count
FROM information_schema.statistics
WHERE table_schema = 'edum'
GROUP BY table_name
ORDER BY table_name;

-- ============================================
-- 4. 完成提示
-- ============================================

SELECT '========================================' AS message;
SELECT '✅ 数据库修复完成！' AS result;
SELECT '========================================' AS message;
SELECT ' ' AS ' ';

-- 显示最终状态
SELECT 
    '📊 表数量' AS item,
    CONCAT(COUNT(*), '/8') AS value
FROM information_schema.tables
WHERE table_schema = 'edum'
AND table_type = 'BASE TABLE'

UNION ALL

SELECT 
    '👤 admin账号' AS item,
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ 存在'
        ELSE '❌ 缺失'
    END AS value
FROM sys_user 
WHERE username = 'admin'

UNION ALL

SELECT 
    '📈 索引数量' AS item,
    CAST(COUNT(*) AS CHAR) AS value
FROM information_schema.statistics 
WHERE table_schema = 'edum';

SELECT ' ' AS ' ';
SELECT '登录信息：admin / admin123' AS login_info;
SELECT '========================================' AS message;
