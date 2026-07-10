# EDUM - Teaching Management Platform

A full-stack teaching management system for schools. Manages students, classes, course schedules, attendance, scores, homework, and provides an AI-powered teaching assistant.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.2, MyBatis, MySQL, Spring Security, JWT, BCrypt |
| AI | Spring AI 1.0, Ollama, qwen2.5-coder 7B |
| Frontend | Vue 3, Element Plus, ECharts, Axios |
| Build | Maven |

---

## Features

- **Dashboard** — statistics cards, class size distribution chart, 7-day attendance trend
- **Student Management** — CRUD with search, filtering, pagination, batch delete
- **Teacher Management** — teacher listing and maintenance
- **Class Management** — CRUD with live student count and capacity alerts
- **Course Scheduling** — weekly timetable with teacher/time conflict detection
- **Attendance Recording** — batch check-in, status tracking (present/late/leave/absent)
- **Score Management** — batch entry, statistics (average, pass rate, distribution)
- **Homework Management** — publish, submit, grade, auto late-judgment, batch grading
- **AI Chat Assistant** — local LLM-powered Q&A for teaching scenarios

---

## Architecture

```
front/               Vue 3 SPA (CDN-based, no build step)
  index.html         Entry point
  css/style.css      Styles
  js/                Page components + http.js wrapper

backend/             Spring Boot API
  controller/        REST controllers
  service/           Business logic
  mapper/            MyBatis mapper interfaces + XML
  entity/            POJOs
  config/            Security, JWT, CORS
  util/              JWT utility
  common/Result.java Unified response

doc/                 Documentation and DB init scripts
```

---

## Database

8 tables with soft delete and audit fields:

| Table | Description |
|-------|-------------|
| `sys_user` | System users with role-based access |
| `class_entity` | Classes linked to a teacher |
| `student` | Students linked to a class |
| `course_schedule` | Weekly timetable |
| `attendance` | Daily attendance records |
| `score` | Exam scores with audit |
| `homework` | Homework assignments |
| `homework_submission` | Student submissions |

---

## Quick Start

### Prerequisites

- Java 21+, MySQL 8+, Maven
- Ollama (for AI chat)

### 1. Init Database

```bash
mysql -u root -p < backend/src/main/resources/db/init.sql
```

Default admin: `admin` / `admin123`

### 2. Start Backend

```bash
cd backend
mvn spring-boot:run
```

API at `http://localhost:8080`

### 3. Start Ollama (for AI Chat)

```bash
ollama pull qwen2.5-coder:7b
ollama run qwen2.5-coder:7b
```

### 4. Open Frontend

Open `front/index.html` in a browser.

---

## API Overview

- **Base**: `/api`
- **Auth**: JWT Bearer token
- **Response**: `{ code, message, data }`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/login` | POST | Login |
| `/api/dashboard/statistics` | GET | Dashboard stats |
| `/api/students` | GET/POST | List / Create |
| `/api/students/{id}` | PUT/DELETE | Update / Delete |
| `/api/classes` | GET/POST | List / Create |
| `/api/courses/schedule` | GET/POST | View / Add course |
| `/api/attendance/batch` | POST | Batch check-in |
| `/api/scores/batch` | POST | Batch score entry |
| `/api/homeworks` | GET/POST | List / Create |
| `/api/ai/chat` | GET | AI chat |

---

## AI Chat

Powered by Ollama + qwen2.5-coder 7B running locally. Click **AI Chat** in the sidebar after login. The model runs entirely on your machine.

---

## License

MIT
