# Local Run Guide

## Required Services

For the full application you need Java 21, Maven, PostgreSQL with PostGIS, Kafka, Redis, and Elasticsearch. PostgreSQL is enough for registration and profile storage. Recommendations need Kafka and Elasticsearch, swipes need Redis, and notifications need Kafka plus Redis.

For a course demo, you can still show the main flow with PostgreSQL plus the notification service: users register, create profiles, enter another user's tag manually, and chat through the minimal web client. The demo chat stores messages in memory, so history is cleared after restarting `MyChat-Notification`.

## Database

Create a PostgreSQL database named `mychat` with user `postgres` and password `Rts28022007`, or override values through environment variables:

```powershell
$env:DATABASE_NAME="mychat"
$env:DATABASE_USER="postgres"
$env:DATABASE_PASSWORD="Rts28022007"
$env:DATABASE_HOST="localhost"
$env:DATABASE_PORT="5432"
```

`MyChat-UserMicroservice` runs Liquibase on startup and creates the required tables/extensions when the database user has permission to create extensions. If your database already contains a failed Liquibase run, clear the `databasechangelog` rows for `v1_init.sql` or recreate the local database.

## Start

From the repository root:

```powershell
.\start-local.ps1
```

Default ports:

- Auth: `8081`
- User: `8082`
- Recommendation: `8083`
- Swipes: `8084`
- Notification: `8085`
- Loaded test: `9999` if started manually from `MyChat-LoadedTest`

Open `web-client/index.html` in a browser to use the minimal UI.

## Demo Flow

1. Register two users in two browser windows or profiles.
2. Create profiles for both users.
3. Enter the second user's tag in the manual like/chat fields.
4. Send messages through the Chat section.
