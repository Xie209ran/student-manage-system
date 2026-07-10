# EDUM 教学数字平台后台管理系统

面向学校和教育机构的综合教务管理平台。涵盖学生、班级、课程排课、考勤打卡、成绩管理、作业管理全流程，并集成基于本地大模型的 AI 教学对话助手。

---

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Spring Boot 3.2.0 | Java 21，RESTful 架构 |
| ORM | MyBatis 3.0.3 | XML 映射，手写 SQL，参数化防注入 |
| 数据库 | MySQL 8 | InnoDB，utf8mb4，8 张业务表 |
| 认证鉴权 | JWT + Spring Security | 无状态 Token，BCrypt 密码加密 |
| AI | Spring AI 1.0.0-M6 + Ollama | 本地部署 qwen2.5-coder 7B 模型 |
| 前端框架 | Vue 3 (CDN) + Element Plus + ECharts | SPA 单页应用，无需构建工具 |
| HTTP 客户端 | Axios | 请求拦截器自动带 Token，响应拦截器统一处理错误 |
| 构建工具 | Maven | 多环境配置，依赖版本统一管理 |

---

## 功能模块

### 1. 登录模块

用户通过用户名和密码登录，系统生成 JWT Token，后续请求通过 Authorization 头携带。支持连续登录失败锁定（5 次锁定 30 分钟）、Token 过期自动跳转登录页。

| 功能 | 说明 |
|------|------|
| 表单校验 | 用户名不为空、密码不少于 6 位 |
| 账户锁定 | 连续失败 5 次锁定 30 分钟 |
| Token 管理 | 有效期 24 小时，无状态 |
| 密码加密 | BCrypt 加密存储 |

### 2. 仪表盘

首页以可视化方式展示教务数据概况。包含 4 个统计数字卡片和 2 个 ECharts 图表。

| 统计卡片 | 说明 |
|---------|------|
| 学生总数 | 未删除的学生记录数 |
| 班级总数 | 未删除的班级记录数 |
| 今日出勤率 | （出勤人数 + 迟到人数）/ 应到人数 |
| 待批改作业数 | 已提交未批改的作业数量 |

图表：
- **班级人数分布柱状图** — 按学生数降序排列，不同班级不同颜色
- **近 7 天出勤趋势折线图** — 展示每日出勤率变化趋势

### 3. 学生管理

对学生信息进行全生命周期管理。

| 操作 | 说明 |
|------|------|
| 学生列表 | 表格展示，支持分页（10/20/50 条每页） |
| 搜索 | 按姓名模糊匹配 |
| 筛选 | 按下拉选择班级过滤 |
| 新增 | 表单校验：学号唯一（4-12 位）、姓名 2-20 字、手机号 11 位 |
| 编辑 | 回填原有数据，修改后保存 |
| 删除 | 逻辑删除（is_deleted 标记），删除前检查待批改作业和考勤记录 |
| 批量删除 | 勾选多条后批量操作，二次确认 |

### 4. 教师管理

教师账号的创建、编辑、查询和维护。密码通过 BCrypt 加密存储，列表返回时清除密码字段。

### 5. 班级管理

| 操作 | 说明 |
|------|------|
| 班级列表 | 显示班级名称、年级、负责教师、学生数/最大人数、成立日期 |
| 容量预警 | 学生数达到最大容量 90% 时显示黄色/红色预警标记 |
| 新增班级 | 班级名称唯一校验、年级 1-12、最大人数 10-100 |
| 删除校验 | 有学生时禁止删除，有课程时提示先取消 |

### 6. 课程排课

以周课表形式展示班级课程安排。

| 操作 | 说明 |
|------|------|
| 课表查看 | 行=第 1-8 节，列=周一至周五 |
| 添加课程 | 选择班级、星期、节次、科目、教师、教室 |
| 冲突检测 | 同一班级同一时段不能有两门课；同一教师同一时段不能跨班 |
| 颜色区分 | 不同科目用不同背景色 |

### 7. 考勤打卡

| 操作 | 说明 |
|------|------|
| 批量打卡 | 选择班级和日期，列出所有学生，批量设置状态 |
| 状态 | 出勤 / 迟到 / 请假 / 缺勤 |
| 统计 | 按班级/日期/学生维度统计出勤率 |
| 去重 | 同一学生同一日期只有一条记录，重复打卡提示是否覆盖 |

### 8. 成绩管理

| 操作 | 说明 |
|------|------|
| 批量录入 | 按班级+考试名称+科目批量录入成绩 |
| 统计分析 | 平均分、最高分、最低分、及格率、优秀率、分数段分布 |
| 去重校验 | 同一学生同一科目同一考试只能有一条记录，重复录入提示覆盖 |

### 9. 作业管理

| 操作 | 说明 |
|------|------|
| 发布作业 | 填写标题、内容、班级、科目、截止时间 |
| 提交作业 | 学生提交，自动判断是否迟交 |
| 批改作业 | 打分 + 写评语，支持批量给分 |
| 统计 | 完成率、按时完成率、平均分、未提交名单 |
| 重提 | 截止时间前可多次提交，保留最后一次 |

### 10. AI 教学助手

| 功能 | 说明 |
|------|------|
| 对话界面 | 类 ChatGPT 聊天 UI，消息气泡 + 打字动画 |
| 模型 | 本地 Ollama 部署 qwen2.5-coder 7B |
| 场景 | 教务问答、教学建议、代码辅助等 |
| 隐私 | 数据不离开本机，无需联网 |

---

## 项目结构

```
EDUM/
├── front/                          # 前端 SPA
│   ├── index.html                  # 入口页面（CDN 引入 Vue 3 / Element Plus）
│   ├── css/style.css               # 全局样式
│   ├── js/
│   │   ├── app.js                  # 主应用（组件注册、路由、布局）
│   │   ├── http.js                 # Axios 封装（拦截器、Token、错误处理）
│   │   ├── login.js                # 登录页组件
│   │   ├── dashboard.js            # 仪表盘组件
│   │   ├── student.js              # 学生管理组件
│   │   ├── teacher.js              # 教师管理组件
│   │   ├── class.js                # 班级管理组件
│   │   ├── course.js               # 课程排课组件
│   │   ├── attendance.js           # 考勤管理组件
│   │   ├── score.js                # 成绩管理组件
│   │   ├── homework.js             # 作业管理组件
│   │   └── chat.js                 # AI 对话组件
│   ├── test.html                   # 测试页面
│   └── performance-test.html       # 性能测试工具
│
├── backend/                        # Spring Boot API
│   ├── pom.xml
│   └── src/main/java/com/edum/
│       ├── EdumApplication.java    # 启动类
│       ├── common/
│       │   └── Result.java         # 统一返回结果包装
│       ├── config/
│       │   ├── SecurityConfig.java # Spring Security 配置
│       │   ├── JwtInterceptor.java # JWT 认证拦截器
│       │   ├── WebConfig.java      # Web 配置（拦截器注册）
│       │   └── CorsConfig.java     # 跨域配置
│       ├── controller/             # REST 控制器
│       │   ├── AuthController.java
│       │   ├── DashboardController.java
│       │   ├── StudentController.java
│       │   ├── ClassController.java
│       │   ├── CourseController.java
│       │   ├── AttendanceController.java
│       │   ├── ScoreController.java
│       │   ├── HomeworkController.java
│       │   ├── UserController.java
│       │   ├── CommonController.java
│       │   ├── NoticeController.java
│       │   └── AIChatController.java
│       ├── service/                # 业务接口
│       ├── service/impl/           # 业务实现
│       ├── mapper/                 # MyBatis Mapper 接口
│       ├── entity/                 # 实体类（继承 BaseEntity）
│       ├── util/
│       │   └── JwtUtil.java        # JWT 工具类
│       └── resources/
│           ├── application.properties
│           ├── mapper/             # MyBatis XML 映射文件
│           └── db/                 # 数据库初始化脚本
│
├── doc/                            # 项目文档
│   ├── init.sql                    # 建库建表 SQL
│   ├── 需求文档.md
│   ├── 数据库设计.md
│   ├── 接口文档.md
│   └── 开发计划.md
│
└── README.md
```

---

## 数据库设计

8 张业务表，统一包含审计字段（`id`, `create_time`, `update_time`, `is_deleted`），全部使用 InnoDB 引擎 + utf8mb4 字符集。

### 表关系

```
sys_user（用户）
    │
    ├── 1:N → class_entity（班级）     ← teacher_id
    │              │
    │              └── 1:N → student（学生） ← class_id
    │                              │
    │                              ├── 1:N → attendance（考勤）
    │                              ├── 1:N → score（成绩）
    │                              └── 1:N → homework_submission（作业提交）
    │
    └── 1:N → course_schedule（课程） ← teacher_id
    └── 1:N → homework（作业）         ← teacher_id
```

### 索引优化

| 表 | 索引数 | 关键索引 |
|----|--------|---------|
| sys_user | 4 | `uk_username`（唯一）、`idx_role` |
| class_entity | 4 | `uk_class_name`（唯一）、`idx_teacher_id` |
| student | 5 | `uk_student_no`（唯一）、`idx_class_id`（高频） |
| course_schedule | 5 | `idx_class_id`、`idx_teacher_id`、联合索引 `idx_day_period` |
| attendance | 5 | `uk_student_date`（唯一）、`idx_attendance_date` |
| score | 6 | `uk_student_subject_exam`（唯一）、`idx_student_id` |
| homework | 6 | `idx_class_id`、`idx_deadline` |
| homework_submission | 6 | `uk_homework_student`（唯一）、`idx_homework_id` |

---

## API 接口设计

### 通用规范

- **基础路径**: `/api`
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: JWT Bearer Token
- **分页参数**: `?pageNum=1&pageSize=10`

### 统一返回格式

```json
// 成功
{ "code": 200, "message": "success", "data": { ... } }

// 失败
{ "code": 400, "message": "错误描述", "data": null }
```

### 接口列表

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 认证 | `/api/auth/login` | POST | 登录 |
| | `/api/auth/logout` | POST | 退出登录 |
| | `/api/auth/current` | GET | 获取当前用户信息 |
| 仪表盘 | `/api/dashboard/statistics` | GET | 统计数据 |
| | `/api/dashboard/class-distribution` | GET | 班级人数分布 |
| | `/api/dashboard/attendance-trend` | GET | 近 7 天出勤趋势 |
| 学生 | `/api/students` | GET | 分页列表（支持搜索+筛选） |
| | `/api/students` | POST | 新增 |
| | `/api/students/{id}` | PUT | 更新 |
| | `/api/students/{id}` | DELETE | 删除 |
| | `/api/students/batch` | DELETE | 批量删除 |
| 班级 | `/api/classes` | GET/POST | 列表 / 新增 |
| | `/api/classes/{id}` | PUT/DELETE | 更新 / 删除 |
| 课程 | `/api/courses/schedule?classId=` | GET | 查看课表 |
| | `/api/courses/schedule` | POST | 添加课程 |
| | `/api/courses/schedule/{id}` | PUT/DELETE | 更新 / 删除 |
| 考勤 | `/api/attendance/batch` | POST | 批量打卡 |
| | `/api/attendance` | GET | 考勤记录列表 |
| | `/api/attendance/statistics` | GET | 考勤统计 |
| 成绩 | `/api/scores/batch` | POST | 批量录入 |
| | `/api/scores` | GET | 成绩列表 |
| | `/api/scores/statistics` | GET | 成绩统计 |
| 作业 | `/api/homeworks` | GET/POST | 列表 / 发布 |
| | `/api/homeworks/{id}` | DELETE | 删除 |
| | `/api/homeworks/{id}/submit` | POST | 提交作业 |
| | `/api/homeworks/submissions/{id}` | PUT | 批改 |
| | `/api/homeworks/{id}/statistics` | GET | 作业统计 |
| AI | `/api/ai/chat?message=` | GET | AI 对话 |
| 通用 | `/api/common/classes` | GET | 班级下拉列表 |
| | `/api/common/teachers` | GET | 教师下拉列表 |
| | `/api/common/subjects` | GET | 科目列表 |

### 错误码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或 Token 过期 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 快速开始

### 环境要求

- JDK 21+
- MySQL 8+
- Maven 3.9+
- Ollama（AI 对话功能需要）

### 1. 初始化数据库

```bash
mysql -u root -p < backend/src/main/resources/db/init.sql
```

### 2. 配置数据库连接

编辑 `backend/src/main/resources/application.properties`，确认数据库密码：

```properties
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

服务启动在 `http://localhost:8080`，控制台看到 `Started EdumApplication in 2.6 seconds` 即成功。

### 4. 启动 AI 模型

```bash
ollama pull qwen2.5-coder:7b
ollama run qwen2.5-coder:7b
```

### 5. 打开前端

浏览器打开 `front/index.html`。

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 系统管理员 |

---

## AI 教学助手

基于 Ollama + qwen2.5-coder 7B 本地部署，数据不离开本机。

### 使用方式

1. 确保 Ollama 已启动：`ollama run qwen2.5-coder:7b`
2. 登录系统（admin / admin123）
3. 左侧菜单点击 **AI Chat**
4. 输入问题，按回车或点 Send 发送

### 适用场景

- 教学问题解答
- 课程设计建议
- 代码编写辅助
- 教务数据查询

---

## 开发说明

### 分层架构

```
Controller → Service → Mapper → MySQL
    ↓           ↓        ↓
  接收请求    业务逻辑    SQL 执行
  参数校验    规则校验    数据访问
  结果返回    异常处理
```

### 认证流程

```
客户端请求 → JwtInterceptor → 解析 Token → 放行 / 返回 401
  ↑                            ↓
  携带 Token                将 userId 写入 Request
  Authorization: Bearer ...
```

### 前端数据流

```
页面组件 → http.js (Axios) → 后端 API → MySQL
              ↓
    请求拦截器: 自动带 Token
    响应拦截器: 统一处理 code / 401 跳登录
```

---

## License

MIT
