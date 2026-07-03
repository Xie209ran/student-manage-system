-- 检查score和homework表的当前结构
USE edum;

-- 查看score表结构
SELECT '=== score表结构 ===' AS info;
DESC score;

-- 查看homework表结构
SELECT '=== homework表结构 ===' AS info;
DESC homework;

-- 检查是否有pass_score字段
SELECT '=== 检查score表的pass_score字段 ===' AS info;
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'edum'
AND TABLE_NAME = 'score'
AND COLUMN_NAME = 'pass_score';

-- 检查是否有class_id字段
SELECT '=== 检查homework表的class_id字段 ===' AS info;
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'edum'
AND TABLE_NAME = 'homework'
AND COLUMN_NAME = 'class_id';
