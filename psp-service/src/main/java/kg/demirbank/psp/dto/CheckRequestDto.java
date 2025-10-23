package kg.demirbank.psp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CheckRequestDto {
    @NotBlank
    @Pattern(regexp = "staticQr|dynamicQr")
    @JsonProperty("qrType")
    private String qrType;

    @NotBlank
    @Size(max = 32)
    @JsonProperty("merchantProvider")
    private String merchantProvider;

    @Size(max = 32)
    @JsonProperty("merchantId")
    private String merchantId;

    @Size(max = 32)
    @JsonProperty("serviceId")
    private String serviceId;

    @Size(max = 32)
    @JsonProperty("serviceName")
    private String serviceName;

    @Size(max = 32)
    @JsonProperty("beneficiaryAccountNumber")
    private String beneficiaryAccountNumber;

    @NotNull
    @Min(0) @Max(9999)
    @JsonProperty("merchantCode")
    private Integer merchantCode;

    @NotBlank
    @Pattern(regexp = "\\d{3}")
    @JsonProperty("currencyCode")
    private String currencyCode = "417";

    @Size(max = 32)
    @JsonProperty("qrTransactionId")
    private String qrTransactionId;

    @Size(max = 32)
    @JsonProperty("qrComment")
    private String qrComment;

    @NotBlank
    @Pattern(regexp = "1|2")
    @JsonProperty("customerType")
    private String customerType;

    @NotNull
    @Digits(integer = 13, fraction = 0)
    @Positive
    @JsonProperty("amount")
    private Long amount;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{4}$")
    @JsonProperty("qrLinkHash")
    private String qrLinkHash;

    @Size(max = 5)
    @Valid
    @JsonProperty("extra")
    private List<KeyValueDto> extra;
}

