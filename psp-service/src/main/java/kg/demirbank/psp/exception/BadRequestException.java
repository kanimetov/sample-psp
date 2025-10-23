package kg.demirbank.psp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for 400 Bad Request
 * The request is invalid or malformed. The server cannot process it
 */
public class BadRequestException extends PspException {
    
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, 400);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST, 400);
    }
}

