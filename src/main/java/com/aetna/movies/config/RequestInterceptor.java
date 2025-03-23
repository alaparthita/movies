package com.aetna.movies.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    private static final String CLIENT_REF_ID_HEADER = "clientRefId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientRefId = request.getHeader(CLIENT_REF_ID_HEADER);
        log.debug("Received request with clientRefId header: {}", clientRefId);
        if (clientRefId != null && !clientRefId.trim().isEmpty()) {
            ClientRefIdHolder.setClientRefId(clientRefId.trim());
            log.debug("Set clientRefId in holder: {}", clientRefId.trim());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String clientRefId = ClientRefIdHolder.getClientRefId();
        log.debug("Clearing clientRefId from holder: {}", clientRefId);
        ClientRefIdHolder.clear();
    }
} 