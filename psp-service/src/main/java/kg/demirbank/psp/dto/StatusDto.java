package kg.demirbank.psp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.demirbank.psp.enums.CustomerType;
import kg.demirbank.psp.enums.Status;
import lombok.Data;

@Data
public class StatusDto {
    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("transactionType")
    private CustomerType transactionType;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("beneficiaryName")
    private String beneficiaryName;

    @JsonProperty("customerType")
    private String customerType;

    @JsonProperty("receiptId")
    private String receiptId;

    @JsonProperty("createdDate")
    private String createdDate;

    @JsonProperty("executedDate")
    private String executedDate;
}

