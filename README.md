# E-commerce Order Management System API

This project is a comprehensive, production-ready RESTful API for managing an e-commerce platform's products and customer orders. It's built with a modern Java stack, fully containerized with Docker, and documented with Swagger/OpenAPI.

## 🚀 Core Features

-   **Product Management:** Full CRUD (Create, Read, Update, Delete) operations for products with pagination and search capabilities.
-   **Order Processing:** A robust system to create and manage customer orders with complex business logic.
-   **Business Logic:** Includes critical checks like product stock availability, prevention of duplicate products in an order, and automatic stock updates.
-   **Status Management:** Functionality to update and cancel orders, with stock restoration on cancellation.
-   **Error Handling:** A centralized exception handling mechanism for clear and consistent error responses.
-   **Validation:** Robust validation on all incoming data using Jakarta Bean Validation.
-   **Testing:** High test coverage (>80%) with both unit and integration tests to ensure reliability.

## 🛠️ Technology Stack

-   **Backend**: Java 21, Spring Boot 3.5.4, Spring Web, Spring Data JPA, Hibernate
-   **Database**: PostgreSQL (for production/Docker), H2 (for testing)
-   **Containerization**: Docker, Docker Compose (with multi-stage builds)
-   **API Documentation**: Swagger / OpenAPI 3 (springdoc-openapi)
-   **Testing**: JUnit 5, Mockito, MockMvc, JaCoCo for test coverage
-   **Build Tool**: Gradle
-   **Utilities**: Lombok, SLF4J & Logback

## ⚙️ How to Run the Project

The entire application is containerized, so all you need is **Docker** and **Docker Compose** installed on your machine.

### 1. Clone the Repository

```bash
git clone https://github.com/muhammadjonsaidov/order-management.git
cd order-management
```

### 2. Run with Docker Compose

Navigate to the root directory of the project in your terminal and run the following single command:

```bash
docker-compose up --build
```
This command will:
1.  Build the Spring Boot application from the source using a multi-stage `Dockerfile`.
2.  Start a PostgreSQL database container.
3.  Start the application API container.
4.  Connect both containers to a shared network.

To run in the background (detached mode), use:
```bash
docker-compose up --build -d
```

### 3. Access the Application

-   **API Server**: `http://localhost:8080`
-   **Swagger UI (API Docs)**: `http://localhost:8080/swagger-ui.html`
-   **Database (via external tool)**: You can connect to the PostgreSQL database on `localhost:5432` with username `user` and password `password`.

## 📝 API Documentation

All endpoints are fully documented using Swagger. Please visit the interactive Swagger UI to view all endpoints, read their descriptions, and test them live.

➡️ [**Swagger UI: http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)

## 📊 Test Coverage

The project is configured with JaCoCo to enforce a minimum of 80% line coverage. You can generate the report by running:
```bash
./gradlew test jacocoTestReport
```
The report will be available at `build/reports/jacoco/index.html`.

## ⏹️ How to Stop the Application

To stop and remove all running containers and networks, run:
```bash
docker-compose down
```