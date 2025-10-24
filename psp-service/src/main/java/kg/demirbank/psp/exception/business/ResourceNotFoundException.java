package kg.demirbank.psp.exception.business;

import kg.demirbank.psp.exception.PspException;
import org.springframework.http.HttpStatus;

/**
 * Exception for 404 Not Found
 * The requested resource does not exist
 */
public class ResourceNotFoundException extends PspException {
    
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, 404);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND, 404);
    }
}

