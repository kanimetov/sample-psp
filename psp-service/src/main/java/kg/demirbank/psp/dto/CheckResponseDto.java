package kg.demirbank.psp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.demirbank.psp.enums.CustomerType;
import lombok.Data;

@Data
public class CheckResponseDto {
    @JsonProperty("beneficiaryName")
    private String beneficiaryName;

    @JsonProperty("transactionType")
    private CustomerType transactionType;
}

