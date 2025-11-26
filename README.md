# Last-Mile Delivery Backend Engine

## Overview
High-performance backend engine for a Last-Mile Delivery application, built with Java 21 and Spring Boot 3.x. Designed with scalability, reliability, and industry best practices in mind.

## Tech Stack
- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.2.3
- **Database:** PostgreSQL 16 (with Flyway Migration)
- **Caching:** Redis
- **Messaging:** RabbitMQ
- **Security:** Spring Security + JWT
- **Testing:** JUnit 5, Mockito, Testcontainers
- **DevOps:** Docker Compose, GitHub Actions

## Architecture
Layered Architecture:
- `Controller`: REST API endpoints
- `Service`: Business logic (Smart Assignment, etc.)
- `Repository`: Data access (JPA)
- `Entity`: Database models
- `DTO`: Data Transfer Objects (mapped via MapStruct)

## Key Features
- **Smart Courier Assignment:** Uses Haversine formula to assign the nearest available courier to an order.
- **Real-time Notifications:** Asynchronous order status updates via RabbitMQ.
- **Performance:** Redis caching for frequently accessed data (Restaurants).
- **Security:** JWT-based authentication and authorization.
- **Observability:** Spring Boot Actuator & Prometheus metrics.

## How to Run

### Prerequisites
- Java 21
- Maven
- Docker & Docker Compose

### Steps
1. **Clone the repository**
   ```bash
   git clone https://github.com/murat/delivery-engine.git
   cd delivery-engine
   ```

2. **Start Infrastructure (Postgres, Redis, RabbitMQ)**
   ```bash
   docker-compose up -d
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access Swagger UI**
   Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) to explore the API.

## API Endpoints
- `POST /api/auth/login`: Login and get JWT.
- `POST /api/auth/register`: Register new user.
- `POST /api/orders`: Create new order.

## Testing
Run unit and integration tests:
```bash
mvn test
```
