package io.mhetko.dataprovider.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ExchangeRateHistoryDto(
        UUID id,
        String base,
        String symbol,
        Double rate,
        OffsetDateTime asOf,
        OffsetDateTime ingestedAt
) {}
