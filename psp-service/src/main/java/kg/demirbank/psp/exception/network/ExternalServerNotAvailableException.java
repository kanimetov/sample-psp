package kg.demirbank.psp.exception.network;

import kg.demirbank.psp.exception.PspException;
import org.springframework.http.HttpStatus;

/**
 * Exception for 524 Custom Error
 * External server is not available
 */
public class ExternalServerNotAvailableException extends PspException {
    
    public ExternalServerNotAvailableException(String message) {
        super(message, HttpStatus.valueOf(524), 524);
    }
    
    public ExternalServerNotAvailableException(String message, Throwable cause) {
        super(message, cause, HttpStatus.valueOf(524), 524);
    }
}

