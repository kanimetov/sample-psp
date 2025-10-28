package kg.demirbank.psp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity for storing merchant webhook configurations
 * Represents registered merchants who should receive webhook notifications
 */
@Entity
@Table(name = "merchant_webhooks")
public class MerchantWebhookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "merchant_webhooks_seq")
    @SequenceGenerator(name = "merchant_webhooks_seq", sequenceName = "MERCHANT_WEBHOOKS_SEQ", allocationSize = 1)
    private Long id;

    /**
     * Merchant name for identification
     */
    @Column(name = "merchant_name", nullable = false, length = 100)
    private String merchantName;

    /**
     * Application ID used for matching with serviceName (case-insensitive)
     * This ID corresponds to the serviceName field from QR codes
     */
    @Column(name = "app_id", nullable = false, unique = true, length = 32)
    private String appId;

    /**
     * API key name to be sent as HTTP header
     */
    @Column(name = "api_key_name", nullable = false, length = 100)
    private String apiKeyName;

    /**
     * API key value to be sent as HTTP header value
     */
    @Column(name = "api_key_value", nullable = false, length = 255)
    private String apiKeyValue;

    /**
     * Target URL for webhook delivery
     */
    @Column(name = "target_url", nullable = false, length = 500)
    private String targetUrl;

    /**
     * Whether this webhook configuration is active
     * Only active merchants receive webhook notifications
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * User who created this webhook configuration
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * Timestamp when webhook configuration was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * User who last updated this webhook configuration
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * Timestamp when webhook configuration was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public MerchantWebhookEntity() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiKeyName() {
        return apiKeyName;
    }

    public void setApiKeyName(String apiKeyName) {
        this.apiKeyName = apiKeyName;
    }

    public String getApiKeyValue() {
        return apiKeyValue;
    }

    public void setApiKeyValue(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MerchantWebhookEntity that = (MerchantWebhookEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MerchantWebhookEntity{" +
                "id=" + id +
                ", merchantName='" + merchantName + '\'' +
                ", appId='" + appId + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}

