package kg.demirbank.psp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a network connection fails
 */
public class NetworkConnectionException extends PspException {

    public NetworkConnectionException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    public NetworkConnectionException(String message, Throwable cause) {
        super(message, cause, HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE.value());
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}

