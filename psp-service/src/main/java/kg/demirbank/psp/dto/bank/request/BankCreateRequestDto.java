package kg.demirbank.psp.dto.bank.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kg.demirbank.psp.dto.common.ELQRData;
import kg.demirbank.psp.dto.common.KeyValueDto;
import kg.demirbank.psp.enums.CustomerType;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for bank transaction creation
 * Used when creating internal bank transaction
 */
@Data
public class BankCreateRequestDto implements ELQRData {
    
    /**
     * Payment amount in tyiyns
     */
    @NotNull
    @Positive
    @JsonProperty("amount")
    private Long amount;
    
    /**
     * Customer type: Individual or Corporate
     */
    @NotNull
    @JsonProperty("customerType")
    private CustomerType customerType;
    
    // ELQR Data fields
    
    /**
     * Payment link type, Field ID=01 from QR
     */
    @JsonProperty("qrType")
    private String qrType;
    
    /**
     * Unique identificator of merchant provider, Field ID=32, SubID=00 from QR
     */
    @JsonProperty("merchantProvider")
    private String merchantProvider;
    
    /**
     * Service provider name, Field ID=59 from QR
     */
    @JsonProperty("merchantId")
    private String merchantId;
    
    /**
     * Service code in the Payment system, Field ID=32, SubID=01 from QR
     */
    @JsonProperty("serviceId")
    private String serviceId;
    
    /**
     * Service name in the Payment system, Field ID=33 SubID=01 from QR
     */
    @JsonProperty("serviceName")
    private String serviceName;
    
    /**
     * Unique identifier of the payer within the service, Field ID=32, SubID=10 from QR
     */
    @JsonProperty("beneficiaryAccountNumber")
    private String beneficiaryAccountNumber;
    
    /**
     * Service provider code (MCC), Field ID=52 from QR
     */
    @JsonProperty("merchantCode")
    private Integer merchantCode;
    
    /**
     * Currency, by default always "417", Field ID=53 from QR
     */
    @JsonProperty("currencyCode")
    private String currencyCode;
    
    /**
     * Transaction ID from QR, Field ID=32, SubID=11 from QR
     */
    @JsonProperty("qrTransactionId")
    private String qrTransactionId;
    
    /**
     * Comment for payment, Field ID=34
     */
    @JsonProperty("qrComment")
    private String qrComment;
    
    /**
     * Last 4 symbols of payment link hash string, Field ID=63 from QR
     */
    @JsonProperty("qrLinkHash")
    private String qrLinkHash;
    
    /**
     * Additional QR code data (extra fields)
     */
    @Size(max = 5)
    @Valid
    @JsonProperty("extra")
    private List<KeyValueDto> extra;
}
