# --- 1-BOSQICH: Qurilish (Build) bosqichi ---
FROM gradle:8.8.0-jdk21 AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-daemon

# --- 2-BOSQICH: Yakuniy (Final) bosqich ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]