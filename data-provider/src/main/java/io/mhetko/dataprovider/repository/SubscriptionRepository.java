package io.mhetko.dataprovider.repository;

import io.mhetko.dataprovider.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByUserId(UUID userId);
    boolean existsByUserIdAndSymbol(UUID userId, String symbol);
    Optional<Subscription> findByUserIdAndSymbol(UUID userId, String symbol);
    List<Subscription> findBySymbolAndIsActiveTrue(String symbol);
}
