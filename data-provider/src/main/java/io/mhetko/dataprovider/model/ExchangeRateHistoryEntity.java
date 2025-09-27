package io.mhetko.dataprovider.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "exchange_rate_history", uniqueConstraints = @UniqueConstraint(columnNames = {"base", "symbol", "as_of"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ExchangeRateHistoryEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String base;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private Double rate;

    @Column(name = "as_of", nullable = false)
    private OffsetDateTime asOf;

    @Column(name = "ingested_at", nullable = false)
    private OffsetDateTime ingestedAt;
}

