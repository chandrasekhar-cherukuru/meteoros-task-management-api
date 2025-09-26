# Meteoros Task Management API

Spring Boot REST API for task management with JWT authentication, rate limiting, and MySQL database.

**GitHub Repository:** https://github.com/chandrasekhar-cherukuru/meteoros-task-management-api

## How to Run the Service Locally

### Prerequisites
- Java 21+
- MySQL 8.0+
- Maven 3.8+

### Installing Dependencies and Starting Server on Port 9876

```bash
# 1. Clone repository
git clone https://github.com/chandrasekhar-cherukuru/meteoros-task-management-api.git
cd meteoros-task-management-api

# 2. Install dependencies
mvn clean install

# 3. Setup database (see Database Setup section below)

# 4. Start server on port 9876
mvn spring-boot:run
```

Server will be available at: `http://localhost:9876`

## Database Setup and Environment Variables

### Database Setup Commands
Run these SQL commands in MySQL:

```sql
CREATE DATABASE meteoros_task_db;
CREATE USER 'meteoros_user'@'localhost' IDENTIFIED BY 'meteoros_password';
GRANT ALL PRIVILEGES ON meteoros_task_db.* TO 'meteoros_user'@'localhost';
FLUSH PRIVILEGES;
```

### Environment Variable Names and Sample .env Template

Create a `.env` file in project root with these variables:

```env
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/meteoros_task_db
DB_USERNAME=meteoros_user
DB_PASSWORD=meteoros_password

# JWT Configuration  
JWT_SECRET=mySecretKey1234567890123456789012345678901234567890
JWT_EXPIRATION=86400

# Server Configuration
SERVER_PORT=9876

# Rate Limiting Configuration
RATE_LIMIT_AUTHENTICATED=10
RATE_LIMIT_UNAUTHENTICATED=3
```

## Postman Collection Requests for Each Endpoint

### 1. Register User Endpoint
```http
POST http://localhost:9876/api/v1/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

### 2. Login User Endpoint
```http
POST http://localhost:9876/api/v1/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

**Note:** Copy the `token` from login response for use in the task endpoints below.

### 3. Create Task Endpoint
```http
POST http://localhost:9876/api/v1/tasks
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "title": "Complete documentation",
  "description": "Write comprehensive API documentation",
  "status": "todo"
}
```

**Expected Response:**
```json
{
  "id": 1,
  "title": "Complete documentation",
  "description": "Write comprehensive API documentation",
  "status": "todo",
  "createdAt": "2025-09-26T20:30:00",
  "updatedAt": "2025-09-26T20:30:00"
}
```

### 4. Update Task Endpoint
```http
PUT http://localhost:9876/api/v1/tasks/1
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "title": "Complete documentation - Updated",
  "description": "Write comprehensive API documentation with examples",
  "status": "in-progress"
}
```

**Expected Response:**
```json
{
  "id": 1,
  "title": "Complete documentation - Updated",
  "description": "Write comprehensive API documentation with examples",
  "status": "in-progress",
  "createdAt": "2025-09-26T20:30:00",
  "updatedAt": "2025-09-26T20:45:00"
}
```

### 5. Delete Task Endpoint
```http
DELETE http://localhost:9876/api/v1/tasks/1
Authorization: Bearer <JWT_TOKEN>
```

**Expected Response:**
```json
{
  "message": "Task deleted successfully!",
  "success": true
}
```

### 6. List Tasks Endpoint
```http
GET http://localhost:9876/api/v1/tasks
Authorization: Bearer <JWT_TOKEN>
```

**Expected Response:**
```json
[
  {
    "id": 2,
    "title": "Review code changes",
    "description": "Review pull request #123",
    "status": "todo",
    "createdAt": "2025-09-26T20:40:00",
    "updatedAt": "2025-09-26T20:40:00"
  },
  {
    "id": 3,
    "title": "Deploy to production",
    "description": "Deploy latest version to production server",
    "status": "done",
    "createdAt": "2025-09-26T20:45:00",
    "updatedAt": "2025-09-26T20:50:00"
  }
]
```

## API Features
- ✅ JWT Authentication with secure password hashing
- ✅ Rate Limiting (10 requests/min authenticated, 3 requests/min unauthenticated)
- ✅ Complete CRUD operations for task management
- ✅ Input validation with custom error handling
- ✅ User-specific task isolation for security
- ✅ MySQL database integration with JPA/Hibernate

## Technology Stack
- **Framework:** Spring Boot 3.5.6
- **Security:** Spring Security + JWT
- **Database:** MySQL 8.0 with JPA/Hibernate
- **Rate Limiting:** Bucket4j
- **Validation:** Bean Validation (JSR-303)
- **Build Tool:** Maven
- **Java Version:** 21

---
