package kg.demirbank.psp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KeyValueDto {
    @NotBlank
    @Size(max = 64)
    private String key;

    @NotBlank
    @Size(max = 256)
    private String value;
}

