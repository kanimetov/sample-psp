package kg.demirbank.psp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Status enum representing transaction status in the payment system.
 */
public enum Status {
    
    /**
     * Created - Transaction created (not final).
     */
    CREATED(10, false),
    
    /**
     * In process - Transaction is being processed (not final).
     */
    IN_PROCESS(20, false),
    
    /**
     * Error - Transaction failed with error (final).
     */
    ERROR(30, true),
    
    /**
     * Canceled - Transaction was canceled (final).
     */
    CANCELED(40, true),
    
    /**
     * Success - Transaction completed successfully (final).
     */
    SUCCESS(50, true);

    private final int code;
    private final boolean isFinal;

    private static final Map<Integer, Status> CODE_MAP = 
            Arrays.stream(values())
                    .collect(Collectors.toMap(Status::getCode, Function.identity()));

    Status(int code, boolean isFinal) {
        this.code = code;
        this.isFinal = isFinal;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Find Status by code.
     * 
     * @param code the numeric code
     * @return the corresponding Status
     * @throws IllegalArgumentException if code is not found
     */
    @JsonCreator
    public static Status fromCode(int code) {
        Status status = CODE_MAP.get(code);
        if (status == null) {
            throw new IllegalArgumentException("Unknown status code: " + code);
        }
        return status;
    }

    /**
     * Check if status is eligible for webhook notification.
     * Returns true for CREATED (PENDING), ERROR, CANCELED, and SUCCESS statuses.
     * 
     * @param status the status to check
     * @return true if status should trigger webhook notification
     */
    public static boolean isWebhookEligible(Status status) {
        if (status == null) {
            return false;
        }
        return status == CREATED || status.isFinal();
    }
}

