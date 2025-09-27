package io.mhetko.datagatherer.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OerLatestResponse(
        @JsonProperty("base") String base,
        @JsonProperty("timestamp") long timestamp,
        @JsonProperty("rates") Map<String, Double> rates
) {}
