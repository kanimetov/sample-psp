package kg.demirbank.psp.entity;

import jakarta.persistence.*;
import kg.demirbank.psp.enums.CustomerType;
import kg.demirbank.psp.enums.Status;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Entity for storing transaction operations (create/execute/update).
 * This table stores all transaction lifecycle operations in the PSP service.
 */
@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_seq")
    @SequenceGenerator(name = "transactions_seq", sequenceName = "TRANSACTIONS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 32)
    private String transactionId;

    @Column(name = "psp_transaction_id", nullable = false, unique = true, length = 50)
    private String pspTransactionId;

    @Column(name = "receipt_id", nullable = false, unique = true, length = 20)
    private String receiptId;

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

    @Column(name = "qr_transaction_id", unique = true, length = 32)
    private String qrTransactionId;

    @Column(name = "qr_comment", length = 32)
    private String qrComment;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "qr_link_hash", nullable = false, length = 4)
    private String qrLinkHash;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "transaction_type", nullable = false)
    private CustomerType transactionType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "beneficiary_name", length = 100)
    private String beneficiaryName;

    @Column(name = "request_hash", length = 255)
    private String requestHash;

    @Column(name = "api_version", length = 10)
    private String apiVersion;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExtraDataEntity> extraData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "last_status_update_at")
    private LocalDateTime lastStatusUpdateAt;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;

    @Column(name = "is_final", nullable = false)
    private Boolean isFinal = false;

    // Audit fields
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // Constructors
    public TransactionEntity() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPspTransactionId() {
        return pspTransactionId;
    }

    public void setPspTransactionId(String pspTransactionId) {
        this.pspTransactionId = pspTransactionId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
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

    public CustomerType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(CustomerType transactionType) {
        this.transactionType = transactionType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
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

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public LocalDateTime getLastStatusUpdateAt() {
        return lastStatusUpdateAt;
    }

    public void setLastStatusUpdateAt(LocalDateTime lastStatusUpdateAt) {
        this.lastStatusUpdateAt = lastStatusUpdateAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Boolean getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", pspTransactionId='" + pspTransactionId + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
