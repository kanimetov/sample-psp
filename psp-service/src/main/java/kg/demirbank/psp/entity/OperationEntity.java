package kg.demirbank.psp.entity;

import jakarta.persistence.*;
import kg.demirbank.psp.enums.OperationType;
import kg.demirbank.psp.enums.TransactionType;
import kg.demirbank.psp.enums.Status;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Unified entity for storing all PSP operations (check, create, execute, update).
 * This table consolidates check requests and transactions into a single table
 * with proper direction tracking and field nullability based on operation type.
 */
@Entity
@Table(name = "operations")
public class OperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operations_seq")
    @SequenceGenerator(name = "operations_seq", sequenceName = "OPERATIONS_SEQ", allocationSize = 1)
    private Long id;

    /**
     * PSP's unified transaction identifier
     * For INCOMING: uses senderTransactionId from request
     * For OUTGOING: generated UUID
     * For CHECK: always generated UUID
     */
    @Column(name = "psp_transaction_id", nullable = false, unique = true, length = 50)
    private String pspTransactionId;

    /**
     * Operation type: CHECK, CREATE, EXECUTE, UPDATE
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    /**
     * Transfer direction: IN (incoming to PSP), OUT (outgoing from PSP), OWN (internal)
     */
    @Column(name = "transfer_direction", nullable = false, length = 3)
    private String transferDirection;

    /**
     * Operator's transaction ID
     * NULL for CHECK operations
     * Populated after CREATE for transactions
     */
    @Column(name = "transaction_id", unique = true, length = 32)
    private String transactionId;

    /**
     * Unified receipt ID
     * For INCOMING: uses senderReceiptId from request
     * For OUTGOING: generated receipt ID
     * NULL for CHECK operations
     */
    @Column(name = "receipt_id", unique = true, length = 20)
    private String receiptId;

    /**
     * Payment link type, Field ID=01 from QR
     */
    @Column(name = "qr_type", nullable = false, length = 32)
    private String qrType;

    /**
     * Unique identificator of merchant provider, Field ID=32, SubID=00 from QR
     */
    @Column(name = "merchant_provider", nullable = false, length = 32)
    private String merchantProvider;

    /**
     * Service provider name, Field ID=59 from QR
     */
    @Column(name = "merchant_id", length = 32)
    private String merchantId;

    /**
     * Service code in the Payment system, Field ID=32, SubID=01 from QR
     */
    @Column(name = "service_id", length = 32)
    private String serviceId;

    /**
     * Service name in the Payment system, Field ID=33 SubID=01 from QR
     */
    @Column(name = "service_name", length = 32)
    private String serviceName;

    /**
     * Unique identifier of the payer within the service, Field ID=32, SubID=10 from QR
     */
    @Column(name = "beneficiary_account_number", length = 32)
    private String beneficiaryAccountNumber;

    /**
     * Service provider code (MCC), Field ID=52 from QR
     */
    @Column(name = "merchant_code", nullable = false)
    private Integer merchantCode;

    /**
     * Currency, by default always "417", Field ID=53 from QR
     */
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    /**
     * Transaction ID from QR, Field ID=32, SubID=11 from QR
     */
    @Column(name = "qr_transaction_id", length = 32)
    private String qrTransactionId;

    /**
     * Comment for payment, Field ID=34
     */
    @Column(name = "qr_comment", length = 99)
    private String qrComment;

    /**
     * Customer type: 1=Individual, 2=Corporate
     * Required for all operations
     */
    @Column(name = "customer_type", nullable = false, length = 1)
    private String customerType;

    /**
     * Payment amount (in tyiyns)
     */
    @Column(name = "amount", nullable = false)
    private Long amount;

    /**
     * Last 4 symbols of payment link hash string, Field ID=63 from QR
     */
    @Column(name = "qr_link_hash", nullable = false, length = 4)
    private String qrLinkHash;

    /**
     * Transaction type - nullable for checks
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    /**
     * Transaction status - nullable for checks, required for transactions
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;

    /**
     * Beneficiary name - from check response
     */
    @Column(name = "beneficiary_name", length = 100)
    private String beneficiaryName;

    /**
     * Request hash for signature verification
     */
    @Column(name = "request_hash", length = 255)
    private String requestHash;

    /**
     * API version used for the request
     */
    @Column(name = "api_version", length = 10)
    private String apiVersion;

    /**
     * Extra data associated with this operation
     */
    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExtraDataEntity> extraData;

    // Timestamps
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

    // Error handling and retry logic
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
    public OperationEntity() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPspTransactionId() {
        return pspTransactionId;
    }

    public void setPspTransactionId(String pspTransactionId) {
        this.pspTransactionId = pspTransactionId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getTransferDirection() {
        return transferDirection;
    }

    public void setTransferDirection(String transferDirection) {
        this.transferDirection = transferDirection;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
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
        OperationEntity that = (OperationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OperationEntity{" +
                "id=" + id +
                ", pspTransactionId='" + pspTransactionId + '\'' +
                ", operationType=" + operationType +
                ", transferDirection='" + transferDirection + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
