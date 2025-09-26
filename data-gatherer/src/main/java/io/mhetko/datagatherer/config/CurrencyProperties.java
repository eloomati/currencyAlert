package io.mhetko.datagatherer.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "dg")
@Getter
@Setter
@NoArgsConstructor
public class CurrencyProperties {
    private String base;
    private List<String> mainCurrencies;
    private double rateChangeThreshold;
}
