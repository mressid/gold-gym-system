# Master GYM Backend

Backend REST API for a gym management system: members (customers), staff/admin
accounts, subscription packs and billing, a photo gallery, training sessions,
and contact messages. Built with Spring Boot, secured with JWT, and backed by
MySQL and MinIO for object storage.

## Tech stack

- Java 21, Spring Boot 3.4
- Spring Security with JWT (OAuth2 Resource Server / JOSE)
- Spring Data JPA, MySQL 8
- MinIO for image storage (profile photos, gallery)
- MapStruct for entity/DTO mapping, Lombok
- springdoc-openapi (Swagger UI)
- Maven

## Prerequisites

- Docker and Docker Compose (recommended path), or
- JDK 21, Maven, a local MySQL 8 instance, and a local MinIO instance if
  running without Docker

## Running with Docker Compose

This is the simplest way to run the full stack (app, MySQL, MinIO):

```bash
docker compose up --build
```

This starts:

- `mysql` - the application database
- `minio` - object storage for uploaded images
- `minio-init` - a one-off job that creates the bucket, makes it publicly
  readable, and seeds a default avatar
- `app` - the Spring Boot API, published on port `8089`

Once it is up, the API is available at `http://localhost:8089` and Swagger UI
at `http://localhost:8089/swagger-ui.html`.

## Configuration

The application is configured entirely through environment variables (see
`src/main/resources/application.properties` for defaults). Create a `.env`
file in the project root for `docker compose` to pick up:

| Variable | Purpose | Default |
|---|---|---|
| `DB_NAME` | MySQL database name | `MasterGYM` |
| `DB_USER` | MySQL user | `ali` |
| `DB_PASSWORD` | MySQL password | `root` |
| `DB_ROOT_PASSWORD` | MySQL root password (compose only) | `root` |
| `JWT_SECRET` | HMAC secret used to sign JWTs | a development default in `application.properties` |
| `MINIO_ROOT_USER` / `MINIO_ACCESS_KEY` | MinIO access key | `minioadmin` |
| `MINIO_ROOT_PASSWORD` / `MINIO_SECRET_KEY` | MinIO secret key | `minioadmin123` |
| `MINIO_BUCKET` | Bucket used for uploads | `master-gym` |
| `MINIO_PUBLIC_URL` | Host used to build public URLs for stored files | `http://localhost:9020` |
| `ADMIN_DISPLAY_NAME` | Display name of the seeded admin account | `Admin` |
| `ADMIN_EMAIL` | Email of the seeded admin account | `admin@example.com` |
| `ADMIN_TELEPHONE` | Phone number of the seeded admin account | `22865991` |
| `ADMIN_PASSWORD` | Password of the seeded admin account | `ali1234` |

Set a real `JWT_SECRET` and admin credentials before deploying anywhere
beyond local development.

The admin account is created once on startup if no user exists with
`ADMIN_EMAIL`.

Uploaded files are only ever stored as object keys internally; the public
URL is built from `MINIO_PUBLIC_URL` at request time, so changing that
variable repoints every existing image without touching the database.

## Running locally without Docker

1. Start a MySQL 8 instance and a MinIO instance, and export the environment
   variables above to point at them.
2. Build and run:

   ```bash
   ./mvnw spring-boot:run
   ```

The API listens on port `8089` by default (`server.port` in
`application.properties`).

## Authentication

- `POST /auth/login` - exchanges a `userName`/`password` pair for a JWT
  (`access-token`), valid for 30 minutes.
- Send the token as `Authorization: Bearer <token>` on subsequent requests.
- Roles are exposed as JWT scopes and enforced with `@PreAuthorize` on
  protected endpoints.

## API overview

Base URL: `http://localhost:8089`

| Resource | Base path |
|---|---|
| Authentication | `/auth` |
| Users (staff/admin accounts) | `/user` |
| Customers (gym members) | `/customer` |
| Packs | `/packs` |
| Subscriptions | `/subscriptions` |
| Roles | `/role` |
| Albums | `/albums` |
| Photos | `/photos` |
| Training sessions | `/training-sessions` |
| Contact messages | `/contact-messages` |

Full request/response schemas are available through Swagger UI at
`/swagger-ui.html` (or the raw OpenAPI document at `/v3/api-docs`) once the
application is running.

## Project structure

```
src/main/java/com/BackEnd/Master/GYM/
  controller/   REST endpoints
  services/     business logic (interfaces + Impl/)
  repository/   Spring Data JPA repositories
  entity/       JPA entities
  dto/          request/response DTOs
  Mapper/       MapStruct entity <-> DTO mappers
  security/     JWT auth, MinIO client config, web security rules
  Exceptions/   custom exceptions and handling
  util/         small stateless helpers
```
