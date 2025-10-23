package kg.demirbank.psp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import kg.demirbank.psp.enums.Status;
import lombok.Data;

@Data
public class UpdateDto {

    @NotNull
    @JsonProperty("status")
    private Status status;

    @NotBlank
    @Size(max = 30)
    @JsonProperty("updateDate")
    private String updateDate;
}

