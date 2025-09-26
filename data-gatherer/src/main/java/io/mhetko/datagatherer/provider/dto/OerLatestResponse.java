package io.mhetko.datagatherer.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OerLatestResponse(
        String base,
        long timestamp,
        Map<String, Double> rates
) {
    public OerLatestResponse(
            @JsonProperty("base") String base,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("rates") Map<String, Double> rates
    ) {
        this.base = base;
        this.timestamp = timestamp;
        this.rates = rates;
    }
}
