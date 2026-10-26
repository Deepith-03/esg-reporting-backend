# =============================================================================
# Build Stage: Compile and Package Spring Boot Application (Java 17)
# =============================================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Cache dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and package executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# =============================================================================
# Runtime Stage: Minimal JRE 17 Production Container
# =============================================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Ensure directory for evidence uploads exists inside container
RUN mkdir -p uploads/evidence

# Copy compiled JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Render assigns PORT dynamically; default to 8081 for local execution
ENV PORT=8081
EXPOSE 8081

# Launch application with dynamic port binding
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
