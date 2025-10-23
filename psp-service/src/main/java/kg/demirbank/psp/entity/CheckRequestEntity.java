package kg.demirbank.psp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Entity for storing check request operations.
 * This table stores all check requests made to the PSP service.
 */
@Entity
@Table(name = "check_requests")
public class CheckRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "check_requests_seq")
    @SequenceGenerator(name = "check_requests_seq", sequenceName = "CHECK_REQUESTS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "qr_type", nullable = false, length = 32)
    private String qrType;

    @Column(name = "merchant_provider", nullable = false, length = 32)
    private String merchantProvider;

    @Column(name = "merchant_id", length = 32)
    private String merchantId;

    @Column(name = "service_id", length = 32)
    private String serviceId;

    @Column(name = "service_name", length = 32)
    private String serviceName;

    @Column(name = "beneficiary_account_number", length = 32)
    private String beneficiaryAccountNumber;

    @Column(name = "merchant_code", nullable = false)
    private Integer merchantCode;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "qr_transaction_id", length = 32)
    private String qrTransactionId;

    @Column(name = "qr_comment", length = 32)
    private String qrComment;

    @Column(name = "customer_type", nullable = false, length = 1)
    private String customerType;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "qr_link_hash", nullable = false, length = 4)
    private String qrLinkHash;

    @Column(name = "request_hash", length = 255)
    private String requestHash;

    @Column(name = "api_version", length = 10)
    private String apiVersion;

    @OneToMany(mappedBy = "checkRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExtraDataEntity> extraData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "response_status", length = 20)
    private String responseStatus;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    // Constructors
    public CheckRequestEntity() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }

    public String getMerchantProvider() {
        return merchantProvider;
    }

    public void setMerchantProvider(String merchantProvider) {
        this.merchantProvider = merchantProvider;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public Integer getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(Integer merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getQrTransactionId() {
        return qrTransactionId;
    }

    public void setQrTransactionId(String qrTransactionId) {
        this.qrTransactionId = qrTransactionId;
    }

    public String getQrComment() {
        return qrComment;
    }

    public void setQrComment(String qrComment) {
        this.qrComment = qrComment;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getQrLinkHash() {
        return qrLinkHash;
    }

    public void setQrLinkHash(String qrLinkHash) {
        this.qrLinkHash = qrLinkHash;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public List<ExtraDataEntity> getExtraData() {
        return extraData;
    }

    public void setExtraData(List<ExtraDataEntity> extraData) {
        this.extraData = extraData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckRequestEntity that = (CheckRequestEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CheckRequestEntity{" +
                "id=" + id +
                ", qrType='" + qrType + '\'' +
                ", merchantProvider='" + merchantProvider + '\'' +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }
}
