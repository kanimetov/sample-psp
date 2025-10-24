package kg.demirbank.psp.dto.incoming.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.demirbank.psp.enums.Status;
import lombok.Data;

/**
 * Incoming execute response DTO (PSP → Operator)
 * Used when PSP acts as beneficiary responding to execute requests from Operator
 */
@Data
public class ExecuteResponseDto {
    /**
     * Transaction ID from the Operator's system
     */
    @JsonProperty("transactionId")
    private String transactionId;

    /**
     * Transaction status (specified in the table)
     * Enum: 10, 20, 30, 40, 50
     */
    @JsonProperty("status")
    private Status status;

    /**
     * Payment amount (in tyiyns)
     */
    @JsonProperty("amount")
    private Long amount;

    /**
     * Beneficiary's full name, from the Beneficiary bank's system
     */
    @JsonProperty("beneficiaryName")
    private String beneficiaryName;

    /**
     * Client type of beneficiary, from system of Receiver Bank
     * 1 - Individual; 2 - Corp
     */
    @JsonProperty("customerType")
    private String customerType;

    /**
     * Receipt number from Beneficiary system
     */
    @JsonProperty("receiptId")
    private String receiptId;

    /**
     * Transaction creation date-time
     */
    @JsonProperty("createdDate")
    private String createdDate;

    /**
     * Transaction execution date-time
     */
    @JsonProperty("executedDate")
    private String executedDate;
}
