package kg.demirbank.psp.dto.merchant.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.demirbank.psp.enums.Status;
import lombok.Data;

/**
 * Response DTO for merchant make payment operation
 * Used when merchant makes a payment after checking QR details
 */
@Data
public class MerchantMakePaymentResponseDto {
    
    /**
     * Generated receipt ID for this payment
     */
    @JsonProperty("receiptId")
    private String receiptId;
    
    /**
     * Transaction status (typically CREATED=10 initially)
     */
    @JsonProperty("status")
    private Status status;
    
    /**
     * Operator/bank transaction ID
     */
    @JsonProperty("transactionId")
    private String transactionId;
    
    /**
     * Confirmed payment amount in tyiyns
     */
    @JsonProperty("amount")
    private Long amount;
    
    /**
     * ISO datetime when transaction was created
     */
    @JsonProperty("createdDate")
    private String createdDate;
}
