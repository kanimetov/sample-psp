package kg.demirbank.psp.dto.outgoing.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.demirbank.psp.enums.Status;
import kg.demirbank.psp.enums.TransactionType;
import lombok.Data;

/**
 * Outgoing status response DTO (Operator → PSP)
 * Used when PSP acts as sender receiving status responses from Operator
 */
@Data
public class StatusDto {
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
     * Transaction type (specified in the table)
     * Enum: 10, 20, 30, 40, 50, 60, 70
     */
    @JsonProperty("transactionType")
    private TransactionType transactionType;

    /**
     * Payment amount (in tyiyns), could be from QR or from Payment App/ Sender Bank
     */
    @JsonProperty("amount")
    private Long amount;

    /**
     * Transaction commission (in tyiyns), from beneficiary's system
     */
    @JsonProperty("commission")
    private Long commission;

    /**
     * Payment ID in the sender's system
     */
    @JsonProperty("senderTransactionId")
    private String senderTransactionId;

    /**
     * Sender's receipt number
     */
    @JsonProperty("senderReceiptId")
    private String senderReceiptId;

    /**
     * Transaction creation date-time
     */
    @JsonProperty("createdDate")
    private String createdDate;

    /**
     * Transaction execution date-time, should be empty if not executed
     */
    @JsonProperty("executedDate")
    private String executedDate;
}
