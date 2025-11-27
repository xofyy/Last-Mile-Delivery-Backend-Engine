# Last-Mile Delivery Backend Engine

## Overview
A high-performance, scalable backend engine for a Last-Mile Delivery application. Built with **Java 21** and **Spring Boot 3**, this project implements industry best practices including **Layered Architecture**, **Event-Driven Design**, and **Geo-spatial Logic** using **PostGIS**.

## 🚀 Key Features
- **Smart Courier Assignment:** Uses **PostGIS** (`ST_DWithin`, `ST_Distance`) to assign the nearest available courier to an order in real-time with high precision.
- **Event-Driven Architecture:** Asynchronous order status updates using **RabbitMQ**.
- **Performance:** **Redis** caching for high-frequency data (e.g., Restaurant details).
- **Security:** Stateless authentication with **Spring Security** and **JWT**.
- **Reliability:** Comprehensive error handling (`GlobalExceptionHandler`, `BusinessException`) and transaction management.
- **Observability:** Metrics exposure via **Spring Boot Actuator** and **Prometheus**.
- **Production Ready:** Multi-stage Docker build and secure production configuration.

## 🛠 Technology Stack
- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.2.3
- **Database:** PostgreSQL 16 + PostGIS (Managed via Flyway)
- **Caching:** Redis 7
- **Messaging:** RabbitMQ 3
- **Containerization:** Docker & Docker Compose
- **Testing:** JUnit 5, Mockito, Testcontainers, PowerShell Scripts

## 🏗 Architecture
The project follows a clean, layered architecture:
- **Controller Layer:** REST API endpoints (documented with OpenAPI/Swagger).
- **Service Layer:** Core business logic (Assignment, Validation).
- **Repository Layer:** Data access using Spring Data JPA & Native Spatial Queries.
- **DTO Pattern:** Data transfer objects mapped via **MapStruct**.

## 🧪 Testing & Verification
We have executed a rigorous testing strategy:
1.  **Unit Tests:** Core logic verification using JUnit 5.
2.  **Integration Tests:** Real database tests using **Testcontainers**.
3.  **Performance Tests:** Simulated 1000 couriers and 100 concurrent orders.
4.  **Geo-spatial Tests:** Verified boundary conditions (Inside/Outside radius).
5.  **Scripts:** Automated PowerShell scripts (`scripts/`) for end-to-end verification.

*See [TEST_RESULTS.md](TEST_RESULTS.md) for detailed test reports.*

## 🏃 How to Run

### Development Mode
1.  **Start Infrastructure:**
    ```bash
    docker-compose up -d
    ```
2.  **Run Application:**
    ```bash
    mvn spring-boot:run
    ```
3.  **Access API:**
    - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Production Deployment
1.  **Deploy with Docker Compose:**
    ```bash
    docker-compose -f docker-compose.prod.yml up -d --build
    ```
    *This will build the optimized Docker image and start App, DB, Redis, and RabbitMQ in production mode.*
