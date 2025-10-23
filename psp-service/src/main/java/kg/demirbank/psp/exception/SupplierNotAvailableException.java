package kg.demirbank.psp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for 523 Custom Error
 * Supplier not available
 */
public class SupplierNotAvailableException extends PspException {
    
    public SupplierNotAvailableException(String message) {
        super(message, HttpStatus.valueOf(523), 523);
    }
    
    public SupplierNotAvailableException(String message, Throwable cause) {
        super(message, cause, HttpStatus.valueOf(523), 523);
    }
}

