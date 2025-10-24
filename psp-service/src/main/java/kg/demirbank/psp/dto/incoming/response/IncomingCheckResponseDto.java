package kg.demirbank.psp.dto.incoming.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.demirbank.psp.enums.TransactionType;
import lombok.Data;

/**
 * Incoming check response DTO (PSP → Operator)
 * Used when PSP acts as beneficiary responding to check requests from Operator
 */
@Data
public class IncomingCheckResponseDto {
    @JsonProperty("beneficiaryName")
    private String beneficiaryName;

    @JsonProperty("transactionType")
    private TransactionType transactionType;
}
