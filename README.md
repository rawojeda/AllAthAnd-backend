# AllAtHand — Backend

REST API built with Java 21 + Spring Boot 3 + PostgreSQL.

---

## Architecture

```
resources/application.yml: Spring configuration (uses env vars or defaults)
.env: Local values(see .env.example to configure)
docker-compose.yml: Generates PostgreSQL + Backend
docker-compose.dev.yml: Generates PostgreSQL. Dev mode.
Dockerfile: Backend container image
src/main/java/com/allathand/
├── config: CORS and global configuration
├── controller: REST endpoints
├── dto: Request/response data transfer objects
├── entity: JPA entities (database tables)
├── repository: Database access layer
└── service: Business logic
```

---

## Prerequisites

- Java 21 JDK ([Temurin](https://adoptium.net))
- Maven 3.9+
- Docker + Docker Compose

---

## Setup

Copy the environment template and fill in your values:

```bash
cp .env.example .env
```

---

## Running locally (recommended for development)

This is the fastest workflow for active development. The backend runs on your machine with hot-reload capability, only the database runs in Docker.

**Terminal 1 — start the database:**
```bash
docker-compose -f docker-compose.dev.yml up
```

**Terminal 2 — start the backend:**
```bash
mvn spring-boot:run
```

Spring uses the defaults from `application.yml`:
- Database: `localhost:5432/allathand`
- Backend port: `8080`

No `.env` configuration needed for this workflow.

---

## Standard

Both PostgreSQL and the backend run inside Docker. The Dockerfile compiles the source code automatically — no local build step required.

```bash
docker-compose up --build
```

Docker Compose reads `.env` and injects all variables into the containers. Make sure `.env` is filled in before running.

**Stop and clean up:**
```bash
docker-compose down     # stops containers — database data is preserved
docker-compose down -v  # stops containers AND deletes all database data (full reset)
```

> Data persists across restarts because PostgreSQL writes to a Docker named volume (`postgres_data`), which exists independently of the container. The container is disposable; the volume is not. Use `-v` only when you want to start from a clean database.

---

## Environment variables

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8080` | Port the backend listens on |
| `DB_HOST` | `localhost` | Database host (`postgres` inside Docker) |
| `DB_PORT` | `5432` | Database port |
| `DB_NAME` | `allathand` | Database name |
| `DB_USER` | `allathand` | Database user |
| `DB_PASSWORD` | `allathand` | Database password |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated list of allowed frontend origins |

> `DB_HOST` is the only variable that changes automatically between environments: Docker Compose always sets it to `postgres` (the internal service name), while local development uses `localhost`.

---

## API endpoints

Base URL: `http://localhost:8080`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/entries` | List all entries (supports `?search=` and `?tags=`) |
| `GET` | `/api/entries/{id}` | Get entry by ID |
| `POST` | `/api/entries` | Create a new entry |
| `PUT` | `/api/entries/{id}` | Update an entry |
| `DELETE` | `/api/entries/{id}` | Delete an entry |
| `GET` | `/api/entries/tags` | List all distinct tags |

---

## Deploying to AWS RDS

Set the following environment variables on your server or ECS task definition:

```
DB_HOST=your-instance.xxxxxx.eu-west-1.rds.amazonaws.com
DB_PORT=5432
DB_NAME=allathand
DB_USER=your_user
DB_PASSWORD=your_secure_password
```
