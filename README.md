# Shared Lists

A simple application to create and share lists with other users. This project is a **tech demo and proof of concept** for combining Apache Wicket with the modern Spring Boot ecosystem.

## What it does

- Create named lists and add entries to them
- Share lists with other users (invite-based, with roles)
- Authenticate via OAuth2 / OIDC

## Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0 |
| UI | Apache Wicket 10 |
| Persistence | Spring Data JPA + Hibernate 7 |
| Database | PostgreSQL |
| Migrations | Flyway |
| Auth | Spring Security + OAuth2 Client |
| Build | Gradle |

## Why Wicket?

Apache Wicket is a component-based web framework that keeps all UI logic in plain Java — no template language, no JavaScript framework required. This project explores whether Wicket remains a viable choice when paired with a contemporary Spring Boot + JPA stack, GraalVM native image support, and OAuth2 login.

## Running locally

1. Provide a PostgreSQL database and an OAuth2/OIDC provider.
2. Configure the connection and OAuth2 client credentials in `application.properties` or via environment variables.
3. Install Node dependencies and start the Tailwind CSS watcher:

```bash
npm install
npx @tailwindcss/cli -i ./input.css -o ./src/main/resources/static/css/output.css --watch
```

The watcher scans your source files for Tailwind class names and rebuilds `output.css` on every change. Keep it running in a separate terminal while developing.

4. Run:

```bash
./gradlew bootRun
```

## Notes

This is a proof of concept — not production-hardened. It intentionally keeps the feature set minimal to stay focused on the architecture.
It will be more polished in the Future.