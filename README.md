# 🛠️ Gestion des Incidents — Backend

Spring Boot REST API for the Gestion des Incidents application.  
**Frontend repo:** https://github.com/kaoutar629/GestionIncidents-frontend

---

## 🚀 Getting Started

### Prerequisites
- **Java** 21+
- **Maven** 3.9+
- **MySQL** 8+

### Installation

```bash
git clone https://github.com/kaoutar629/GestionIncidents-backend.git
cd GestionIncidents-backend
```

### Environment Variables

Export these variables (or add them to your IDE run config):

```bash
export MYSQL_ADDON_HOST=localhost
export MYSQL_ADDON_PORT=3306
export MYSQL_ADDON_DB=gestion_incidents
export MYSQL_ADDON_USER=root
export MYSQL_ADDON_PASSWORD=yourpassword
export MAIL_USER=your_mailtrap_user
export MAIL_PASS=your_mailtrap_pass
```

> ⚙️ The schema is auto-created by Hibernate (`ddl-auto=update`). No manual migration needed.

### Run

```bash
./mvnw spring-boot:run
# API runs at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Build for Production

```bash
./mvnw package -DskipTests
# Produces target/gestionIncidents-*.jar
```

---

## 🧰 Tech Stack

| Technology | Purpose |
|-----------|---------|
| **Spring Boot 3** | REST API framework |
| **Spring Security + JWT** | Authentication & stateless sessions |
| **Spring Data JPA / Hibernate** | ORM & database access |
| **MySQL 8** | Relational database |
| **Lombok** | Boilerplate reduction |
| **SpringDoc / Swagger UI** | API documentation |
| **JavaMailSender + Mailtrap** | Email notifications |
| **Spring Events (async)** | Decoupled notification system |
| **MapStruct** | DTO ↔ Entity mapping |
| **Bean Validation** | Input validation |

---

## ✨ Features

- 🔐 JWT authentication + BCrypt password hashing
- 📋 Full incident CRUD with role-based visibility
- 🤖 Rule-based NLP classifier (no external API)
- 📊 Analytics endpoints
- 👥 User management (Admin only)
- 📧 Async email notifications on incident events
---

## 📁 Project Structure
src/main/java/com/kaoutar/gestionIncidents/
├── config/         # OpenAPI, AsyncConfig
├── controller/     # REST controllers
├── service/        # Business logic
├── security/       # JWT filter, WebSecurityConfig
├── entity/         # JPA entities
├── dto/            # Request/Response DTOs
├── repository/     # Spring Data JPA repositories
├── events/         # Spring application events
├── mappers/        # MapStruct mappers
└── exception/      # Global exception handler

---

## 🚀 Deployment

Deployed on **Render**: https://your-backend.onrender.com
---

## 📁 Project Structure
