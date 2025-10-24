package kg.demirbank.psp.dto.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kg.demirbank.psp.enums.CustomerType;
import lombok.Data;

/**
 * Request DTO for client check operation
 * Used when client wants to check QR payment details before making payment
 */
@Data
public class ClientCheckRequestDto {
    
    /**
     * Full QR URI from URL (e.g., from https://retail.demirbank.kg/#QR_DATA)
     * Contains the complete QR code data that needs to be decoded
     */
    @NotBlank(message = "QR URI is required")
    @JsonProperty("qrUri")
    private String qrUri;
    
    /**
     * Customer type: Individual or Corporate
     * Required for all operations
     */
    @NotNull(message = "Customer type is required")
    @JsonProperty("customerType")
    private CustomerType customerType;
}
