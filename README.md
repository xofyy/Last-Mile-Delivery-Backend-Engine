# Last-Mile Delivery Backend Engine

## Overview
A high-performance, scalable backend engine for a Last-Mile Delivery application. Built with **Java 21** and **Spring Boot 3**, this project implements industry best practices including **Layered Architecture**, **Event-Driven Design**, and **Geo-spatial Logic**.

## 🚀 Key Features
- **Smart Courier Assignment:** Uses **Haversine Formula** to assign the nearest available courier to an order in real-time.
- **Event-Driven Architecture:** Asynchronous order status updates using **RabbitMQ**.
- **Performance:** **Redis** caching for high-frequency data (e.g., Restaurant details).
- **Security:** Stateless authentication with **Spring Security** and **JWT**.
- **Reliability:** Comprehensive error handling and transaction management.
- **Observability:** Metrics exposure via **Spring Boot Actuator** and **Prometheus**.

## 🛠 Technology Stack
- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.2.3
- **Database:** PostgreSQL 16 (Managed via Flyway)
- **Caching:** Redis 7
- **Messaging:** RabbitMQ 3
- **Containerization:** Docker & Docker Compose
- **Testing:** JUnit 5, Mockito, Testcontainers

## 🏗 Architecture
The project follows a clean, layered architecture:
- **Controller Layer:** REST API endpoints (documented with OpenAPI/Swagger).
- **Service Layer:** Core business logic (Assignment, Validation).
- **Repository Layer:** Data access using Spring Data JPA.
- **DTO Pattern:** Data transfer objects mapped via **MapStruct**.

## 🧪 Testing & Verification
We have executed a rigorous testing strategy:
1.  **Unit Tests:** Core logic verification.
2.  **Integration Tests:** End-to-end API flows.
3.  **Edge Case Tests:** Input validation, unauthorized access, and system resilience.
4.  **Concurrency Tests:** Verified race condition handling (5 simultaneous orders vs 1 courier).
5.  **Geo-spatial Tests:** Verified accuracy of distance calculations.

*See [TEST_RESULTS.md](TEST_RESULTS.md) for detailed test reports.*

## 🏃 How to Run

### Prerequisites
- Java 21
- Maven
- Docker

### Steps
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
