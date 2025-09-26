package io.mhetko.datagatherer.dto;

import java.time.OffsetDateTime;

public record RateDto(
        String base,
        String target,
        Double rate,
        OffsetDateTime asOf
) {}
