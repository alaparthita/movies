package com.aetna.movies.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class ClientRefIdHolder {
    private static final ThreadLocal<String> clientRefIdHolder = new ThreadLocal<>();
    private static final String CLIENT_REF_ID_MDC_KEY = "clientRefId";

    public static void setClientRefId(String clientRefId) {
        clientRefIdHolder.set(clientRefId);
        if (clientRefId != null) {
            MDC.put(CLIENT_REF_ID_MDC_KEY, clientRefId);
        } else {
            MDC.remove(CLIENT_REF_ID_MDC_KEY);
        }
    }

    public static String getClientRefId() {
        return clientRefIdHolder.get();
    }

    public static void clear() {
        clientRefIdHolder.remove();
        MDC.remove(CLIENT_REF_ID_MDC_KEY);
    }
} 