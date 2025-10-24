package kg.demirbank.psp.dto.bank.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for bank account check operation
 * Used when checking beneficiary account in our bank
 */
@Data
public class BankCheckRequestDto {
    
    /**
     * Service provider name (merchant ID)
     */
    @Size(max = 32)
    @JsonProperty("merchantId")
    private String merchantId;
    
    /**
     * Unique identifier of the payer within the service (account number)
     */
    @Size(max = 32)
    @JsonProperty("beneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    
    /**
     * Service provider code (MCC)
     */
    @NotNull
    @JsonProperty("merchantCode")
    private Integer merchantCode;
    
    /**
     * Payment amount in tyiyns
     */
    @NotNull
    @Positive
    @JsonProperty("amount")
    private Long amount;
}
