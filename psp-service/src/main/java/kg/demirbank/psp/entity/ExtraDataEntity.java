package kg.demirbank.psp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity for storing extra key-value data associated with operations.
 * This table stores additional metadata that can be attached to operations.
 */
@Entity
@Table(name = "extra_data")
public class ExtraDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "extra_data_seq")
    @SequenceGenerator(name = "extra_data_seq", sequenceName = "EXTRA_DATA_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "key_name", nullable = false, length = 64)
    private String key;

    @Column(name = "value_data", nullable = false, length = 256)
    private String value;

    @Column(name = "order_index")
    private Integer orderIndex;

    // Many-to-one relationship with OperationEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id")
    private OperationEntity operation;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "data_type", length = 20)
    private String dataType; // STRING, NUMBER, BOOLEAN, JSON, etc.

    @Column(name = "description", length = 200)
    private String description;

    // Constructors
    public ExtraDataEntity() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public OperationEntity getOperation() {
        return operation;
    }

    public void setOperation(OperationEntity operation) {
        this.operation = operation;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraDataEntity that = (ExtraDataEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ExtraDataEntity{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", orderIndex=" + orderIndex +
                ", isActive=" + isActive +
                '}';
    }
}
