# Meteoros Task Management API

Spring Boot REST API for task management with JWT authentication, rate limiting, and MySQL database.

**GitHub Repository:** https://github.com/chandrasekhar-cherukuru/meteoros-task-management-api

## How to Run the Service Locally

### Prerequisites
- Java 21+
- MySQL 8.0+
- Maven 3.8+

### Installing Dependencies and Starting Server on Port 9876



Server will be available at: `http://localhost:9876`

## Database Setup and Environment Variables

### Database Setup Commands
Run these SQL commands in MySQL:


CREATE DATABASE meteoros_task_db;
CREATE USER 'meteoros_user'@'localhost' IDENTIFIED BY 'meteoros_password';
GRANT ALL PRIVILEGES ON meteoros_task_db.* TO 'meteoros_user'@'localhost';
FLUSH PRIVILEGES;
