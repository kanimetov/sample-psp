package kg.demirbank.psp.exception.validation;

import kg.demirbank.psp.exception.PspException;
import org.springframework.http.HttpStatus;

/**
 * Exception for 456 Custom Error
 * Max amount not valid
 */
public class MaxAmountNotValidException extends PspException {
    
    public MaxAmountNotValidException(String message) {
        super(message, HttpStatus.valueOf(456), 456);
    }
    
    public MaxAmountNotValidException(String message, Throwable cause) {
        super(message, cause, HttpStatus.valueOf(456), 456);
    }
}

