package kg.demirbank.psp.exception.validation;

import kg.demirbank.psp.exception.PspException;
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
