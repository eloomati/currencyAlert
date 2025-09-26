package io.mhetko.datagatherer.service;

import io.mhetko.datagatherer.config.CurrencyProperties;
import io.mhetko.datagatherer.provider.OpenExchangeRatesClient;
import io.mhetko.datagatherer.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrentUpdateService {

    private final OpenExchangeRatesClient oerClient;
    private final RateRepository rateRepository;
    private final CurrencyProperties currencyProperties;

    public void updateRates() {
        Map<String, Double> rates = oerClient.latest("PLN", currencyProperties.getMainCurrencies());

        rates.forEach((symbol, rate) -> {
            System.out.println(symbol + ": " + rate);
        });
    }

}
