# Auth Service

Java 21 + Spring Boot 4.1.1 + Spring Cloud 2025.1.2 authentication microservice.

## Features
- User registration with BCrypt password hashing
- Registration OTP stored in Redis for 5 minutes
- Email verification before login
- JWT access token
- Database-backed refresh token with revoke/expiry
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

Copy `config-example/auth-service.yaml` to the Config Server Git repository as `auth-service.yaml`.

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
