package kg.demirbank.psp.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Common DTO for key-value pairs
 * Used in both incoming and outgoing requests for additional data
 */
@Data
public class KeyValueDto {
    @NotBlank
    @Size(max = 64)
    private String key;

    @NotBlank
    @Size(max = 256)
    private String value;
}