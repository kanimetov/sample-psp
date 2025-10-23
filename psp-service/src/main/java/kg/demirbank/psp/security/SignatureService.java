package kg.demirbank.psp.security;

import kg.demirbank.psp.exception.SignatureVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * Service for RSA signature generation and verification
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SignatureService {

    private final KeyManagementService keyManagementService;

    /**
     * Generate signature for outgoing request body or URI
     */
    public String generateSignature(byte[] bodyBytes, String uri) {
        try {
            byte[] dataToSign;
            
            if (bodyBytes != null && bodyBytes.length > 0) {
                // Use body for POST requests
                dataToSign = bodyBytes;
                log.debug("Generating signature for request body");
            } else if (uri != null && !uri.trim().isEmpty()) {
                // Use URI for GET/DELETE requests
                dataToSign = uri.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                log.debug("Generating signature for URI: {}", uri);
            } else {
                log.error("No body or URI provided for signature generation");
                throw new SignatureVerificationException("No data available for signature generation");
            }

            PrivateKey privateKey = keyManagementService.getPspPrivateKey();
            return signData(dataToSign, privateKey);
        } catch (Exception e) {
            log.error("Failed to generate signature", e);
            throw new SignatureVerificationException("Failed to generate signature", e);
        }
    }

    /**
     * Verify signature for incoming request body or URI
     */
    public boolean verifySignature(byte[] bodyBytes, String signature, String uri) {
        try {
            byte[] dataToVerify;
            
            if (bodyBytes != null && bodyBytes.length > 0) {
                // Use body for POST requests
                dataToVerify = bodyBytes;
                log.debug("Verifying signature for request body");
            } else if (uri != null && !uri.trim().isEmpty()) {
                // Use URI for GET/DELETE requests
                dataToVerify = uri.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                log.debug("Verifying signature for URI: {}", uri);
            } else {
                log.warn("No body or URI provided for signature verification");
                return false; // Reject requests without data to sign
            }

            if (signature == null || signature.trim().isEmpty()) {
                log.warn("Missing signature in request");
                return false;
            }

            PublicKey publicKey = keyManagementService.getOperatorPublicKey();
            return verifyData(dataToVerify, signature, publicKey);
        } catch (Exception e) {
            log.error("Failed to verify signature", e);
            return false;
        }
    }

    /**
     * Sign data with RSA private key using SHA-256
     */
    private String signData(byte[] data, PrivateKey privateKey) throws Exception {
        // Calculate SHA-256 hash of raw body bytes
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);

        // Sign the hash with RSA private key
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(hash);
        byte[] signatureBytes = signature.sign();

        // Encode signature as Base64
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    /**
     * Verify data signature with RSA public key using SHA-256
     */
    private boolean verifyData(byte[] data, String signature, PublicKey publicKey) throws Exception {
        // Calculate SHA-256 hash of raw body bytes
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);

        // Decode Base64 signature
        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        // Verify signature
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(hash);
        return sig.verify(signatureBytes);
    }

    /**
     * Verify signature for incoming request with detailed error information
     * Returns detailed result for controller processing
     */
    public SignatureVerificationResult verifySignatureWithDetails(byte[] bodyBytes, String signature, String uri) {
        try {
            // Skip verification if disabled
            if (!keyManagementService.isSignatureVerificationEnabled()) {
                log.debug("Signature verification disabled, skipping");
                return SignatureVerificationResult.success();
            }

            byte[] dataToVerify;
            
            if (bodyBytes != null && bodyBytes.length > 0) {
                // Use body for POST requests
                dataToVerify = bodyBytes;
                log.debug("Verifying signature for request body");
            } else if (uri != null && !uri.trim().isEmpty()) {
                // Use URI for GET/DELETE requests
                dataToVerify = uri.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                log.debug("Verifying signature for URI: {}", uri);
            } else {
                log.warn("No body or URI provided for signature verification");
                return SignatureVerificationResult.failure("No data available for signature verification");
            }

            if (signature == null || signature.trim().isEmpty()) {
                log.warn("Missing signature in request");
                return SignatureVerificationResult.failure("Missing signature header");
            }

            PublicKey publicKey = keyManagementService.getOperatorPublicKey();
            boolean isValid = verifyData(dataToVerify, signature, publicKey);
            
            if (isValid) {
                log.debug("Signature verification successful");
                return SignatureVerificationResult.success();
            } else {
                log.warn("Signature verification failed");
                return SignatureVerificationResult.failure("Signature verification failed");
            }
        } catch (Exception e) {
            log.error("Failed to verify signature", e);
            return SignatureVerificationResult.failure("Signature verification error: " + e.getMessage());
        }
    }

    /**
     * Result of signature verification
     */
    public static class SignatureVerificationResult {
        private final boolean success;
        private final String errorMessage;

        private SignatureVerificationResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public static SignatureVerificationResult success() {
            return new SignatureVerificationResult(true, null);
        }

        public static SignatureVerificationResult failure(String errorMessage) {
            return new SignatureVerificationResult(false, errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
