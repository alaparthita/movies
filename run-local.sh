#!/bin/bash

# Download OpenTelemetry Java Agent if not exists
OTEL_VERSION=1.28.0
OTEL_AGENT_PATH="./otel-javaagent.jar"
if [ ! -f "$OTEL_AGENT_PATH" ]; then
    echo "Downloading OpenTelemetry Java Agent..."
    curl -L -o $OTEL_AGENT_PATH https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_VERSION}/opentelemetry-javaagent.jar
fi

# Set OpenTelemetry environment variables
export OTEL_SERVICE_NAME=movies-svc
export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=prometheus
export OTEL_LOGS_EXPORTER=none
export OTEL_EXPORTER_OTLP_PROTOCOL=grpc
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
export OTEL_TRACES_SAMPLER=parentbased_always_on
export OTEL_PROPAGATORS=b3
export OTEL_RESOURCE_ATTRIBUTES=service.name=movies-svc,environment=local
export OTEL_LOGS_EXPORT=true
export OTEL_TRACES_EXPORT=true
export OTEL_METRICS_EXPORT=true
export OTEL_EXPORTER_OTLP_HEADERS=
export OTEL_EXPORTER_OTLP_TIMEOUT=10000
export OTEL_EXPORTER_OTLP_COMPRESSION=none
export OTEL_EXPORTER_OTLP_CERTIFICATE=
export OTEL_EXPORTER_OTLP_CLIENT_KEY=
export OTEL_EXPORTER_OTLP_CLIENT_CERTIFICATE=
export OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=/v1/traces
export OTEL_EXPORTER_OTLP_METRICS_ENDPOINT=/v1/metrics
export OTEL_EXPORTER_OTLP_LOGS_ENDPOINT=/v1/logs
export OTEL_TRACES_SAMPLER_ARG=1.0
export OTEL_TRACES_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_TRACES_HEADERS=
export OTEL_EXPORTER_OTLP_TRACES_TIMEOUT=10000
export OTEL_EXPORTER_OTLP_TRACES_COMPRESSION=none
export OTEL_EXPORTER_OTLP_TRACES_CERTIFICATE=
export OTEL_EXPORTER_OTLP_TRACES_CLIENT_KEY=
export OTEL_EXPORTER_OTLP_TRACES_CLIENT_CERTIFICATE=

# Set Java options
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# Run the application with OpenTelemetry agent
./gradlew bootRun --args="-javaagent:${OTEL_AGENT_PATH}" 