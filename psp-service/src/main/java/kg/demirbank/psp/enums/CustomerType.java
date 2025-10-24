package kg.demirbank.psp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Customer type enum for PSP system operations.
 * Represents the type of customer performing the transaction.
 */
public enum CustomerType {
    
    /**
     * Individual customer - natural person
     */
    INDIVIDUAL("1"),
    
    /**
     * Corporate customer - legal entity
     */
    CORPORATE("2");

    private final String code;
    
    private static final Map<String, CustomerType> CODE_MAP = 
            Arrays.stream(values())
                    .collect(Collectors.toMap(CustomerType::getCode, Function.identity()));

    CustomerType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * Get CustomerType by code.
     * 
     * @param code the string code ("1" or "2")
     * @return the corresponding CustomerType
     * @throws IllegalArgumentException if code is not found
     */
    @JsonCreator
    public static CustomerType fromCode(String code) {
        CustomerType customerType = CODE_MAP.get(code);
        if (customerType == null) {
            throw new IllegalArgumentException("Unknown customer type code: " + code);
        }
        return customerType;
    }
}
