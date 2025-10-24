package kg.demirbank.psp.dto.bank.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.demirbank.psp.enums.TransactionType;
import lombok.Data;

/**
 * Response DTO for bank account check operation
 * Used when checking beneficiary account in our bank
 */
@Data
public class BankCheckResponseDto {
    
    /**
     * Beneficiary name (masked for privacy)
     */
    @JsonProperty("beneficiaryName")
    private String beneficiaryName;
    
    /**
     * Transaction type determined by bank
     */
    @JsonProperty("transactionType")
    private TransactionType transactionType;
    
    /**
     * Whether the account exists and is valid for transactions
     */
    @JsonProperty("accountValid")
    private Boolean accountValid;
}
