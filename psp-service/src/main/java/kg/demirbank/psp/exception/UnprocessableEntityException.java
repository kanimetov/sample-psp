package kg.demirbank.psp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for 422 Unprocessable Entity
 * The request is well-formed but contains invalid data that cannot be processed
 */
public class UnprocessableEntityException extends PspException {
    
    public UnprocessableEntityException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, 422);
    }
    
    public UnprocessableEntityException(String message, Throwable cause) {
        super(message, cause, HttpStatus.UNPROCESSABLE_ENTITY, 422);
    }
}

