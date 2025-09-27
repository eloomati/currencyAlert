package io.mhetko.datagatherer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "gatherer_last_rates", uniqueConstraints = @UniqueConstraint(columnNames = {"base_id", "target_id"}))
@Getter
@Setter
@Builder(toBuilder = true)
public class RateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "base_id", nullable = false)
    private CurrencyEntity base;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private CurrencyEntity target;

    @Column(name = "rate", nullable = false)
    private Double rate;

    @Column(name = "as_of", nullable = false)
    private OffsetDateTime asOf;

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
