-- 修复数据库表结构（智能检查版）
USE edum;

-- 1. score表 - 检查并添加缺失字段
SELECT '=== 修复score表 ===' AS info;

-- pass_score
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'edum' AND TABLE_NAME = 'score' AND COLUMN_NAME = 'pass_score';
SET @sql = IF(@col_exists = 0, 'ALTER TABLE score ADD COLUMN pass_score DECIMAL(5,2) COMMENT ''及格分数'' AFTER full_score', 'SELECT ''pass_score已存在'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- input_user_id
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'edum' AND TABLE_NAME = 'score' AND COLUMN_NAME = 'input_user_id';
SET @sql = IF(@col_exists = 0, 'ALTER TABLE score ADD COLUMN input_user_id BIGINT COMMENT ''录入用户ID'' AFTER `rank`', 'SELECT ''input_user_id已存在'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- update_user_id
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'edum' AND TABLE_NAME = 'score' AND COLUMN_NAME = 'update_user_id';
SET @sql = IF(@col_exists = 0, 'ALTER TABLE score ADD COLUMN update_user_id BIGINT COMMENT ''更新用户ID'' AFTER input_user_id', 'SELECT ''update_user_id已存在'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- modify_time
SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'edum' AND TABLE_NAME = 'score' AND COLUMN_NAME = 'modify_time';
SET @sql = IF(@col_exists = 0, 'ALTER TABLE score ADD COLUMN modify_time DATETIME COMMENT ''修改时间'' AFTER update_user_id', 'SELECT ''modify_time已存在'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- idx_input_user索引
SELECT COUNT(*) INTO @idx_exists FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = 'edum' AND TABLE_NAME = 'score' AND INDEX_NAME = 'idx_input_user';
SET @sql = IF(@idx_exists = 0, 'ALTER TABLE score ADD INDEX idx_input_user (input_user_id)', 'SELECT ''idx_input_user已存在'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- idx_update_user索引
SELECT COUNT(*) INTO @idx_exists FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = 'edum' AND TABLE_NAME = 'score' AND INDEX_NAME = 'idx_update_user';
SET @sql = IF(@idx_exists = 0, 'ALTER TABLE score ADD INDEX idx_update_user (update_user_id)', 'SELECT ''idx_update_user已存在'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. homework表 - 检查并添加class_id字段
SELECT '=== 修复homework表 ===' AS info;

SELECT COUNT(*) INTO @col_exists FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'edum' AND TABLE_NAME = 'homework' AND COLUMN_NAME = 'class_id';
SET @sql = IF(@col_exists = 0, 'ALTER TABLE homework ADD COLUMN class_id BIGINT COMMENT ''班级ID'' AFTER content', 'SELECT ''class_id已存在'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT COUNT(*) INTO @idx_exists FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = 'edum' AND TABLE_NAME = 'homework' AND INDEX_NAME = 'idx_class_id';
SET @sql = IF(@idx_exists = 0, 'ALTER TABLE homework ADD INDEX idx_class_id (class_id)', 'SELECT ''idx_class_id已存在'' AS info');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. student表 - 检查并添加parent_name和parent_phone（如果不存在）
-- 先检查parent_name是否存在
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'edum' 
AND TABLE_NAME = 'student' 
AND COLUMN_NAME = 'parent_name';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE student ADD COLUMN parent_name VARCHAR(50) COMMENT ''家长姓名'' AFTER address', 
    'SELECT ''parent_name字段已存在'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 再检查parent_phone是否存在
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'edum' 
AND TABLE_NAME = 'student' 
AND COLUMN_NAME = 'parent_phone';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE student ADD COLUMN parent_phone VARCHAR(20) COMMENT ''家长电话'' AFTER parent_name', 
    'SELECT ''parent_phone字段已存在'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. attendance表 - 检查并添加check_in_user_id和check_in_time（如果不存在）
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'edum' 
AND TABLE_NAME = 'attendance' 
AND COLUMN_NAME = 'check_in_user_id';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE attendance ADD COLUMN check_in_user_id BIGINT COMMENT ''签到用户ID'' AFTER remark', 
    'SELECT ''check_in_user_id字段已存在'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'edum' 
AND TABLE_NAME = 'attendance' 
AND COLUMN_NAME = 'check_in_time';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE attendance ADD COLUMN check_in_time DATETIME COMMENT ''签到时间'' AFTER check_in_user_id', 
    'SELECT ''check_in_time字段已存在'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查索引是否存在
SELECT COUNT(*) INTO @idx_exists 
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'edum' 
AND TABLE_NAME = 'attendance' 
AND INDEX_NAME = 'idx_check_in_user';

SET @sql = IF(@idx_exists = 0, 
    'ALTER TABLE attendance ADD INDEX idx_check_in_user (check_in_user_id)', 
    'SELECT ''idx_check_in_user索引已存在'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 验证修复结果
SELECT '✅ 表结构修复完成！' AS result;

-- 检查score表
SELECT '=== score表 ===' AS table_name;
DESC score;

-- 检查homework表
SELECT '=== homework表 ===' AS table_name;
DESC homework;

-- 检查student表
SELECT '=== student表 ===' AS table_name;
DESC student;

-- 检查attendance表
SELECT '=== attendance表 ===' AS table_name;
DESC attendance;
