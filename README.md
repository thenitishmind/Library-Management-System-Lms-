# Library Management System

A production-ready **RESTful Library Management System** built with **Spring Boot 2.7**, **Java 8**, **MySQL**, and **JWT-based security**. It covers the complete lifecycle of a library — books, members, loans, reservations, fines, notifications, and reporting — exposed through a clean REST API documented with Swagger/OpenAPI.

---

## Author

**Designed and developed by Nitish Singh**
Email: 

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Default Credentials](#default-credentials)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [License](#license)

---

## Features

- **JWT Authentication** — stateless login/register with access & refresh tokens
- **Role-Based Access Control** — `ROLE_ADMIN`, `ROLE_LIBRARIAN`, `ROLE_MEMBER`
- **Book Management** — full CRUD for books, authors, categories, publishers, and individual book copies
- **Member Management** — member registration, membership types (STANDARD, PREMIUM, STUDENT), status tracking
- **Loan Management** — borrow, return, renew books; configurable loan periods and max active loans per membership type
- **Reservation System** — reserve available books, auto-notification on availability
- **Fine Engine** — automatic fine calculation on overdue returns, configurable daily rates and grace periods per membership
- **Notification Service** — in-app notifications for due dates, overdue books, and reservation readiness
- **Reporting** — loan statistics, fine summaries, and popular books
- **Swagger UI** — interactive API documentation available at `/swagger-ui.html`
- **Hibernate ORM** — `ddl-auto: update` keeps the schema in sync automatically
- **HikariCP** — efficient connection pooling

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 8 |
| Framework | Spring Boot 2.7.18 |
| Security | Spring Security + JWT (jjwt 0.9.1) |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL 8 |
| Mapping | MapStruct 1.5.5 |
| Boilerplate | Lombok 1.18.30 |
| API Docs | SpringDoc OpenAPI (Swagger UI) 1.7.0 |
| Build | Apache Maven 3.9.6 |

---

## Architecture

```
com.library.lms
├── config/          # Security, Redis/cache, DataInitializer
├── controller/      # REST controllers (Auth, Book, Member, Loan, ...)
├── dto/
│   ├── request/     # Incoming payload DTOs
│   └── response/    # Outgoing response DTOs
├── entity/          # JPA entities + enums
├── exception/       # GlobalExceptionHandler, custom exceptions
├── mapper/          # MapStruct mappers
├── repository/      # Spring Data JPA repositories
├── security/        # JwtTokenProvider, JwtAuthenticationFilter, UserDetailsService
└── service/
    └── impl/        # Business logic implementations
```

---

## Prerequisites

| Tool | Minimum Version |
|---|---|
| Java JDK | 8 |
| Apache Maven | 3.6+ |
| MySQL Server | 8.0 |

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/library-management-system.git
cd library-management-system
```

### 2. Create the MySQL database

```sql
CREATE DATABASE library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure the application

Edit `src/main/resources/application.yml` and update the datasource credentials to match your local MySQL setup:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: your_mysql_password
```

### 4. Build and run

```bash
# Using the bundled Maven wrapper or local Maven
mvn clean package -DskipTests
java -jar target/lms-1.0.0.jar
```

Or run directly from source:

```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

### 5. Access Swagger UI

Open your browser and navigate to:

```
http://localhost:8080/swagger-ui.html
```

---

## Default Credentials

On first startup, `DataInitializer` seeds the database with the following accounts automatically.

### Admin Account

| Field | Value |
|---|---|
| Email | `NotTOshow` |
| Password | `NotTOshow` |
| Role | `ROLE_ADMIN` |
| Employee Code | `EMP-0001` |

### Sample Member Accounts

| Email | Password | Membership |
|---|---|---|
| `NotTOshow.com` | `NotTOshow@1` | PREMIUM |
| `NotTOshow.com` | `NotTOshow@1` | STUDENT |
| `NotTOshow.com` | `NotTOshow@1` | STANDARD |

> These accounts are only seeded when the database is empty (first run). If you restart the application with existing data, seeding is skipped.

---

## API Endpoints

All endpoints are prefixed with `/api`. Use the **admin credentials** to obtain a JWT token first.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/login` | Login and receive JWT tokens |
| POST | `/api/auth/register` | Register a new member |
| POST | `/api/auth/refresh` | Refresh access token |

### Books

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/books` | List all books (paginated) |
| GET | `/api/books/{id}` | Get book details |
| POST | `/api/books` | Add a new book (Admin/Librarian) |
| PUT | `/api/books/{id}` | Update a book (Admin/Librarian) |
| DELETE | `/api/books/{id}` | Delete a book (Admin) |

### Members

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/members` | List all members (Admin/Librarian) |
| GET | `/api/members/{id}` | Get member details |
| PUT | `/api/members/{id}` | Update member |
| DELETE | `/api/members/{id}` | Deactivate member (Admin) |

### Loans

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/loans` | List all loans (Admin/Librarian) |
| POST | `/api/loans` | Issue a book loan |
| PUT | `/api/loans/{id}/return` | Return a book |
| PUT | `/api/loans/{id}/renew` | Renew a loan |

### Reservations

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reservations` | List reservations |
| POST | `/api/reservations` | Create a reservation |
| DELETE | `/api/reservations/{id}` | Cancel a reservation |

### Fines

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/fines` | List all fines |
| GET | `/api/fines/{id}` | Get fine details |
| POST | `/api/fines/{id}/pay` | Pay a fine |

### Reports

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports/loans` | Loan statistics |
| GET | `/api/reports/fines` | Fine summary |
| GET | `/api/reports/popular-books` | Most borrowed books |

---

## Fine Policy by Membership Type

| Membership | Daily Rate | Max Fine | Grace Period | Max Loan Days | Max Renewals | Max Active Loans |
|---|---|---|---|---|---|---|
| STANDARD | ₹5.00 | ₹500.00 | 1 day | 14 days | 2 | 5 |
| PREMIUM | ₹2.00 | ₹200.00 | 3 days | 21 days | 3 | 10 |
| STUDENT | ₹1.00 | ₹100.00 | 2 days | 14 days | 2 | 5 |

---

## Configuration

Key properties in `application.yml`:

```yaml
jwt:
  secret: <your-256-bit-hex-secret>
  access-token-expiry: 900000       # 15 minutes (ms)
  refresh-token-expiry: 604800000   # 7 days (ms)

library:
  loan:
    reminder-days-before-due: 2
    max-renewals-default: 2
  fine:
    daily-rate-default: 5.00
    max-fine-default: 500.00

server:
  port: 8080
```

For production, override the JWT secret via environment variable:

```bash
export JWT_SECRET=your-super-secure-256-bit-hex-secret
```

---

## Project Structure

```
Library Management System/
├── src/
│   ├── main/
│   │   ├── java/com/library/lms/
│   │   │   ├── config/
│   │   │   │   ├── DataInitializer.java   # Seeds DB on first run
│   │   │   │   ├── RedisConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/               # 9 REST controllers
│   │   │   ├── dto/                      # Request & Response DTOs
│   │   │   ├── entity/                   # 16 JPA entities + 11 enums
│   │   │   ├── exception/                # Global error handling
│   │   │   ├── mapper/                   # MapStruct mappers
│   │   │   ├── repository/               # 14 JPA repositories
│   │   │   ├── security/                 # JWT filter & provider
│   │   │   └── service/impl/             # 8 service implementations
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
└── README.md
```

---

## License

```
Copyright 2024 Nitish Singh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
