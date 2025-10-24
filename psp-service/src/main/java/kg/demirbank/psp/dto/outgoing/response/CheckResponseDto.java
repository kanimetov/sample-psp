package kg.demirbank.psp.dto.outgoing.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.demirbank.psp.enums.TransactionType;
import lombok.Data;

/**
 * Outgoing check response DTO (Operator → PSP)
 * Used when PSP acts as sender receiving check responses from Operator
 */
@Data
public class CheckResponseDto {
    @JsonProperty("beneficiaryName")
    private String beneficiaryName;

    @JsonProperty("transactionType")
    private TransactionType transactionType;
}
