# API Reference

Base URL: /api. Auth: JWT Bearer token in Authorization header.
Response: { code:200, message:"success", data: {...} }
Error codes: 200=ok, 400=bad request, 401=unauthorized, 403=forbidden, 404=not found, 500=server error
Pagination: ?pageNum=1&pageSize=10 -> { total, list }

## Auth
POST /api/auth/login body:{ username, password } -> { token, userInfo: { id, username, realName, role } }
POST /api/auth/logout -> void
GET /api/auth/current -> SysUser (requires token)

## Dashboard
GET /api/dashboard/statistics -> { totalStudents, totalClasses, todayAttendanceRate, pendingHomeworkCount }
GET /api/dashboard/class-distribution -> [{ className, studentCount }]
GET /api/dashboard/attendance-trend -> [{ date, attendanceRate }]

## Students
GET /api/students?pageNum&pageSize&name&classId -> paginated list
POST /api/students body:{ studentNo, name, gender, birthDate, classId, phone, address, enrollmentDate } -> { id }
PUT /api/students/{id} body:{ ... } -> void
DELETE /api/students/{id} -> void
DELETE /api/students/batch body:[ids] -> void
Validations: studentNo unique 4-12 chars, name 2-20 chars, gender 1/2, phone 11 digits.

## Teachers
GET /api/users?pageNum&pageSize&name&status -> paginated teacher list
POST /api/users body:{ username, password, realName, phone } -> { id }
PUT /api/users/{id} body:{ ... } -> void
DELETE /api/users/{id} -> void

## Classes
GET /api/classes?pageNum&pageSize&grade&teacherId -> paginated list (includes currentStudents count)
POST /api/classes body:{ className, grade, teacherId, classroom, maxStudents, establishDate } -> { id }
PUT /api/classes/{id} body:{ ... } -> void
DELETE /api/classes/{id} -> void (fails if students exist)
Validations: className unique 2-30 chars, grade 1-12, maxStudents 10-100.

## Course Schedule
GET /api/courses/schedule?classId&startDate&endDate -> [{ id, classId, dayOfWeek, period, subject, teacherId, teacherName, classroom, startDate, endDate }]
POST /api/courses/schedule body:{ classId, dayOfWeek, period, subject, teacherId, classroom, startDate, endDate } -> { id }
PUT /api/courses/schedule/{id} body:{ ... } -> void
DELETE /api/courses/schedule/{id} -> void
Conflict detection: same class same time slot conflict, same teacher same time slot conflict.

## Attendance
POST /api/attendance/batch body:{ classId, date, records: [{ studentId, status, remark }] } -> void
GET /api/attendance?classId&date&studentId&pageNum&pageSize -> paginated list
PUT /api/attendance/{id} body:{ status, remark } -> void
GET /api/attendance/statistics?classId&startDate&endDate -> { totalCount, presentCount, lateCount, leaveCount, absentCount, attendanceRate }
Status values: present, late, leave, absent.

## Scores
POST /api/scores/batch body:{ classId, subject, examName, examDate, scores: [{ studentId, scoreValue }] } -> void
GET /api/scores?studentId&classId&subject&examName&pageNum&pageSize -> paginated list
PUT /api/scores/{id} body:{ scoreValue, remark } -> void
GET /api/scores/statistics?classId&subject&examName -> { averageScore, highestScore, lowestScore, passRate, excellentRate, scoreDistribution }
Unique: one score per student+subject+exam.

## Homework
POST /api/homeworks body:{ title, content, classId, subject, deadline, fullScore } -> { id }
GET /api/homeworks?classId&subject&status&pageNum&pageSize -> paginated list (includes submitCount, totalCount)
GET /api/homeworks/{id} -> detail with submissions list
DELETE /api/homeworks/{id} -> void
POST /api/homeworks/{homeworkId}/submit body:{ studentId, submitContent } -> void
PUT /api/homeworks/submissions/{id} body:{ scoreValue, comment } -> void
PUT /api/homeworks/{homeworkId}/batch-grade body:{ defaultScore, submissions: [{ submissionId, scoreValue, comment }] } -> void
GET /api/homeworks/{id}/statistics -> { totalCount, submitCount, gradedCount, completionRate, averageScore, unsubmittedStudents }
Status: ongoing, ended, graded. Auto detects late submission.

## AI Chat
GET /api/ai/chat?message=text -> AI response text
Requires auth token. Connects to local Ollama qwen2.5-coder:7b.
Use this endpoint to ask questions about the system, database schema, or API.

## Common Dropdowns
GET /api/common/classes -> [{ id, className, grade }] (for select dropdowns)
GET /api/common/teachers -> [{ id, realName, role }] (for select dropdowns)
GET /api/common/subjects -> [string array of 13 subjects]
Subjects: 语文, 数学, 英语, 物理, 化学, 生物, 政治, 历史, 地理, 体育, 音乐, 美术, 信息技术

## Business Rules Summary
- All tables use logical delete (is_deleted=1).
- Login fails 5 times locks account for 30 minutes.
- Class delete blocked if students exist.
- Student delete checks pending homework and attendance records.
- Course schedule checks teacher time conflict.
- Same student+same date only one attendance record.
- Same student+same subject+same exam only one score.
- Homework can be resubmitted before deadline, keeps latest.
- Password stored using BCrypt encryption.
- Token expires after 24 hours.
