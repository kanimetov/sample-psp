package kg.demirbank.psp.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for signature keys
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // Using default cache manager for now
    // Caffeine configuration will be handled by application.yml
}
