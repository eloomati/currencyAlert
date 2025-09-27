package io.mhetko.datagatherer.service;

import io.mhetko.datagatherer.model.CurrencyEntity;
import io.mhetko.datagatherer.model.RateEntity;
import io.mhetko.datagatherer.provider.OpenExchangeRatesClient;
import io.mhetko.datagatherer.repository.CurrencyRepository;
import io.mhetko.datagatherer.repository.RateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.tuple.Pair;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CurrentUpdateService {

    private final RateRepository rateRepository;
    private final RateCacheService rateCacheService;
    private final CurrencyRepository currencyRepository;
    private final OpenExchangeRatesClient openExchangeRatesClient;

    @Transactional
    public void fetchAndSaveRates(String base, Iterable<String> symbols, OffsetDateTime asOf) {
        var rates = openExchangeRatesClient.latest(base, symbols);
        for (var entry : rates.entrySet()) {
            saveRate(base, entry.getKey(), entry.getValue(), asOf);
        }
    }

    public Double getLatestRate(String base, String symbol) {
        var rates = rateCacheService.getLastRates(base, symbol);
        return (rates != null && !rates.isEmpty()) ? rates.get(0) : null;
    }

    private Pair<CurrencyEntity, CurrencyEntity> validateCurrencies(String base, String symbol) {
        CurrencyEntity baseCurrency = currencyRepository.findByCode(base)
                .orElseThrow(() -> new IllegalArgumentException("Base currency not found: " + base));
        CurrencyEntity targetCurrency = currencyRepository.findByCode(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Target currency not found: " + symbol));
        return Pair.of(baseCurrency, targetCurrency);
    }

    private void persistRate(CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, Double rate, OffsetDateTime asOf) {
        rateCacheService.saveRate(baseCurrency.getCode(), targetCurrency.getCode(), rate);
        RateEntity rateEntity = rateRepository.findByBaseAndTarget(baseCurrency, targetCurrency)
                .orElse(RateEntity.builder()
                        .base(baseCurrency)
                        .target(targetCurrency)
                        .build())
                .toBuilder()
                .rate(rate)
                .asOf(asOf)
                .updatedAt(OffsetDateTime.now())
                .build();
        rateRepository.save(rateEntity);
    }

    private void saveRate(String base, String symbol, Double rate, OffsetDateTime asOf) {
        var currencies = validateCurrencies(base, symbol);
        persistRate(currencies.getLeft(), currencies.getRight(), rate, asOf);
    }
}