package kg.demirbank.psp.exception.security;

import kg.demirbank.psp.exception.PspException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when signature verification fails
 */
public class SignatureVerificationException extends PspException {
    
    public SignatureVerificationException(String message) {
        super(message, HttpStatus.FORBIDDEN, 403);
    }
    
    public SignatureVerificationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN, 403);
    }
}
