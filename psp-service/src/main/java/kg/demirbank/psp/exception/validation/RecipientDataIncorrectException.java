package kg.demirbank.psp.exception.validation;

import kg.demirbank.psp.exception.PspException;
import org.springframework.http.HttpStatus;

/**
 * Exception for 452 Custom Error
 * The recipient's data is incorrect
 */
public class RecipientDataIncorrectException extends PspException {
    
    public RecipientDataIncorrectException(String message) {
        super(message, HttpStatus.valueOf(452), 452);
    }
    
    public RecipientDataIncorrectException(String message, Throwable cause) {
        super(message, cause, HttpStatus.valueOf(452), 452);
    }
}

