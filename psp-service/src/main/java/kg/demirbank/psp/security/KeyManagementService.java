package kg.demirbank.psp.security;

import kg.demirbank.psp.config.SecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Service for managing cryptographic keys
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeyManagementService {

    private final SecurityConfig securityConfig;

    /**
     * Load operator's public key from PEM file
     */
    @Cacheable("operator-public-key")
    public PublicKey getOperatorPublicKey() {
        try {
            log.info("Loading operator public key from: {}", securityConfig.getOperatorPublicKeyPath());
            return loadPublicKeyFromPem(securityConfig.getOperatorPublicKeyPath());
        } catch (Exception e) {
            log.error("Failed to load operator public key", e);
            throw new RuntimeException("Failed to load operator public key", e);
        }
    }

    /**
     * Load PSP's private key from PEM file
     */
    @Cacheable("psp-private-key")
    public PrivateKey getPspPrivateKey() {
        try {
            log.info("Loading PSP private key from: {}", securityConfig.getPspPrivateKeyPath());
            return loadPrivateKeyFromPem(securityConfig.getPspPrivateKeyPath());
        } catch (Exception e) {
            log.error("Failed to load PSP private key", e);
            throw new RuntimeException("Failed to load PSP private key", e);
        }
    }

    /**
     * Load public key from PEM file
     */
    private PublicKey loadPublicKeyFromPem(String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] keyBytes = fis.readAllBytes();
            String keyString = new String(keyBytes);
            
            // Remove PEM headers and footers
            keyString = keyString.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        }
    }

    /**
     * Load private key from PEM file
     */
    private PrivateKey loadPrivateKeyFromPem(String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] keyBytes = fis.readAllBytes();
            String keyString = new String(keyBytes);
            
            // Remove PEM headers and footers
            keyString = keyString.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        }
    }

    /**
     * Check if signature verification is enabled
     */
    public boolean isSignatureVerificationEnabled() {
        return securityConfig.isEnabled();
    }
}
