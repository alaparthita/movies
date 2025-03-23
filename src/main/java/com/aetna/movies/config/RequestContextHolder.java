package com.aetna.movies.config;

import org.springframework.stereotype.Component;

@Component
public class RequestContextHolder {
    private static final ThreadLocal<String> clientRefIdHolder = new ThreadLocal<>();

    public static void setClientRefId(String clientRefId) {
        clientRefIdHolder.set(clientRefId);
    }

    public static String getClientRefId() {
        return clientRefIdHolder.get();
    }

    public static void clear() {
        clientRefIdHolder.remove();
    }
} 