package io.mhetko.datagatherer.service;

import io.mhetko.datagatherer.repository.ExchangeRateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ChangeAnalyzerService {

    private final ExchangeRateHistoryRepository exchangeRateHistoryRepository;

    /**
     * Sprawdza, czy zmiana kursu przekroczyła zadany próg.
     * @param base waluta bazowa
     * @param symbol waluta docelowa
     * @param threshold próg zmiany (np. 0.05 dla 5%)
     * @return true jeśli zmiana przekracza próg, false w przeciwnym razie
     */
    public boolean hasRateChanged(String base, String symbol, double threshold) {
        var rates = exchangeRateHistoryRepository
                .findTop2ByBaseAndSymbolOrderByAsOfDesc(base, symbol);

        if (rates.size() < 2) {
            return false; 
        }

        double last = rates.get(0).getRate();
        double previous = rates.get(1).getRate();
        double change = Math.abs(last - previous);

        return change > threshold;
    }
}

