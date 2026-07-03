-- 性能优化 - 数据库索引优化脚本
-- 执行日期：2026-05-19
-- 说明：添加缺失的索引以提升查询性能

USE edum;

-- ============================================
-- 1. 学生表优化索引
-- ============================================

-- 优化按班级和姓名搜索学生
-- 使用场景：学生列表页面，选择班级后输入姓名搜索
ALTER TABLE student ADD INDEX idx_class_name (class_id, name);

-- 优化按入学日期查询
-- 使用场景：统计某时间段入学的学生
ALTER TABLE student ADD INDEX idx_enrollment_date (enrollment_date);

-- ============================================
-- 2. 考勤表优化索引
-- ============================================

-- 优化按学生和状态查询
-- 使用场景：查看某学生的所有缺勤记录
ALTER TABLE attendance ADD INDEX idx_student_status (student_id, status);

-- 优化按打卡人查询
-- 使用场景：查看某教师的所有打卡记录
ALTER TABLE attendance ADD INDEX idx_checkin_user (checkin_user_id);

-- ============================================
-- 3. 成绩表优化索引
-- ============================================

-- 优化按学生和科目查询
-- 使用场景：查看某学生的所有科目成绩
ALTER TABLE score ADD INDEX idx_student_subject (student_id, subject);

-- 优化按考试名称查询
-- 使用场景：查看某次考试的所有成绩
ALTER TABLE score ADD INDEX idx_exam_name (exam_name);

-- ============================================
-- 4. 作业表优化索引
-- ============================================

-- 优化按班级和状态查询
-- 使用场景：查看某班级的进行中作业
-- 注意：class_ids是逗号分隔的字符串，此索引效果有限
-- 建议：后续改为关联表结构
ALTER TABLE homework ADD INDEX idx_teacher_status (teacher_id, status);

-- 优化按发布时间和状态查询
-- 使用场景：查看最近发布的作业
ALTER TABLE homework ADD INDEX idx_publish_status (publish_time, status);

-- ============================================
-- 5. 作业提交表优化索引
-- ============================================

-- 优化按学生和批改状态查询
-- 使用场景：查看某学生的待批改作业
ALTER TABLE homework_submission ADD INDEX idx_student_score (student_id, score);

-- 优化按批改人查询
-- 使用场景：查看某教师批改的所有作业
ALTER TABLE homework_submission ADD INDEX idx_grader (grader_id);

-- 优化按提交时间查询
-- 使用场景：统计某时间段的提交情况
ALTER TABLE homework_submission ADD INDEX idx_submit_time (submit_time);

-- ============================================
-- 6. 课程表优化索引
-- ============================================

-- 优化按日期范围查询
-- 使用场景：查询某时间段的课程安排
ALTER TABLE course_schedule ADD INDEX idx_date_range (start_date, end_date);

-- ============================================
-- 验证索引创建
-- ============================================

-- 查看所有索引
SHOW INDEX FROM student;
SHOW INDEX FROM attendance;
SHOW INDEX FROM score;
SHOW INDEX FROM homework;
SHOW INDEX FROM homework_submission;
SHOW INDEX FROM course_schedule;

-- 查看索引使用情况（需要实际查询后才能看到统计信息）
SELECT 
    table_name,
    index_name,
    seq_in_index,
    column_name,
    cardinality
FROM information_schema.statistics
WHERE table_schema = 'edum'
ORDER BY table_name, index_name, seq_in_index;

-- ============================================
-- 性能测试查询
-- ============================================

-- 测试1：按班级和姓名搜索学生
EXPLAIN SELECT * FROM student 
WHERE class_id = 1 AND name LIKE '%张%' 
AND is_deleted = 0;

-- 测试2：按学生和状态查询考勤
EXPLAIN SELECT * FROM attendance 
WHERE student_id = 1 AND status = 'absent' 
AND is_deleted = 0;

-- 测试3：按学生和科目查询成绩
EXPLAIN SELECT * FROM score 
WHERE student_id = 1 AND subject = '数学' 
AND is_deleted = 0;

-- 测试4：按教师和状态查询作业
EXPLAIN SELECT * FROM homework 
WHERE teacher_id = 1 AND status = 'ongoing' 
AND is_deleted = 0;

-- 测试5：按学生和得分查询作业提交
EXPLAIN SELECT * FROM homework_submission 
WHERE student_id = 1 AND score IS NULL 
AND is_deleted = 0;

-- ============================================
-- 注意事项
-- ============================================

-- 1. 执行前请备份数据库
-- 2. 在低峰期执行，避免影响业务
-- 3. 大表添加索引可能需要较长时间
-- 4. 索引会增加存储空间和维护成本
-- 5. 定期分析索引使用情况，删除无用索引

-- 查看表大小和索引大小
SELECT 
    table_name,
    ROUND(data_length / 1024 / 1024, 2) AS data_size_mb,
    ROUND(index_length / 1024 / 1024, 2) AS index_size_mb,
    ROUND((data_length + index_length) / 1024 / 1024, 2) AS total_size_mb
FROM information_schema.tables
WHERE table_schema = 'edum'
ORDER BY total_size_mb DESC;

-- 查看索引使用统计（MySQL 5.7+）
SELECT 
    object_schema,
    object_name,
    index_name,
    count_star,
    count_read,
    count_write
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = 'edum'
AND index_name IS NOT NULL
ORDER BY count_star DESC;
