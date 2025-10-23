package kg.demirbank.psp.exception;

import org.springframework.http.HttpStatus;

/**
 * Generic network exception for other network-related errors
 */
public class NetworkException extends PspException {

    public NetworkException(String message) {
        super(message, HttpStatus.BAD_GATEWAY, HttpStatus.BAD_GATEWAY.value());
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_GATEWAY, HttpStatus.BAD_GATEWAY.value());
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_GATEWAY;
    }
}

