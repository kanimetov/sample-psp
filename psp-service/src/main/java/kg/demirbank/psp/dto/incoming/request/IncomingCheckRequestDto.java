package kg.demirbank.psp.dto.incoming.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import kg.demirbank.psp.dto.common.KeyValueDto;
import lombok.Data;

import java.util.List;

/**
 * Incoming check request DTO (Operator → PSP)
 * Used when PSP acts as beneficiary receiving check requests from Operator
 */
@Data
public class IncomingCheckRequestDto {
    /**
     * Payment link type, Field ID=01 from QR
     */
    @NotBlank
    @Pattern(regexp = "staticQr|dynamicQr")
    @JsonProperty("qrType")
    private String qrType;

    /**
     * Unique identificator of merchant provider (QR Acquirer), Field ID=32, SubID=00 from QR
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
     * Service code in the Payment system of QR Acquirer, Field ID=32, SubID=01 from QR
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
    @Min(0) @Max(9999)
    @JsonProperty("merchantCode")
    private Integer merchantCode;

    /**
     * Currency, by default always "417", Field ID=53 from QR
     */
    @NotBlank
    @Pattern(regexp = "\\d{3}")
    @JsonProperty("currencyCode")
    private String currencyCode = "417";

    /**
     * Transaction ID in the QR Acquirer system, Field ID=32, SubID=11 from QR
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
     * Payment amount (in tyiyns), could be from QR or from Payment App/ Sender Bank
     */
    @NotNull
    @Digits(integer = 13, fraction = 0)
    @Positive
    @JsonProperty("amount")
    private Long amount;

    /**
     * Last 4 symbols of payment link hash string, Field ID=63 from QR
     */
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{4}$")
    @JsonProperty("qrLinkHash")
    private String qrLinkHash;

    @Size(max = 5)
    @Valid
    @JsonProperty("extra")
    private List<KeyValueDto> extra;
}
