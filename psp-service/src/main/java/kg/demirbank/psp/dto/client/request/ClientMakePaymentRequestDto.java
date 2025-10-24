package kg.demirbank.psp.dto.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kg.demirbank.psp.enums.CustomerType;
import lombok.Data;

/**
 * Request DTO for client make payment operation
 * Used when client wants to make a payment after checking QR details
 */
@Data
public class ClientMakePaymentRequestDto {
    
    /**
     * Full QR URI from URL (same as used in check)
     * Contains the complete QR code data that needs to be decoded
     */
    @NotBlank(message = "QR URI is required")
    @JsonProperty("qrUri")
    private String qrUri;
    
    /**
     * Payment amount in tyiyns
     * Must be positive value
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @JsonProperty("amount")
    private Long amount;
    
    /**
     * Customer type: Individual or Corporate
     * Required for all operations
     */
    @NotNull(message = "Customer type is required")
    @JsonProperty("customerType")
    private CustomerType customerType;
    
    /**
     * Required payment session ID linking to previous CHECK operation
     * Links this payment to the previous check operation
     */
    @NotBlank(message = "Payment session ID is required")
    @JsonProperty("paymentSessionId")
    private String paymentSessionId;
}
