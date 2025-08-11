# FinanceHub

Enterprise-grade, multi-tenant financial platform built with Spring Boot. This is the clean baseline implementation used later for the benchmark suite.

## Quick start (dev)

Prereqs: Java 17+, Maven 3.9+, Docker, Docker Compose

```bash
# Build and test
mvn -f app/financehub/pom.xml -B -ntp verify

# Run app
mvn -f app/financehub/pom.xml -B -ntp spring-boot:run

# Or via Docker Compose (DB, Redis, Keycloak, app)
docker compose -f app/financehub/docker-compose.yml up -d --build
```

App will listen on http://localhost:8080

## Endpoints
- GET `/health` basic JSON health
- Actuator `/actuator` (health/info only in dev by default)

## Modules
- Spring Boot (web, actuator, validation, flyway placeholder)
- To be added in later phases: Security (OIDC/SAML), JPA, Flyway migrations, reporting, audit, etc.


