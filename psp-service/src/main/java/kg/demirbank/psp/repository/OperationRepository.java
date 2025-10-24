package kg.demirbank.psp.repository;

import kg.demirbank.psp.entity.OperationEntity;
import kg.demirbank.psp.enums.OperationType;
import kg.demirbank.psp.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for OperationEntity
 * Provides data access methods for unified operations table
 */
@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, Long> {

    /**
     * Find operation by PSP transaction ID
     */
    Optional<OperationEntity> findByPspTransactionId(String pspTransactionId);

    /**
     * Find operation by operator's transaction ID
     */
    Optional<OperationEntity> findByTransactionId(String transactionId);

    /**
     * Find operation by receipt ID
     */
    Optional<OperationEntity> findByReceiptId(String receiptId);

    /**
     * Find operations by operation type
     */
    List<OperationEntity> findByOperationType(OperationType operationType);

    /**
     * Find operations by transfer direction
     */
    List<OperationEntity> findByTransferDirection(String transferDirection);

    /**
     * Find operations by status
     */
    List<OperationEntity> findByStatus(Status status);

    /**
     * Find operations by operation type and transfer direction
     */
    List<OperationEntity> findByOperationTypeAndTransferDirection(OperationType operationType, String transferDirection);

    /**
     * Find operations by merchant code
     */
    List<OperationEntity> findByMerchantCode(Integer merchantCode);

    /**
     * Find operations by QR link hash
     */
    List<OperationEntity> findByQrLinkHash(String qrLinkHash);

    /**
     * Find operations created after specified date
     */
    List<OperationEntity> findByCreatedAtAfter(LocalDateTime dateTime);

    /**
     * Find operations created between dates
     */
    List<OperationEntity> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * Find operations by customer type
     */
    List<OperationEntity> findByCustomerType(String customerType);

    /**
     * Find operations that are not final (for retry logic)
     */
    @Query("SELECT o FROM OperationEntity o WHERE o.isFinal = false AND o.retryCount < o.maxRetries")
    List<OperationEntity> findNonFinalOperationsForRetry();

    /**
     * Find operations by PSP transaction ID and operation type
     */
    Optional<OperationEntity> findByPspTransactionIdAndOperationType(String pspTransactionId, OperationType operationType);

    /**
     * Find operations by operator transaction ID and operation type
     */
    Optional<OperationEntity> findByTransactionIdAndOperationType(String transactionId, OperationType operationType);

    /**
     * Check if operation exists by PSP transaction ID
     */
    boolean existsByPspTransactionId(String pspTransactionId);

    /**
     * Check if operation exists by operator transaction ID
     */
    boolean existsByTransactionId(String transactionId);

    /**
     * Check if operation exists by receipt ID
     */
    boolean existsByReceiptId(String receiptId);

    /**
     * Find operations by merchant provider
     */
    List<OperationEntity> findByMerchantProvider(String merchantProvider);

    /**
     * Find operations by amount range
     */
    List<OperationEntity> findByAmountBetween(Long minAmount, Long maxAmount);

    /**
     * Count operations by operation type
     */
    long countByOperationType(OperationType operationType);

    /**
     * Count operations by transfer direction
     */
    long countByTransferDirection(String transferDirection);

    /**
     * Count operations by status
     */
    long countByStatus(Status status);

    /**
     * Find operations for audit trail by date range
     */
    @Query("SELECT o FROM OperationEntity o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<OperationEntity> findOperationsForAuditTrail(@Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Find operations by multiple criteria for reporting
     */
    @Query("SELECT o FROM OperationEntity o WHERE " +
           "(:operationType IS NULL OR o.operationType = :operationType) AND " +
           "(:transferDirection IS NULL OR o.transferDirection = :transferDirection) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:merchantCode IS NULL OR o.merchantCode = :merchantCode) AND " +
           "(:customerType IS NULL OR o.customerType = :customerType) AND " +
           "o.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY o.createdAt DESC")
    List<OperationEntity> findOperationsByCriteria(@Param("operationType") OperationType operationType,
                                                   @Param("transferDirection") String transferDirection,
                                                   @Param("status") Status status,
                                                   @Param("merchantCode") Integer merchantCode,
                                                   @Param("customerType") String customerType,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
}
