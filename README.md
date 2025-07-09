# Sunduq - Spring Boot OAuth2 Application

A comprehensive Spring Boot application featuring Google OAuth 2.0 authentication, JWT tokens, session management, and secure REST APIs.

## Features

- **Google OAuth 2.0 Integration**: Secure authentication using Google OAuth2
- **JWT Token Support**: JSON Web Token generation and validation
- **Session Management**: HTTP session-based user tracking
- **Authentication Filter**: Custom JWT authentication filter
- **Secure REST APIs**: Protected endpoints with authentication
- **User Management**: In-memory user storage with CRUD operations

## Project Structure

```
src/main/java/com/tyb/mytest/
├── config/
│   └── SecurityConfig.java          # Spring Security configuration
├── controller/
│   ├── AuthController.java          # Authentication endpoints
│   └── MyController.java            # Main REST API endpoints
├── filter/
│   └── JwtAuthenticationFilter.java # JWT authentication filter
├── model/
│   └── User.java                    # User entity model
├── service/
│   ├── CustomOAuth2UserService.java # OAuth2 user service
│   ├── JwtService.java              # JWT token operations
│   └── UserService.java             # User management service
└── MytestApplication.java           # Main application class
```

## Setup Instructions

### 1. Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Create OAuth 2.0 credentials:
   - Application type: Web application
   - Authorized redirect URIs: `http://localhost:8080/api/login/oauth2/code/google`
5. Copy the Client ID and Client Secret

### 2. Environment Configuration

Set the following environment variables or update `application.yml`:

```bash
export GOOGLE_CLIENT_ID=your-google-client-id
export GOOGLE_CLIENT_SECRET=your-google-client-secret
export JWT_SECRET=your-jwt-secret-key
```

### 3. Build and Run

```bash
# Build the application
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Public Endpoints (No Authentication Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/public/health` | Health check endpoint |
| GET | `/api/public/info` | API information |
| GET | `/api/auth/login` | Get login information |

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/oauth2/authorization/google` | Initiate Google OAuth2 login |
| GET | `/api/auth/success` | OAuth2 success callback |
| GET | `/api/auth/failure` | OAuth2 failure callback |
| POST | `/api/auth/logout` | Logout user |
| GET | `/api/auth/user` | Get current user info |
| GET | `/api/auth/session` | Get session information |

### Protected Endpoints (Authentication Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/greet` | Greeting message (personalized if authenticated) |
| GET | `/api/simple` | Simple response |
| GET | `/api/code` | Developer information |
| GET | `/api/profile` | User profile information |
| GET | `/api/dashboard` | User dashboard with stats |
| POST | `/api/api/data` | Create data (requires JSON body) |
| PUT | `/api/api/data/{id}` | Update data by ID |
| DELETE | `/api/api/data/{id}` | Delete data by ID |

## Authentication Flow

### 1. OAuth2 Login Flow

```mermaid
sequenceDiagram
    participant User
    participant App
    participant Google
    
    User->>App: GET /auth/login
    App->>User: Return Google login URL
    User->>App: GET /oauth2/authorization/google
    App->>Google: Redirect to Google OAuth2
    Google->>User: Google login page
    User->>Google: Enter credentials
    Google->>App: Redirect with authorization code
    App->>Google: Exchange code for access token
    Google->>App: Return user information
    App->>App: Create/update user
    App->>User: Redirect to /auth/success with JWT token
```

### 2. JWT Token Usage

After successful OAuth2 authentication, you'll receive a JWT token. Use it in subsequent requests:

```bash
curl -H "Authorization: Bearer your-jwt-token" http://localhost:8080/api/profile
```

### 3. Session Management

The application maintains HTTP sessions alongside JWT tokens:
- Sessions are created on successful OAuth2 login
- Session timeout: 30 minutes
- Session cookies: `SUNDUQ_SESSION`

## Example Usage

### 1. Check API Health

```bash
curl http://localhost:8080/api/public/health
```

Response:
```json
{
  "status": "UP",
  "timestamp": "2024-01-15T10:30:00",
  "service": "Sunduq API",
  "version": "1.0.0"
}
```

### 2. Initiate Google Login

```bash
curl http://localhost:8080/api/auth/login
```

Response:
```json
{
  "message": "Please authenticate with Google OAuth2",
  "googleLoginUrl": "/oauth2/authorization/google"
}
```

### 3. Access Protected Endpoint (with JWT)

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
     http://localhost:8080/api/profile
```

Response:
```json
{
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "picture": "https://...",
    "provider": "google",
    "enabled": true
  },
  "authenticated": true,
  "timestamp": "2024-01-15T10:35:00"
}
```

### 4. Create Data

```bash
curl -X POST \
     -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
     -H "Content-Type: application/json" \
     -d '{"title": "Sample Data", "content": "This is test data"}' \
     http://localhost:8080/api/api/data
```

## Security Features

- **CSRF Protection**: Disabled for API usage (enable for web forms)
- **CORS Configuration**: Configured for cross-origin requests
- **Session Security**: HTTP-only cookies, secure in production
- **JWT Security**: Token-based authentication with expiration
- **OAuth2 Security**: Secure Google authentication flow

## Configuration

### Key Configuration Properties

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid,profile,email

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000 # 24 hours

server:
  port: 8080
  servlet:
    context-path: /api
```

## Development

### Running in Development Mode

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Building for Production

```bash
./mvnw clean package
java -jar target/sunduq-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### Common Issues

1. **OAuth2 redirect URI mismatch**: Ensure the redirect URI in Google Console matches your application URL
2. **JWT token expired**: Tokens expire after 24 hours, re-authenticate to get a new token
3. **Session timeout**: Sessions expire after 30 minutes of inactivity

### Debug Logging

Enable debug logging in `application.yml`:

```yaml
logging:
  level:
    com.tyb.mytest: DEBUG
    org.springframework.security: DEBUG
```

## License

This project is licensed under the MIT License.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request