package io.mhetko.datagatherer.scheduler;

import io.mhetko.datagatherer.config.CurrencyProperties;
import io.mhetko.datagatherer.service.CurrentUpdateService;
import io.mhetko.datagatherer.service.RateChangeNotifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@EnableScheduling
@Service
@RequiredArgsConstructor
public class RateUpdateScheduler {

    private final CurrentUpdateService currentUpdateService;
    private final CurrencyProperties currencyProperties;
    private final RateChangeNotifierService rateChangeNotifierService;

    @Scheduled(cron = "${dg.fetch-cron:0 0 * * * ?}")
    public void updateRates() {
        String base = currencyProperties.getBase();
        Iterable<String> symbols = currencyProperties.getMainCurrencies();
        double threshold = currencyProperties.getRateChangeThreshold();
        OffsetDateTime asOf = OffsetDateTime.now();

        currentUpdateService.fetchAndSaveRates(base, symbols, asOf);
        notifyForRates(base, symbols, threshold);
    }

    private void notifyForRates(String base, Iterable<String> symbols, double threshold) {
        for (String symbol : symbols) {
            Double newRate = currentUpdateService.getLatestRate(base, symbol);
            if (newRate != null) {
                rateChangeNotifierService.notifyIfRateChanged(base, symbol, threshold, newRate);
            }
        }
    }
}