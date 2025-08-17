# Case Study: Production-Ready E-commerce Order Management API

This project is more than just a test task; it's a practical case study covering the full development lifecycle of a modern backend system. Built from the ground up, this RESTful API for an e-commerce platform encompasses everything from architectural design and security to performance optimization, automated testing, and final containerization.

## 1. Project Goal and Approach

**The Task:** To create a REST API for an e-commerce order management system, allowing for the management of products and customer orders.

**My Approach:** I approached this task not just as a coding exercise, but from the perspective of building a **scalable, robust, and maintainable** system. I chose to implement industry-standard best practices and tools at every stage to ensure the final product is professional and production-ready.

## 2. Architecture and Key Technical Solutions

The foundation of the project is a classic but powerful **Three-Layer Architecture** (`Controller`, `Service`, `Repository`). This architectural choice minimizes coupling between different parts of the application, which makes it significantly easier to modify, test, and scale each layer independently in the future.

### a) Security: Spring Security & JWT
API security was a top priority. I implemented a **role-based access control (RBAC) model using Spring Security**, clearly defining `ADMIN` and `USER` roles. This prevents unauthorized access to critical API endpoints, ensuring the confidentiality and integrity of system data. For authentication, I chose stateless **JSON Web Tokens (JWT)**, which makes the system inherently scalable and ready for potential microservice architectures.

### b) Database Migrations: Flyway
Managing database schemas with `ddl-auto: update` is risky in production. I solved this by **automating database migrations with Flyway**. By versioning all schema changes in SQL scripts, I created a reliable and trackable history of the database structure. This minimizes human error during deployments and simplifies collaboration within a development team.

### c) Performance: Redis Caching
To enhance API responsiveness, I integrated a **caching layer using Spring Cache and Redis**. By strategically applying `@Cacheable` and `@CacheEvict` annotations, I significantly reduced the load on the PostgreSQL database for frequently read data, like product details. This dramatically decreases the average API response time, which directly improves the user experience and the overall system throughput.

### d) Containerization: Docker & Docker Compose
To solve the classic "it works on my machine" problem and prepare the project for deployment, I **containerized the entire application stack using Docker and Docker Compose**. A multi-stage `Dockerfile` creates a lightweight, optimized production image. The accompanying `docker-compose.yml` file, complete with health checks, orchestrates the API, PostgreSQL, and Redis containers. This creates a consistent and isolated environment that can be spun up anywhere with a single command, which drastically simplifies the developer onboarding and deployment processes.

### e) Quality Assurance: Comprehensive Testing
To ensure the reliability and stability of the code, I wrote a comprehensive suite of **unit tests (with Mockito) and integration tests (with MockMvc and H2)**. By configuring JaCoCo, I achieved **over 80% test coverage**. This robust test suite prevents regression bugs when new features are added, ensuring the long-term stability and maintainability of the codebase.

## 3. Technology Stack

-   **Backend Framework:** Java 21, Spring Boot 3.5.4 (Web, Data JPA, Security)
-   **Database:** PostgreSQL (for Production/Development) & H2 (for Testing)
-   **Caching:** Redis
-   **Authentication:** JSON Web Tokens (JWT)
-   **Database Migration:** Flyway
-   **Containerization:** Docker & Docker Compose
-   **API Documentation:** Swagger / OpenAPI 3
-   **Testing & Quality:** JUnit 5, Mockito, MockMvc, JaCoCo
-   **Monitoring:** Spring Boot Actuator
-   **Build Tool:** Gradle

## 4. Getting Started: How to Run the Project

The entire application is containerized. The only prerequisites are **Docker** and **Docker Compose**.

#### a) Clone the Repository

```bash
git clone https://github.com/muhammadjonsaidov/order-management.git
cd order-management
```

#### b) Run with Docker Compose

From the project's root directory, execute the following command:

```bash
docker-compose up --build
```

This single command will build the Java application, start the PostgreSQL and Redis containers, and run the API, all connected within a shared Docker network. To run in the background, use the `-d` flag.

#### c) Accessing the Services

-   **API Server**: `http://localhost:8080`
-   **Swagger UI (API Docs)**: `http://localhost:8080/swagger-ui/index.html`
-   **Actuator Health Check**: `http://localhost:8080/actuator/health`

## 5. API Documentation & Usage

All endpoints are fully documented in the interactive Swagger UI. This is the best place to explore and test the API.

➡️ [**Swagger UI: http://localhost:8080/swagger-ui/index.html**](http://localhost:8080/swagger-ui/index.html)

**Authentication:**
1.  Use the `POST /api/auth/login` endpoint with default credentials (`admin`/`admin123` or `user`/`user123`) or register a new user via `POST /api/auth/register`.
2.  Copy the returned JWT token.
3.  In Swagger UI, click the "Authorize" button and paste the token in the format `Bearer <your_token>`.
4.  You can now access the protected endpoints.

## 6. Stopping the Application

To stop and remove all running containers, networks, and associated volumes, run:

```bash
docker-compose down -v
```
