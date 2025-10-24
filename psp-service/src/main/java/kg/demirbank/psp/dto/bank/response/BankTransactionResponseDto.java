package kg.demirbank.psp.dto.bank.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.demirbank.psp.enums.Status;
import lombok.Data;

/**
 * Response DTO for bank transaction creation
 * Used when creating internal bank transaction
 */
@Data
public class BankTransactionResponseDto {
    
    /**
     * Bank's transaction ID
     */
    @JsonProperty("transactionId")
    private String transactionId;
    
    /**
     * Transaction status
     */
    @JsonProperty("status")
    private Status status;
    
    /**
     * Transaction creation timestamp
     */
    @JsonProperty("createdDate")
    private String createdDate;
}
