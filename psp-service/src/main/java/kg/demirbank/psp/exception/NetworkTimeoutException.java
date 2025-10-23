package kg.demirbank.psp.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a network request times out
 */
public class NetworkTimeoutException extends PspException {

    public NetworkTimeoutException(String message) {
        super(message, HttpStatus.GATEWAY_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT.value());
    }

    public NetworkTimeoutException(String message, Throwable cause) {
        super(message, cause, HttpStatus.GATEWAY_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT.value());
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.GATEWAY_TIMEOUT;
    }
}

