package io.mhetko.datagatherer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


import jakarta.validation.constraints.NotBlank;
import java.util.List;


@Validated
@ConfigurationProperties(prefix = "dg")
public record ProviderProperties(
        @NotBlank String provider, // openexchangerates
        @NotBlank String base, // USD
        List<String> symbols, // [PLN, EUR, GBP]
        @NotBlank String apiUrl,
        @NotBlank String apiKey
) {}
