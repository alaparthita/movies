# Step 1: Build Stage
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copy Gradle files and dependencies to leverage caching
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

# Give execution permissions for Gradle Wrapper
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew bootJar


# Step 2: Runtime Stage
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /app/build/libs/*.jar /app/movies.jar

# Install OpenTelemetry Java Agent
ENV OTEL_VERSION=1.28.0
RUN curl -L -o otel-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_VERSION}/opentelemetry-javaagent.jar
#COPY otel-javaagent.jar /app/otel-javaagent.jar

# Set up OpenTelemetry environment variables
ENV OTEL_SERVICE_NAME=movies-svc
ENV OTEL_TRACES_EXPORTER=otlp
# Leveraging prometheus for metrics
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
ENV OTEL_RESOURCE_ATTRIBUTES=service.name=movies-svc,environment=prod

# Expose application & OpenTelemetry ports
EXPOSE 8080

# Start the application with OpenTelemetry agent
CMD ["java", "-javaagent:/app/otel-javaagent.jar", "-jar", "/app/movies.jar"]
