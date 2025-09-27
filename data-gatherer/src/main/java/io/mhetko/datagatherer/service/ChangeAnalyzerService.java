package io.mhetko.datagatherer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChangeAnalyzerService {

    private final RateCacheService rateCacheService;

    /**
     * Sprawdza, czy zmiana kursu przekroczyła zadany próg.
     * @param base waluta bazowa
     * @param symbol waluta docelowa
     * @param threshold próg zmiany (np. 0.05 dla 5%)
     * @return true jeśli zmiana przekracza próg, false w przeciwnym razie
     */
    public boolean hasRateChanged(String base, String symbol, double threshold) {
        List<Double> rates = rateCacheService.getLastRates(base, symbol);

        if (rates == null || rates.size() < 2) {
            return false;
        }

        double last = rates.get(0);
        double previous = rates.get(1);
        double change = Math.abs(last - previous);

        return change > threshold;
    }
}

