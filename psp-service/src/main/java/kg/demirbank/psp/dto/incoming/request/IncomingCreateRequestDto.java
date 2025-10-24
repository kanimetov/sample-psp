package kg.demirbank.psp.dto.incoming.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import kg.demirbank.psp.dto.common.ELQRData;
import kg.demirbank.psp.dto.common.KeyValueDto;
import kg.demirbank.psp.enums.TransactionType;
import lombok.Data;

import java.util.List;

/**
 * Incoming create request DTO (Operator → PSP)
 * Used when PSP acts as beneficiary receiving create requests from Operator
 */
@Data
public class IncomingCreateRequestDto implements ELQRData {

    /**
     * Transaction ID from the Operator's system
     */
    @NotNull
    @JsonProperty("transactionId")
    private String transactionId;
    
    /**
     * Payment link type, Field ID=01 from QR
     */
    @NotBlank
    @Pattern(regexp = "staticQr|dynamicQr")
    @JsonProperty("qrType")
    private String qrType;

    /**
     * Unique identificator of merchant provider, Field ID=32, SubID=00 from QR
     */
    @NotBlank
    @Size(max = 32)
    @JsonProperty("merchantProvider")
    private String merchantProvider;

    /**
     * Service provider name, Field ID=59 from QR
     */
    @Size(max = 32)
    @JsonProperty("merchantId")
    private String merchantId;

    /**
     * Service code in the Payment system, Field ID=32, SubID=01 from QR
     */
    @Size(max = 32)
    @JsonProperty("serviceId")
    private String serviceId;

    /**
     * Service name in the Payment system, Field ID=33 SubID=01 from QR
     */
    @Size(max = 32)
    @JsonProperty("serviceName")
    private String serviceName;

    /**
     * Unique identifier of the payer within the service (лицевой счет), Field ID=32, SubID=10 from QR
     */
    @Size(max = 32)
    @JsonProperty("beneficiaryAccountNumber")
    private String beneficiaryAccountNumber;

    /**
     * Service provider code (MCC), Field ID=52 from QR
     */
    @NotNull
    @Min(0)
    @Max(9999)
    @JsonProperty("merchantCode")
    private Integer merchantCode;

    /**
     * Currency, by default always "417", Field ID=53 from QR
     */
    @NotBlank
    @Size(max = 3)
    @JsonProperty("currencyCode")
    private String currencyCode = "417";

    /**
     * Transaction ID from QR, Field ID=32, SubID=11 from QR
     */
    @Size(max = 32)
    @JsonProperty("qrTransactionId")
    private String qrTransactionId;

    /**
     * Comment for payment, Field ID=34
     */
    @Size(max = 32)
    @JsonProperty("qrComment")
    private String qrComment;

    /**
     * Transaction id from the Sender's system
     */
    @NotBlank
    @Size(max = 50)
    @JsonProperty("senderTransactionId")
    private String senderTransactionId;

    /**
     * Sender's receipt number
     */
    @NotBlank
    @Size(max = 20)
    @JsonProperty("senderReceiptId")
    private String senderReceiptId;

    /**
     * Payment amount (in tyiyns)
     */
    @NotNull
    @Positive
    @JsonProperty("amount")
    private Long amount;

    /**
     * Last 4 symbols of payment link hash string, Field ID=63 from QR
     */
    @NotBlank
    @Size(max = 4)
    @JsonProperty("qrLinkHash")
    private String qrLinkHash;

    /**
     * Transaction type (specified in the table)
     * Enum: 10, 20, 30, 40, 50, 60, 70
     */
    @NotNull
    @JsonProperty("transactionType")
    private TransactionType transactionType;

    @Size(max = 5)
    @Valid
    @JsonProperty("extra")
    private List<KeyValueDto> extra;
}
