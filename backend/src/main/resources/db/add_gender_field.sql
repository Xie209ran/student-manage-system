-- Add gender field to sys_user table
USE edum;

-- Check if gender field exists, add if not exists
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'edum' 
AND TABLE_NAME = 'sys_user' 
AND COLUMN_NAME = 'gender';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE sys_user ADD COLUMN gender TINYINT COMMENT "Gender (1-male, 0-female)" AFTER real_name', 
    'SELECT "gender field already exists" AS info');
PREPARE stmt FROM @sql; 
EXECUTE stmt; 
DEALLOCATE PREPARE stmt;

-- Verify result
SELECT 
    COLUMN_NAME as 'Field_Name',
    COLUMN_TYPE as 'Data_Type',
    COLUMN_COMMENT as 'Comment',
    IS_NULLABLE as 'Nullable'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'edum'
AND TABLE_NAME = 'sys_user'
ORDER BY ORDINAL_POSITION;
