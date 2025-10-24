package kg.demirbank.psp.enums;

/**
 * Enum for operation types in PSP system
 * Represents different stages of transaction lifecycle
 */
public enum OperationType {
    /**
     * Check operation - verification of QR code details
     * Used before creating actual transaction
     */
    CHECK(10),
    
    /**
     * Create operation - creating new transaction
     * Initial transaction creation in the system
     */
    CREATE(20),
    
    /**
     * Execute operation - executing created transaction
     * Processing the actual payment/transfer
     */
    EXECUTE(30),
    
    /**
     * Update operation - updating transaction status
     * Status updates from operator or internal system
     */
    UPDATE(40);
    
    private final int code;
    
    OperationType(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
    
    /**
     * Get OperationType by code
     */
    public static OperationType fromCode(int code) {
        for (OperationType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown operation type code: " + code);
    }
}
