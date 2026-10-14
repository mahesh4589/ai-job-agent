# Auth Service API

The interactive OpenAPI/Swagger UI is available while Auth Service is running:

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

If Auth Service uses a different port in Config Server, replace `8081` with that port. Authentication routes below are public; `/api/v1/test/secure` requires an access JWT.

## Authentication flow

1. Register with `POST /api/v1/auth/register`.
2. Verify the emailed registration OTP with `POST /api/v1/auth/verify-otp`.
3. Login with `POST /api/v1/auth/login` and save the returned access and refresh tokens securely.
4. Send the refresh token to `POST /api/v1/auth/refresh` when needed. Refresh rotates the token: discard the submitted token and keep the newly returned token.
5. Logout with `POST /api/v1/auth/logout` using the latest refresh token. A revoked token cannot be refreshed.

All request bodies use `Content-Type: application/json`.

## Example requests

### Register

`POST /api/v1/auth/register`

```json
{
  "firstName": "Asha",
  "lastName": "Patil",
  "mobileNo": 9876543210,
  "emailId": "asha@example.com",
  "password": "Use-A-Strong-Password"
}
```

### Verify registration OTP

`POST /api/v1/auth/verify-otp`

```json
{
  "email": "asha@example.com",
  "otp": "123456"
}
```

### Login

`POST /api/v1/auth/login`

```json
{
  "emailId": "asha@example.com",
  "password": "Use-A-Strong-Password"
}
```

### Refresh tokens

`POST /api/v1/auth/refresh`

```json
{
  "refreshToken": "<refresh-token-from-login>"
}
```

### Logout

`POST /api/v1/auth/logout`

```json
{
  "refreshToken": "<latest-refresh-token>"
}
```

### Protected endpoint

`GET /api/v1/test/secure`

In Swagger UI, select **Authorize** and enter the access token only (without the `Bearer ` prefix). Requests made by Swagger include it as a bearer token.

## Other password endpoints

- `POST /api/v1/auth/forgot-password` body: `{"email":"asha@example.com"}`
- `POST /api/v1/auth/reset-password` body: `{"email":"asha@example.com","otp":"123456","newPassword":"A-New-Strong-Password"}`

Successful authentication endpoints wrap data in `success`, `message`, `data`, and `statusCode` fields. Do not commit, log, or share real passwords, OTPs, access tokens, or refresh tokens.
