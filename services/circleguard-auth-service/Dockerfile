# Stage 1: Extract layers from the JAR
FROM eclipse-temurin:21-jre-alpine AS builder
WORKDIR /app
COPY build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 2: Final runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install curl for health checks (Smoke Tests)
RUN apk add --no-cache curl

# Copy extracted layers from the builder stage
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Use the JarLauncher to start the application with optimized layers
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
