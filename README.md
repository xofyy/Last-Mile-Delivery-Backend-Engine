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
- **AI Demand Prediction:** Python microservice using **Scikit-Learn** to predict order demand based on time and day.
- **Production Ready:** Multi-stage Docker build and secure production configuration.

## 🛠 Technology Stack
- **Language:** Java 21 (LTS), Python 3.9
- **Framework:** Spring Boot 3.2.3, FastAPI
- **Database:** PostgreSQL 16 + PostGIS, SQLite (AI Training)
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
- **AI Microservice:** Independent Python service for training and prediction, communicating via RabbitMQ and REST.

## 🧪 Testing & Verification
We have executed a rigorous testing strategy:
1.  **Unit Tests:** Core logic verification using JUnit 5.
2.  **Integration Tests:** Real database tests using **Testcontainers**.
3.  **Performance Tests:** Simulated 1000 couriers and 100 concurrent orders.
4.  **Geo-spatial Tests:** Verified boundary conditions (Inside/Outside radius).
5.  **AI Integration:** Verified end-to-end data flow and model learning (`scripts/ai_tests.ps1`, `scripts/ai_training_test.ps1`).
6.  **Scripts:** Automated PowerShell scripts (`scripts/`) for end-to-end verification.

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
    - AI Service: [http://localhost:8001/docs](http://localhost:8001/docs)

### Production Deployment
1.  **Deploy with Docker Compose:**
    ```bash
    docker-compose -f docker-compose.prod.yml up -d --build
    ```
    *This will build the optimized Docker image and start App, DB, Redis, RabbitMQ, and AI Service in production mode.*
