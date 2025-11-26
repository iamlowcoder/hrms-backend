# HRMS - Human Resource Management System

A Spring Boot 3.3 application with JWT authentication, PostgreSQL database, and role-based access control.

## Tech Stack

- **Java 21**
- **Spring Boot 3.3.4**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database access
- **PostgreSQL** - Database
- **JWT (jjwt 0.12.6)** - Token-based authentication
- **Lombok** - Boilerplate reduction
- **BCrypt** - Password encryption

## Package Structure

```
com.yourcompany.hrms
├── config          - Configuration classes (Security, JWT, DataInitializer)
├── security        - Security components (Filters, UserDetailsService, Authorization)
├── auth            - Authentication (Controller, DTOs)
├── user            - User management (Controller, Service, DTOs)
├── exception       - Exception handling (GlobalExceptionHandler, Custom exceptions)
├── jwt             - JWT utilities (Service, Config)
├── dto             - Data Transfer Objects (ResponseWrapper)
├── entity          - JPA entities (User, Role, RoleName)
├── repository      - Data access layer
├── service         - Business logic
└── controller      - REST controllers
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.6+
- PostgreSQL 12+

### Configuration

1. Update `application.yml` with your PostgreSQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database
    username: ${POSTGRES_USER:postgres}
    password: ${POSTGRES_PASSWORD:password}

jwt:
  secret: ${JWT_SECRET:change-me-to-a-strong-secret-key-at-least-64-bytes-512-bits-long-for-hs512-algorithm-please}
  expiration-seconds: ${JWT_EXPIRATION_SECONDS:86400}
```

2. Set environment variables (optional):
   - `POSTGRES_USER` - PostgreSQL username
   - `POSTGRES_PASSWORD` - PostgreSQL password
   - `JWT_SECRET` - JWT secret key (at least 64 bytes for HS512)
   - `JWT_EXPIRATION_SECONDS` - Token expiration time (default: 86400 = 24 hours)

### Running the Application

```bash
mvn spring-boot:run
```

On first startup, a default admin user is automatically created:
- **Email**: `admin@company.com`
- **Password**: `Admin@123`
- **Role**: `ADMIN`

## API Endpoints

### Authentication

#### POST `/api/auth/login`
Login and get JWT token.

**Request Body:**
```json
{
  "email": "admin@company.com",
  "password": "Admin@123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

**How to Get Your First JWT Token:**

1. Start the application
2. The default admin user is automatically created on first startup
3. Make a POST request to `/api/auth/login` with:
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "email": "admin@company.com",
       "password": "Admin@123"
     }'
   ```
4. Copy the `token` from the response
5. Use it in subsequent requests as: `Authorization: Bearer <token>`

### User Management

All user endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

#### POST `/api/users`
Create a new user. **Requires: ADMIN or HR role**

**Request Body:**
```json
{
  "email": "user@company.com",
  "password": "password123",
  "fullName": "John Doe",
  "phone": "+1234567890",
  "employeeCode": "EMP-1001",
  "roleName": "EMPLOYEE"
}
```

**Note:** 
- Only ADMIN can create ADMIN or HR users
- HR can only create EMPLOYEE users
- If `employeeCode` is not provided, it will be auto-generated (EMP-1001, EMP-1002, etc.)

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 1,
    "email": "user@company.com",
    "fullName": "John Doe",
    ...
  }
}
```

#### GET `/api/users`
List all users with pagination and search. **Requires: ADMIN or HR role**

**Query Parameters:**
- `search` (optional) - Search by name or email
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size
- `sortBy` (default: id) - Sort field
- `sortDir` (default: ASC) - Sort direction (ASC/DESC)

**Example:**
```
GET /api/users?search=john&page=0&size=10&sortBy=fullName&sortDir=ASC
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "content": [...],
    "totalElements": 10,
    "totalPages": 1,
    ...
  }
}
```

#### GET `/api/users/{id}`
Get a single user by ID. **Requires: ADMIN/HR role OR same user**

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@company.com",
    "fullName": "John Doe",
    "phone": "+1234567890",
    "employeeCode": "EMP-1001",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "createdById": 1,
    "createdByFullName": "System Administrator",
    "roleName": "EMPLOYEE"
  }
}
```

#### PUT `/api/users/{id}`
Update a user. **Requires: ADMIN/HR role OR same user (limited fields)**

**Request Body (all fields optional):**
```json
{
  "email": "newemail@company.com",
  "fullName": "Jane Doe",
  "phone": "+9876543210",
  "employeeCode": "EMP-1002",
  "roleName": "HR",
  "isActive": true
}
```

**Note:**
- Same user (non-admin/hr) can only update: `fullName`, `phone`
- ADMIN/HR can update all fields

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {...}
}
```

#### DELETE `/api/users/{id}`
Soft delete a user (sets `isActive = false`). **Requires: ADMIN role only**

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "User soft deleted successfully"
}
```

## Roles and Permissions

### ADMIN
- Can create ADMIN, HR, and EMPLOYEE users
- Can view, update, and delete all users
- Full access to all endpoints

### HR
- Can only create EMPLOYEE users
- Can view and update all users
- Cannot delete users

### EMPLOYEE
- Can view and update own profile (limited fields: fullName, phone)
- Cannot create, delete, or view other users

## Security

- **JWT Authentication**: All endpoints except `/api/auth/**` require JWT token
- **Stateless Sessions**: No server-side session storage
- **CSRF Disabled**: Not needed for stateless JWT authentication
- **Password Encryption**: BCrypt with strength 10
- **Token Expiration**: 24 hours (configurable via `jwt.expiration-seconds`)

## Error Responses

All errors follow a consistent format:

```json
{
  "success": false,
  "message": "Error message here"
}
```

**HTTP Status Codes:**
- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Validation error or invalid input
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

**Validation Errors:**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  }
}
```

## Development

### Building the Project

```bash
mvn clean install
```

### Running Tests

```bash
mvn test
```

## License

This project is proprietary software.

