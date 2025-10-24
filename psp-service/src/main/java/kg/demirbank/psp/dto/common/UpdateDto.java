package kg.demirbank.psp.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import kg.demirbank.psp.enums.Status;
import lombok.Data;

/**
 * Common update request DTO
 * Used for status updates in both incoming and outgoing requests
 */
@Data
public class UpdateDto {

    /**
     * Transaction status (specified in the table), in this API only final status should be send
     */
    @NotNull
    @JsonProperty("status")
    private Status status;

    /**
     * Transaction status update date-time
     */
    @NotBlank
    @Size(max = 30)
    @JsonProperty("updateDate")
    private String updateDate;
}
