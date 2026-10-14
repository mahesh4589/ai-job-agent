# Auth Service

Java 21 + Spring Boot 4.1.1 + Spring Cloud 2025.1.2 authentication microservice.

## Features
- User registration with BCrypt password hashing
- Registration OTP stored in Redis for 5 minutes
- OTP verification attempt limit and resend cooldown
- Email verification before login
- JWT access token
- Database-backed refresh token with revoke/expiry, one-time rotation, and SHA-256 digest storage
- Forgot-password OTP and password reset
- Kafka `user.registered` event
- Eureka registration
- Config Server import
- Actuator health/info/prometheus

## Required local infrastructure
- PostgreSQL: `ai_job_agent`
- Redis: `localhost:6379`
- Kafka: `localhost:9092`
- Eureka: `localhost:8761`
- Config Server: `localhost:8888`

The Config Server reads `auth-service.yaml` from the `ai-job-agent-config` repository submodule. Its database password and JWT signing key are resolved from the Auth Service process environment. For local setup instructions and PowerShell examples, see `../ai-job-agent-config/README.md`. Do not commit secret values; the Config Server cannot read variables that exist only in the Config Server process, so set them in the Auth Service process or its IDE run configuration.

## APIs
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/verify-otp`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `POST /api/v1/auth/forgot-password`
- `POST /api/v1/auth/reset-password`
- `GET /api/v1/test/secure` (JWT protected)

Do not log OTPs or JWT secrets in production.

Refresh tokens are opaque random values. Only their SHA-256 digests are stored in the `refresh_tokens.token` column. Refreshing a token revokes it and returns a replacement; clients must persist the replacement and discard the old value. When deploying this change to a database that contains refresh tokens from the earlier implementation, invalidate those existing sessions (for example, delete rows from `refresh_tokens`) before enabling the new code. The old raw values cannot be converted to digests because the server does not possess the clients' copies.
