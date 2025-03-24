# Movies Service

A Spring Boot microservice that provides comprehensive movie information and integrates with a ratings service. This service offers RESTful APIs for retrieving movie details, including titles, release dates, genres, and ratings.

## Features

- RESTful APIs for movie information management
- Integration with Ratings service for movie ratings
- SQLite database for data persistence
- OpenAPI/Swagger documentation
- Actuator endpoints for health monitoring and metrics
- OpenTelemetry integration for distributed tracing
- JaCoCo code coverage reporting
- Checkstyle for code quality
- Docker support

## Prerequisites

- Java 17 or higher
- Gradle 8.x
- Ratings service running on port 8081
- Docker (optional, for containerization)

## Quick Start

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd movies
   ```

2. Add execution permission to Gradle wrapper:
   ```bash
   chmod +x gradlew
   ```

3. Build and run tests:
   ```bash
   ./gradlew clean build
   ```

4. Generate test coverage report:
   ```bash
   ./gradlew test jacocoTestReport
   ```

5. Start the application:
   ```bash
   ./gradlew bootRun
   ```

The service will start on port 8080.

## API Documentation

The API documentation is available through Swagger UI when the application is running:
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs.yaml

### Key Endpoints

- `GET /api/v1/movies/`: Get all movies (paginated)
- `GET /api/v1/movies/{id}`: Get movie by ID
- `GET /api/v1/movies/year/{year}`: Get movies by release year
- `GET /api/v1/movies/genre/{genre}`: Get movies by genre

## Monitoring and Metrics

The application exposes various actuator endpoints for monitoring:

- Health Check: http://localhost:8080/actuator/health
- Metrics (Prometheus): http://localhost:8080/actuator/prometheus

## Database

The application uses SQLite as its database. The database file is included in the repository at `src/main/resources/movies.db`.

Key features:
- Indexes on genres and releaseDate columns for optimized queries
- JPA/Hibernate for data access
- Automatic schema updates

## Configuration

The application can be configured through `application.yaml`. Key configurations include:

- Database connection settings
- Ratings service endpoint
- Actuator endpoint exposure
- Logging configuration

Environment-specific configurations are available in separate YAML files.

## Distributed Tracing

OpenTelemetry integration is available for distributed tracing. Enable it using the OpenTelemetry Java agent and environment variables.

## Code Quality

- JaCoCo for code coverage reporting
- Unit tests for core functionality

## Docker Support

Build the Docker image:
```bash
docker build -t movies-service .
```

Run using Docker Compose:
```bash
docker-compose up
```

## Future Improvements

1. Enhanced genre search through:
   - Distributed caching
   - JSON field indexing
   - Full-text search capabilities

2. API Gateway integration for:
   - Authentication/Authorization
   - Rate limiting
   - Request routing