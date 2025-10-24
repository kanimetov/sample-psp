package kg.demirbank.psp.exception.validation;

import kg.demirbank.psp.exception.PspException;
import org.springframework.http.HttpStatus;

/**
 * Exception for 455 Custom Error
 * Min amount not valid
 */
public class MinAmountNotValidException extends PspException {
    
    public MinAmountNotValidException(String message) {
        super(message, HttpStatus.valueOf(455), 455);
    }
    
    public MinAmountNotValidException(String message, Throwable cause) {
        super(message, cause, HttpStatus.valueOf(455), 455);
    }
}

