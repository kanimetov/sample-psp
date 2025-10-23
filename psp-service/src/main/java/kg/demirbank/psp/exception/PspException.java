package kg.demirbank.psp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for PSP Service
 */
@Getter
public class PspException extends RuntimeException {
    
    private final HttpStatus status;
    private final Integer code;
    
    public PspException(String message, HttpStatus status, Integer code) {
        super(message);
        this.status = status;
        this.code = code;
    }
    
    public PspException(String message, Throwable cause, HttpStatus status, Integer code) {
        super(message, cause);
        this.status = status;
        this.code = code;
    }
}

