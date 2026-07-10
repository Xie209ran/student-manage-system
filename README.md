# EDUM 教学数字平台后台管理系统

面向学校和教育机构的综合教务管理平台，涵盖学生、班级、课程、考勤、成绩、作业全流程管理，并集成 AI 教学对话助手。

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 21, Spring Boot 3.2, MyBatis, MySQL, Spring Security, JWT, BCrypt |
| AI   | Spring AI 1.0, Ollama, qwen2.5-coder 7B |
| 前端 | Vue 3, Element Plus, ECharts, Axios |
| 构建 | Maven |

---

## 功能模块

- **仪表盘** — 统计卡片、班级人数分布图、近 7 天出勤趋势
- **学生管理** — 增删改查、姓名搜索、班级筛选、分页、批量删除
- **教师管理** — 教师列表与维护
- **班级管理** — 增删改查、实时人数统计、满员预警
- **课程排课** — 周课表展示、教师/时间冲突检测
- **考勤打卡** — 批量打卡、状态追踪（出勤/迟到/请假/缺勤）
- **成绩管理** — 批量录入、统计分析（平均分/及格率/分数分布）
- **作业管理** — 发布/提交/批改、自动判断迟交、批量给分
- **AI 教学助手** — 基于本地大模型的智能问答

---

## 项目结构

```
front/               前端（Vue 3 SPA，CDN 引入，无需构建）
  index.html         入口
  css/style.css      样式
  js/                页面组件 + http.js 封装

backend/             Spring Boot API
  controller/        REST 控制器
  service/           业务逻辑层
  mapper/            MyBatis Mapper 接口 + XML
  entity/            实体类
  config/            安全、JWT、CORS 配置
  util/              JWT 工具类
  common/Result.java 统一返回格式

doc/                 文档和数据库初始化脚本
```

---

## 数据库

8 张表，统一支持逻辑删除和时间审计字段：

| 表名 | 说明 |
|------|------|
| `sys_user` | 系统用户（管理员/教师），角色权限 |
| `class_entity` | 班级，关联负责教师 |
| `student` | 学生，关联所属班级 |
| `course_schedule` | 课程安排（星期+节次） |
| `attendance` | 每日考勤记录 |
| `score` | 考试成绩，支持审核 |
| `homework` | 作业发布 |
| `homework_submission` | 作业提交与批改 |

---

## 快速开始

### 环境要求

- Java 21+
- MySQL 8+
- Maven
- Ollama（AI 对话功能需要）

### 1. 初始化数据库

```bash
mysql -u root -p < backend/src/main/resources/db/init.sql
```

默认管理员账号：`admin` / `admin123`

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

API 地址：`http://localhost:8080`

### 3. 启动 Ollama（AI 对话功能）

```bash
ollama pull qwen2.5-coder:7b
ollama run qwen2.5-coder:7b
```

### 4. 打开前端

浏览器直接打开 `front/index.html` 即可。

---

## 接口说明

- **基础路径**: `/api`
- **认证方式**: JWT Bearer Token（放 Authorization 头）
- **返回格式**: 统一 `{ code, message, data }`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 登录 |
| `/api/dashboard/statistics` | GET | 仪表盘统计数据 |
| `/api/students` | GET/POST | 学生列表 / 新增 |
| `/api/students/{id}` | PUT/DELETE | 更新 / 删除学生 |
| `/api/classes` | GET/POST | 班级列表 / 新增 |
| `/api/courses/schedule` | GET/POST | 查看 / 添加课程 |
| `/api/attendance/batch` | POST | 批量打卡 |
| `/api/scores/batch` | POST | 批量录入成绩 |
| `/api/homeworks` | GET/POST | 作业列表 / 发布 |
| `/api/ai/chat` | GET | AI 对话（需登录） |

---

## AI 教学助手

基于 Ollama + qwen2.5-coder 7B 本地模型，无需联网。登录后点击左侧 **AI Chat** 即可使用，支持教务场景下的各类问答。

---

## License

MIT
