package kg.demirbank.psp.repository;

import kg.demirbank.psp.entity.MerchantWebhookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for merchant webhook configurations
 * Provides data access operations for merchant webhook settings
 */
@Repository
public interface MerchantWebhookRepository extends JpaRepository<MerchantWebhookEntity, Long> {
    
    /**
     * Find active merchant webhook by application ID
     * Case-insensitive search for app_id to match serviceName from operations
     * 
     * @param appId application ID to search for (case-insensitive)
     * @param isActive whether the merchant is active
     * @return Optional containing the merchant webhook entity if found
     */
    Optional<MerchantWebhookEntity> findByAppIdIgnoreCaseAndIsActive(String appId, Boolean isActive);
}

