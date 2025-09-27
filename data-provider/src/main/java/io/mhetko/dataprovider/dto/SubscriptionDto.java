package io.mhetko.dataprovider.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class SubscriptionDto {
    private UUID id;
    private UUID userId;
    private String symbol;
    private boolean isActive;
    private OffsetDateTime createdAt;
    private BigDecimal threshold;
}
