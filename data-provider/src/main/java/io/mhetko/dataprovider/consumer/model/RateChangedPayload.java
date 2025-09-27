package io.mhetko.dataprovider.consumer.model;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class RateChangedPayload {
    private String base;
    private String symbol;
    private Double rate;
    private OffsetDateTime asOf;
}
