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
        final String finalCorrelationId;
        
        if (correlationId == null || correlationId.trim().isEmpty()) {
            finalCorrelationId = LoggingUtil.generateAndSetCorrelationId();
        } else {
            finalCorrelationId = correlationId;
            LoggingUtil.setCorrelationId(correlationId);
        }
        
        // Add correlation ID to response headers
        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);
        
        // Extract request information for logging
        String method = exchange.getRequest().getMethod().name();
        String uri = exchange.getRequest().getURI().getPath();
        String queryString = exchange.getRequest().getURI().getQuery();
        String fullUri = queryString != null ? uri + "?" + queryString : uri;
        
        long startTime = System.currentTimeMillis();
        
        // Log request start
        log.info("Request started: {} {} with correlation ID: {}", 
                method, fullUri, finalCorrelationId);
        
        // Continue with the filter chain
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    // Calculate response time
                    long responseTime = System.currentTimeMillis() - startTime;
                    
                    // Log request completion
                    var status = exchange.getResponse().getStatusCode();
                    int statusCode = status != null ? status.value() : 200;
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
    
}
