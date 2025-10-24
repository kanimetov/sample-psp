package kg.demirbank.psp.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * ELQR (Electronic QR) Data Interface
 * Contains all QR code properties extracted from QR codes
 * Used across all request DTOs that process QR code data
 */
public interface ELQRData {
    
    /**
     * Payment link type, Field ID=01 from QR
     */
    @NotBlank
    @Pattern(regexp = "staticQr|dynamicQr")
    @JsonProperty("qrType")
    String getQrType();
    
    /**
     * Unique identificator of merchant provider (QR Acquirer), Field ID=32, SubID=00 from QR
     */
    @NotBlank
    @Size(max = 32)
    @JsonProperty("merchantProvider")
    String getMerchantProvider();
    
    /**
     * Service provider name, Field ID=59 from QR
     */
    @Size(max = 32)
    @JsonProperty("merchantId")
    String getMerchantId();
    
    /**
     * Service code in the Payment system of QR Acquirer, Field ID=32, SubID=01 from QR
     */
    @Size(max = 32)
    @JsonProperty("serviceId")
    String getServiceId();
    
    /**
     * Service name in the Payment system, Field ID=33 SubID=01 from QR
     */
    @Size(max = 32)
    @JsonProperty("serviceName")
    String getServiceName();
    
    /**
     * Unique identifier of the payer within the service (лицевой счет), Field ID=32, SubID=10 from QR
     */
    @Size(max = 32)
    @JsonProperty("beneficiaryAccountNumber")
    String getBeneficiaryAccountNumber();
    
    /**
     * Service provider code (MCC), Field ID=52 from QR
     */
    @NotNull
    @Min(0) @Max(9999)
    @JsonProperty("merchantCode")
    Integer getMerchantCode();
    
    /**
     * Currency, by default always "417", Field ID=53 from QR
     */
    @NotBlank
    @Pattern(regexp = "\\d{3}")
    @JsonProperty("currencyCode")
    String getCurrencyCode();
    
    /**
     * Transaction ID in the QR Acquirer system, Field ID=32, SubID=11 from QR
     */
    @Size(max = 32)
    @JsonProperty("qrTransactionId")
    String getQrTransactionId();
    
    /**
     * Comment for payment, Field ID=34
     */
    @Size(max = 99)
    @JsonProperty("qrComment")
    String getQrComment();
    
    /**
     * Last 4 symbols of payment link hash string, Field ID=63 from QR
     */
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{4}$")
    @JsonProperty("qrLinkHash")
    String getQrLinkHash();
    
    /**
     * Additional QR code data (extra fields)
     * Maximum of 5 additional key-value pairs can be stored
     */
    @Size(max = 5)
    @Valid
    @JsonProperty("extra")
    List<KeyValueDto> getExtra();
}
