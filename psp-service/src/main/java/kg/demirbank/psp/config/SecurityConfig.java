package kg.demirbank.psp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Security configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "security.signature")
@Data
public class SecurityConfig {
    
    /**
     * Path to operator's public key PEM file
     */
    private String operatorPublicKeyPath;
    
    /**
     * Path to PSP's private key PEM file
     */
    private String pspPrivateKeyPath;
    
    /**
     * Whether signature verification is enabled
     */
    private boolean enabled = true;
}
