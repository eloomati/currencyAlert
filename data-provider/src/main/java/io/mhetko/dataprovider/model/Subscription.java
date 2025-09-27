package io.mhetko.dataprovider.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt;

    @Column(name = "threshold", nullable = false, precision = 8, scale = 4)
    private BigDecimal threshold = BigDecimal.ZERO;
}