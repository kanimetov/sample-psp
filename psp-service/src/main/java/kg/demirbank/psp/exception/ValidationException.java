package kg.demirbank.psp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when DTO validation fails
 */
public class ValidationException extends PspException {

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, 400);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST, 400);
    }
}
