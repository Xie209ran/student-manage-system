# Database Schema

## sys_user (System Users)
Columns: id(BIGINT PK), username(VARCHAR 50 UNIQUE), password(VARCHAR 255), real_name(VARCHAR 50), role(VARCHAR 20), phone(VARCHAR 20), email(VARCHAR 100), avatar(VARCHAR 255), login_fail_count(INT), locked_until(DATETIME), last_login_time(DATETIME), status(TINYINT 1=active), create_time(DATETIME), update_time(DATETIME), is_deleted(TINYINT)
Notes: admin / teacher roles. Password BCrypt encrypted. status 0=disabled 1=enabled.

## class_entity (Classes)
Columns: id(BIGINT PK), class_name(VARCHAR 50 UNIQUE), grade(INT 1-12), teacher_id(BIGINT FK ref sys_user), classroom(VARCHAR 50), max_capacity(INT default 50), establish_date(DATE), create_time, update_time, is_deleted
Notes: FK teacher_id -> sys_user.id. Logical delete.

## student (Students)
Columns: id(BIGINT PK), student_no(VARCHAR 20 UNIQUE), name(VARCHAR 50), gender(TINYINT 1=male 2=female), birth_date(DATE), class_id(BIGINT FK ref class_entity), phone(VARCHAR 20), address(VARCHAR 500), enrollment_date(DATE), create_time, update_time, is_deleted
Notes: FK class_id -> class_entity.id. gender 1=male 2=female.

## course_schedule (Course Schedule)
Columns: id(BIGINT PK), class_id(BIGINT FK), day_of_week(TINYINT 1=Mon-5=Fri), period(TINYINT 1-8), subject(VARCHAR 50), teacher_id(BIGINT FK), classroom(VARCHAR 50), start_date(DATE), end_date(DATE), create_time, update_time, is_deleted
Notes: FK class_id -> class_entity.id, FK teacher_id -> sys_user.id. day_of_week 1=Monday. period 1-8.

## attendance (Attendance)
Columns: id(BIGINT PK), student_id(BIGINT FK), class_id(BIGINT FK), attendance_date(DATE), status(VARCHAR 20), remark(VARCHAR 500), checkin_user_id(BIGINT), checkin_time(DATETIME), create_time, update_time, is_deleted
Notes: UNIQUE(student_id, attendance_date). status values: present, late, leave, absent.

## score (Scores)
Columns: id(BIGINT PK), student_id(BIGINT FK), class_id(BIGINT FK), subject(VARCHAR 50), exam_name(VARCHAR 100), score(DECIMAL 5,2), full_score(DECIMAL 5,2), exam_date(DATE), rank(INT), remark(VARCHAR 500), create_time, update_time, is_deleted
Notes: UNIQUE(student_id, subject, exam_name).

## homework (Homework)
Columns: id(BIGINT PK), title(VARCHAR 200), content(TEXT), class_ids(VARCHAR 500), subject(VARCHAR 50), teacher_id(BIGINT FK), deadline(DATETIME), full_score(INT), attachment_url(VARCHAR 500), publish_time(DATETIME), status(VARCHAR 20), create_time, update_time, is_deleted
Notes: status values: ongoing, ended, graded. class_ids is comma-separated.

## homework_submission (Homework Submission)
Columns: id(BIGINT PK), homework_id(BIGINT FK), student_id(BIGINT FK), submit_time(DATETIME), content(TEXT), attachment_url(VARCHAR 500), is_late(TINYINT 0=no 1=yes), score(DECIMAL 5,2), comment(VARCHAR 1000), grader_id(BIGINT), grade_time(DATETIME), create_time, update_time, is_deleted
Notes: UNIQUE(homework_id, student_id). is_late 0=on-time 1=late.

## Relationship Summary
- sys_user 1:N -> class_entity (teacher_id)
- class_entity 1:N -> student (class_id)
- student 1:N -> attendance (student_id)
- student 1:N -> score (student_id)
- student 1:N -> homework_submission (student_id)
- class_entity 1:N -> course_schedule (class_id)
- sys_user 1:N -> course_schedule (teacher_id)
- homework 1:N -> homework_submission (homework_id)
