package kg.demirbank.psp.exception.security;

import kg.demirbank.psp.exception.PspException;
import org.springframework.http.HttpStatus;

/**
 * Exception for 453 Custom Error
 * Access to the system is denied
 */
public class AccessDeniedException extends PspException {
    
    public AccessDeniedException(String message) {
        super(message, HttpStatus.valueOf(453), 453);
    }
    
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause, HttpStatus.valueOf(453), 453);
    }
}

