package io.mhetko.datagatherer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "dg")
public class CurrencyProperties {
    private List<String> mainCurrencies;

    public List<String> getMainCurrencies() {
        return mainCurrencies;
    }

    public void setMainCurrencies(List<String> mainCurrencies) {
        this.mainCurrencies = mainCurrencies;
    }
}
