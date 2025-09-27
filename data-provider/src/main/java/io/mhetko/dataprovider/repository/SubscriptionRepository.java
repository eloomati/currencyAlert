package io.mhetko.dataprovider.repository;

import io.mhetko.dataprovider.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByUserId(UUID userId);
    boolean existsByUserIdAndSymbol(UUID userId, String symbol);
    Optional<Subscription> findByUserIdAndSymbol(UUID userId, String symbol);
    @Query("SELECT s FROM Subscription s JOIN FETCH s.user WHERE s.symbol = :symbol AND s.isActive = true")
    List<Subscription> findBySymbolAndIsActiveTrue(@Param("symbol") String symbol);

}
