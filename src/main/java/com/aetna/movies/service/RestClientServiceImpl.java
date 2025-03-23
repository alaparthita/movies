package com.aetna.movies.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

import com.aetna.movies.config.ClientRefIdHolder;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapPropagator;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RestClientServiceImpl implements RestClientService {

    private static final String CLIENT_REF_ID_HEADER = "clientRefId";
    private static final AttributeKey<String> CLIENT_REF_ID_ATTR = AttributeKey.stringKey("client.ref.id");
    private final HttpClient client;
    private final Tracer tracer;
    private final TextMapPropagator propagator;

    public RestClientServiceImpl() {
        this.client = HttpClient.newHttpClient();
        this.tracer = GlobalOpenTelemetry.getTracer("com.aetna.movies");
        this.propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
    }

    @Override
    public HttpResponse<String> get(String uri) throws IOException, InterruptedException {
        String clientRefId = ClientRefIdHolder.getClientRefId();
        log.debug("Making GET request with clientRefId: {}", clientRefId);

        Span span = tracer.spanBuilder("HTTP GET")
                .setSpanKind(SpanKind.CLIENT)
                .setAttribute(CLIENT_REF_ID_ATTR, clientRefId)
                .startSpan();

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri))
                    .header("Accept", "application/json");

            if (clientRefId != null) {
                requestBuilder.header(CLIENT_REF_ID_HEADER, clientRefId);
            }

            // Inject the current context into the request headers
            Context context = Context.current().with(span);
            propagator.inject(context, requestBuilder, (builder, key, value) -> builder.header(key, value));

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            span.setStatus(StatusCode.OK, "Request completed successfully");
            return response;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    public HttpResponse<String> post(String uri, String body) throws IOException, InterruptedException {
        String clientRefId = ClientRefIdHolder.getClientRefId();
        log.debug("Making POST request with clientRefId: {}", clientRefId);

        Span span = tracer.spanBuilder("HTTP POST")
                .setSpanKind(SpanKind.CLIENT)
                .setAttribute(CLIENT_REF_ID_ATTR, clientRefId)
                .startSpan();

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .uri(URI.create(uri))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json");

            if (clientRefId != null) {
                requestBuilder.header(CLIENT_REF_ID_HEADER, clientRefId);
            }

            // Inject the current context into the request headers
            Context context = Context.current().with(span);
            propagator.inject(context, requestBuilder, (builder, key, value) -> builder.header(key, value));

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            span.setStatus(StatusCode.OK, "Request completed successfully");
            return response;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
