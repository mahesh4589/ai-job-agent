# Auth Service: interview study guide

## Request flow

1. Registration validates the request, normalizes the email, hashes the password with BCrypt, and creates an unverified user.
2. The OTP service sends a short-lived verification code. Verification marks the account as verified.
3. Login checks the password and verification state, then issues a short-lived JWT access token and a random opaque refresh token.
4. Requests carrying an access token pass through the JWT filter. The filter validates the signature and expiry, loads the user, and places an authenticated principal in Spring Security's context.
5. Refresh validates the stored digest while holding a database row lock, revokes the old token, and issues a replacement. Logout revokes the presented refresh token.

## Concepts to explain in an interview

### Authentication and authorization

Authentication answers “who is making this request?” Authorization answers “what is that authenticated user allowed to do?” Login establishes identity; Spring Security authorities and endpoint rules enforce permissions. A valid JWT alone should not grant access to another user's data: resource queries must also be scoped to the authenticated user's ID.

### Access token and refresh token

The access token is a signed JWT with a short lifetime. Services can validate its signature and claims without a database lookup on every request. The refresh token is a random opaque credential backed by server-side state, allowing the server to revoke it and rotate it. A client must replace its saved refresh token after every successful refresh.

### Why hash a refresh token?

Refresh tokens are high-entropy random values, so a fast SHA-256 digest is suitable for lookup. If the token table leaks, an attacker gets digests rather than credentials that can be used directly. Passwords are different: people choose guessable values, so they need a deliberately slow password hash such as BCrypt.

### Why rotate and lock?

Rotation makes a refresh token one-time use. If a stolen token is replayed after the legitimate client has refreshed, the old token is already revoked. The database row lock serializes concurrent refresh attempts so two requests cannot both successfully rotate the same token.

### Why use Kafka?

Kafka decouples Auth Service from downstream consumers such as profile creation or onboarding notifications. It lets consumers process events asynchronously and retry independently. Publishing directly after a database write still has a failure window; a transactional outbox is the usual next step to make database changes and event publication reliable together.

## Current implementation limits to discuss honestly

- OTP verification is limited to five failed attempts and sending has a 60-second cooldown. Production should also add IP/device-level rate limits and monitor email delivery; the forgot-password API intentionally returns the same success response for unknown addresses.
- Registration writes to PostgreSQL, sends email, and publishes Kafka as separate operations. A transactional outbox and an idempotent email workflow are still needed.
- Existing refresh-token rows from the previous plaintext format must be invalidated during rollout; old sessions cannot be migrated to digests.
- Production database schema changes should use Flyway or Liquibase migrations and `ddl-auto: validate`, rather than Hibernate `update`.
- Add focused tests for token rotation/replay, OTP expiry/attempt limits, and authorization ownership before calling the service production-ready.

## Example interview summary

“The Auth Service uses Spring Security for request authentication, BCrypt for user passwords, short-lived signed JWTs for API access, and random opaque refresh tokens for revocable sessions. Refresh tokens are stored as SHA-256 digests and rotated under a database lock to limit replay. OTP and Kafka support registration workflows. For production reliability, I would add IP/device-level rate limits, use a transactional outbox for database-to-Kafka events, and manage the schema with versioned migrations.”
