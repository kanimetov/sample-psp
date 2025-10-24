package kg.demirbank.psp.exception.validation;

import kg.demirbank.psp.exception.PspException;
import org.springframework.http.HttpStatus;

/**
 * Exception for 454 Custom Error
 * Incorrect data in the request
 */
public class IncorrectRequestDataException extends PspException {
    
    public IncorrectRequestDataException(String message) {
        super(message, HttpStatus.valueOf(454), 454);
    }
    
    public IncorrectRequestDataException(String message, Throwable cause) {
        super(message, cause, HttpStatus.valueOf(454), 454);
    }
}

