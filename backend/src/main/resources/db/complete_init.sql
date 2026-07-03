-- ============================================
-- 教学数字平台后台管理系统 - 完整数据库初始化脚本
-- ============================================
-- 版本：V2.0
-- 更新日期：2026-05-19
-- 说明：包含所有表结构和优化索引
-- 使用方法：mysql -u root -p < complete_init.sql
-- ============================================

-- 删除旧数据库（可选，谨慎使用）
-- DROP DATABASE IF EXISTS edum;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS edum DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE edum;

-- ============================================
-- 1. 系统用户表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'teacher' COMMENT '角色（admin-管理员，teacher-教师）',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
    locked_until DATETIME COMMENT '锁定截止时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ============================================
-- 2. 班级表
-- ============================================
CREATE TABLE IF NOT EXISTS class_entity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    class_name VARCHAR(50) NOT NULL UNIQUE COMMENT '班级名称',
    grade INT NOT NULL COMMENT '年级（1-12）',
    teacher_id BIGINT COMMENT '负责教师ID',
    classroom VARCHAR(50) COMMENT '教室位置',
    max_capacity INT NOT NULL DEFAULT 50 COMMENT '最大人数',
    establish_date DATE COMMENT '成立日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    INDEX idx_grade (grade),
    INDEX idx_teacher_id (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- ============================================
-- 3. 学生表
-- ============================================
CREATE TABLE IF NOT EXISTS student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT NOT NULL COMMENT '性别（1-男，2-女）',
    birth_date DATE COMMENT '出生日期',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(500) COMMENT '家庭住址',
    enrollment_date DATE COMMENT '入学日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    -- 原有索引
    INDEX idx_student_no (student_no),
    INDEX idx_name (name),
    INDEX idx_class_id (class_id),
    -- 优化索引
    INDEX idx_class_name (class_id, name),
    INDEX idx_enrollment_date (enrollment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- ============================================
-- 4. 课程表
-- ============================================
CREATE TABLE IF NOT EXISTS course_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    day_of_week TINYINT NOT NULL COMMENT '星期（1-周一，5-周五）',
    period TINYINT NOT NULL COMMENT '节次（1-8）',
    subject VARCHAR(50) NOT NULL COMMENT '科目',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    classroom VARCHAR(50) COMMENT '教室',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    -- 原有索引
    INDEX idx_class_id (class_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_day_period (day_of_week, period),
    -- 优化索引
    INDEX idx_date_range (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- ============================================
-- 5. 考勤表
-- ============================================
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    status VARCHAR(20) NOT NULL DEFAULT 'present' COMMENT '状态（present-出勤，late-迟到，leave-请假，absent-缺勤）',
    remark VARCHAR(500) COMMENT '备注',
    checkin_user_id BIGINT COMMENT '打卡人ID',
    checkin_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    -- 原有索引
    UNIQUE KEY uk_student_date (student_id, attendance_date),
    INDEX idx_class_date (class_id, attendance_date),
    INDEX idx_attendance_date (attendance_date),
    -- 优化索引
    INDEX idx_student_status (student_id, status),
    INDEX idx_checkin_user (checkin_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤表';

-- ============================================
-- 6. 成绩表
-- ============================================
CREATE TABLE IF NOT EXISTS score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    subject VARCHAR(50) NOT NULL COMMENT '科目',
    exam_name VARCHAR(100) NOT NULL COMMENT '考试名称',
    score DECIMAL(5,2) NOT NULL COMMENT '成绩',
    full_score DECIMAL(5,2) NOT NULL DEFAULT 100 COMMENT '满分',
    exam_date DATE NOT NULL COMMENT '考试日期',
    rank INT COMMENT '排名',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    -- 原有索引
    UNIQUE KEY uk_student_subject_exam (student_id, subject, exam_name),
    INDEX idx_class_subject (class_id, subject),
    INDEX idx_exam_date (exam_date),
    -- 优化索引
    INDEX idx_student_subject (student_id, subject),
    INDEX idx_exam_name (exam_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- ============================================
-- 7. 作业表
-- ============================================
CREATE TABLE IF NOT EXISTS homework (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '作业标题',
    content TEXT NOT NULL COMMENT '作业内容',
    class_ids VARCHAR(500) NOT NULL COMMENT '布置班级ID列表（逗号分隔）',
    subject VARCHAR(50) NOT NULL COMMENT '科目',
    teacher_id BIGINT NOT NULL COMMENT '布置教师ID',
    deadline DATETIME NOT NULL COMMENT '截止时间',
    full_score INT NOT NULL DEFAULT 100 COMMENT '满分分值',
    attachment_url VARCHAR(500) COMMENT '附件URL',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    status VARCHAR(20) DEFAULT 'ongoing' COMMENT '状态（ongoing-进行中，ended-已结束，graded-已批改）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    -- 原有索引
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_subject (subject),
    INDEX idx_status (status),
    INDEX idx_deadline (deadline),
    -- 优化索引
    INDEX idx_teacher_status (teacher_id, status),
    INDEX idx_publish_status (publish_time, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- ============================================
-- 8. 作业提交表
-- ============================================
CREATE TABLE IF NOT EXISTS homework_submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    homework_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    submit_time DATETIME COMMENT '提交时间',
    content TEXT COMMENT '提交内容',
    attachment_url VARCHAR(500) COMMENT '提交附件URL',
    is_late TINYINT DEFAULT 0 COMMENT '是否迟交（0-否，1-是）',
    score DECIMAL(5,2) COMMENT '得分',
    comment VARCHAR(1000) COMMENT '评语',
    grader_id BIGINT COMMENT '批改人ID',
    grade_time DATETIME COMMENT '批改时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    -- 原有索引
    UNIQUE KEY uk_homework_student (homework_id, student_id),
    INDEX idx_student_id (student_id),
    INDEX idx_homework_id (homework_id),
    -- 优化索引
    INDEX idx_student_score (student_id, score),
    INDEX idx_grader (grader_id),
    INDEX idx_submit_time (submit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- ============================================
-- 插入初始数据
-- ============================================

-- 插入初始管理员账号（密码：admin123，BCrypt加密后）
INSERT INTO sys_user (username, password, real_name, role, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin', 1)
ON DUPLICATE KEY UPDATE username=username;

-- ============================================
-- 验证数据库创建
-- ============================================

-- 查看所有表
SHOW TABLES;

-- 查看表结构
DESCRIBE sys_user;
DESCRIBE class_entity;
DESCRIBE student;
DESCRIBE course_schedule;
DESCRIBE attendance;
DESCRIBE score;
DESCRIBE homework;
DESCRIBE homework_submission;

-- 查看所有索引
SELECT 
    table_name,
    index_name,
    column_name,
    seq_in_index,
    non_unique
FROM information_schema.statistics
WHERE table_schema = 'edum'
ORDER BY table_name, index_name, seq_in_index;

-- 查看初始用户
SELECT id, username, real_name, role, status FROM sys_user;

-- ============================================
-- 完成提示
-- ============================================
SELECT '✅ 数据库初始化完成！' AS message;
SELECT '📊 共创建8张表' AS info;
SELECT '👤 已创建管理员账号：admin / admin123' AS account_info;
