<div align="center">

#  MeetMind AI

**Turn messy meetings into decisions, summaries, and tasks that actually get done.**

Drop in a transcript — or just the audio — and MeetMind transcribes it, reads it,
and hands you back a clean summary, the key decisions, and a tracked list of
action items with owners and due dates.

[![CI](https://github.com/Shruti2026/MeetMind/actions/workflows/ci.yml/badge.svg)](https://github.com/Shruti2026/MeetMind/actions/workflows/ci.yml)
[![CD](https://github.com/Shruti2026/MeetMind/actions/workflows/cd.yml/badge.svg)](https://github.com/Shruti2026/MeetMind/actions/workflows/cd.yml)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F)
![React](https://img.shields.io/badge/React-18-61DAFB)
![Postgres](https://img.shields.io/badge/PostgreSQL-16-336791)

</div>

---

## Why MeetMind?

Every team already records meetings. Almost no team turns those recordings into
follow-through. MeetMind closes that gap: the AI pass doesn't just summarise, it
extracts **who owes what by when**, and those become first-class tasks you can
tick off from a dashboard.

- 🎙️ **Audio or text in** — upload a recording (up to 25 MB) or paste a transcript.
- ✍️ **Whisper transcription** — audio becomes text automatically.
- 🤖 **Gemini analysis** — summary, key decisions, and structured action items.
- ✅ **Task tracking** — every action item is a task with an assignee, due date, and status.
- 📊 **Dashboard** — meeting and task counts, upcoming deadlines, recent meetings at a glance.
- 🔐 **JWT auth** — every meeting is scoped to the user who created it.

---

## How it works

```
  Upload audio ─┐
                ├─▶  Transcript  ─▶  Gemini 1.5 Flash  ─▶  Summary
  Paste text  ──┘    (Whisper)          analysis           Key decisions
                                                           Action items ─▶ Tasks
```

A meeting moves through `PENDING → PROCESSING → PROCESSED` (or `FAILED` if the AI
call errors out — handled by a global exception handler, not a stack trace in the UI).

---

## Tech Stack

| Layer | Stack |
|---|---|
| **Backend** | Java 21, Spring Boot 3.3, Spring Security + JWT (jjwt), Spring Data JPA, Bean Validation, Lombok, springdoc-openapi |
| **Frontend** | React 18, Vite 5, Tailwind CSS 3, React Router 6, Axios |
| **Database** | PostgreSQL 16 |
| **AI** | Google Gemini 1.5 Flash (analysis), OpenAI Whisper (`whisper-1`, speech-to-text) |
| **Infra** | Docker, Docker Compose, GitHub Actions, GHCR |

---

## Quick Start

```bash
git clone git@github.com:Shruti2026/MeetMind.git
cd MeetMind

# add your API keys (both optional for browsing the app, required for AI features)
cat > .env <<'ENV'
GEMINI_API_KEY=your-gemini-key
OPENAI_API_KEY=your-openai-key
JWT_SECRET=some-long-random-string
ENV

docker compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | `localhost:5432` (`meetmind` / `meetmind`) |

The frontend container runs Vite in dev mode with hot reload and proxies `/api`
to the backend via `VITE_API_PROXY_TARGET`.

<details>
<summary><b>Running without Docker</b></summary>

```bash
# Postgres must be running locally on 5432 with db/user/pass = meetmind

# backend
cd backend && mvn spring-boot:run

# frontend (new terminal)
cd frontend && npm install && npm run dev
```
</details>

---

## API

All routes except `/api/auth/**`, `/swagger-ui/**`, and `/api-docs/**` require
`Authorization: Bearer <token>`.

### Auth
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Create an account, returns a JWT |
| `POST` | `/api/auth/login` | Log in, returns a JWT |

### Meetings
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/meetings` | Create a meeting |
| `GET` | `/api/meetings` | List your meetings |
| `GET` | `/api/meetings/{id}` | Meeting detail |
| `PUT` | `/api/meetings/{id}` | Update a meeting |
| `DELETE` | `/api/meetings/{id}` | Delete a meeting |

### Processing
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/meetings/{id}/transcript` | Attach a text transcript |
| `POST` | `/api/meetings/{id}/audio` | Upload audio (multipart, ≤ 25 MB) → Whisper |
| `POST` | `/api/meetings/{id}/process` | Run Gemini analysis |
| `GET` | `/api/meetings/{id}/summary` | Fetch summary + action items |

### Tasks & Dashboard
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/tasks` | All action items across your meetings |
| `PUT` | `/api/tasks/{id}` | Edit description / assignee / due date |
| `PATCH` | `/api/tasks/{id}/status` | Flip `PENDING` ↔ `COMPLETED` |
| `DELETE` | `/api/tasks/{id}` | Delete a task |
| `GET` | `/api/dashboard` | Counts, upcoming deadlines, recent meetings |

---

## Data Model

```
User ──1:N──▶ Meeting ──1:1──▶ Transcript   (TEXT | AUDIO)
                 │
                 └──1:1──▶ Summary ──1:N──▶ ActionItem  (PENDING | COMPLETED)
```

`Meeting.status`: `PENDING · PROCESSING · PROCESSED · FAILED`

---



<div align="center">
<sub>Built with Spring Boot, React, and a healthy dislike of meetings that go nowhere.</sub>
</div>
