package kg.demirbank.psp.config;

import kg.demirbank.psp.util.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import org.springframework.lang.NonNull;

/**
 * WebFilter for automatic correlation ID management and request/response logging.
 * This filter ensures every request has a correlation ID for tracing.
 */
@Component
@Order(1)
@Slf4j
public class LoggingFilter implements WebFilter {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    
    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        // Extract or generate correlation ID
        final String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.trim().isEmpty()) {
            LoggingUtil.generateAndSetCorrelationId();
        } else {
            LoggingUtil.setCorrelationId(correlationId);
        }
        
        // Add correlation ID to response headers
        final String finalCorrelationId = correlationId != null ? correlationId : LoggingUtil.generateAndSetCorrelationId();
        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);
        
        // Extract request information for logging
        String method = exchange.getRequest().getMethod().name();
        String uri = exchange.getRequest().getURI().getPath();
        String queryString = exchange.getRequest().getURI().getQuery();
        String fullUri = queryString != null ? uri + "?" + queryString : uri;
        String ipAddress = getClientIpAddress(exchange);
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        String apiVersion = extractApiVersion(uri);
        
        // Set request context in MDC
        LoggingUtil.setRequestContext(ipAddress, userAgent, null, null);
        if (apiVersion != null) {
            LoggingUtil.setOperationContext("HTTP_REQUEST", null, null, null, null, apiVersion);
        }
        
        long startTime = System.currentTimeMillis();
        
        // Log request start
        log.info("Request started: {} {} from IP: {} with correlation ID: {}", 
                method, fullUri, ipAddress, finalCorrelationId);
        
        // Continue with the filter chain
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    // Calculate response time
                    long responseTime = System.currentTimeMillis() - startTime;
                    
                    // Set response time in MDC
                    LoggingUtil.setResponseTime(responseTime);
                    
                    // Log request completion
                    int statusCode = exchange.getResponse().getStatusCode() != null ? 
                            exchange.getResponse().getStatusCode().value() : 200;
                    String logLevel = (statusCode >= 400) ? "ERROR" : "INFO";
                    
                    if ("ERROR".equals(logLevel)) {
                        log.error("Request completed: {} {} - Status: {} - Response time: {}ms - Correlation ID: {}", 
                                 method, fullUri, statusCode, responseTime, finalCorrelationId);
                    } else {
                        log.info("Request completed: {} {} - Status: {} - Response time: {}ms - Correlation ID: {}", 
                                method, fullUri, statusCode, responseTime, finalCorrelationId);
                    }
                    
                    // Clear MDC context
                    LoggingUtil.clearContext();
                });
    }
    
    /**
     * Extract client IP address from request
     */
    private String getClientIpAddress(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return exchange.getRequest().getRemoteAddress() != null && 
                exchange.getRequest().getRemoteAddress().getAddress() != null ? 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }
    
    /**
     * Extract API version from URI path
     */
    private String extractApiVersion(String uri) {
        if (uri == null) return null;
        
        // Pattern: /in/qr/{version}/tx/...
        String[] parts = uri.split("/");
        if (parts.length >= 4 && "qr".equals(parts[2])) {
            return parts[3];
        }
        
        return null;
    }
}
