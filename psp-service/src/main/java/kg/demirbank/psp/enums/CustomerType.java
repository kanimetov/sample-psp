package kg.demirbank.psp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Customer type enum representing different transaction types in the payment system.
 */
public enum CustomerType {
    
    /**
     * C2C - Transfer by QR code/payment link.
     */
    C2C(10),
    
    /**
     * C2B - Purchase via QR code/payment link.
     */
    C2B(20),
    
    /**
     * C2G - State payment (of an individual) by QR code/payment link.
     */
    C2G(30),
    
    /**
     * B2C - Money transfer/withdrawal/refund by QR code/payment link.
     */
    B2C(40),
    
    /**
     * B2B - Payment/transfer by QR code/payment link.
     */
    B2B(50),
    
    /**
     * BANK_RESERVE (-) - Electronic message on setting the reserve to the bank.
     */
    BANK_RESERVE(60),
    
    /**
     * B2G - State payment (legal entity) by QR code/payment link.
     */
    B2G(70);

    private final int code;

    private static final Map<Integer, CustomerType> CODE_MAP = 
            Arrays.stream(values())
                    .collect(Collectors.toMap(CustomerType::getCode, Function.identity()));

    CustomerType(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    /**
     * Find CustomerType by code.
     * 
     * @param code the numeric code
     * @return the corresponding CustomerType
     * @throws IllegalArgumentException if code is not found
     */
    @JsonCreator
    public static CustomerType fromCode(int code) {
        CustomerType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Unknown customer type code: " + code);
        }
        return type;
    }
}

