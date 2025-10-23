package kg.demirbank.psp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for 500 Internal Server Error
 * System error
 */
public class SystemErrorException extends PspException {
    
    public SystemErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, 500);
    }
    
    public SystemErrorException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR, 500);
    }
}

