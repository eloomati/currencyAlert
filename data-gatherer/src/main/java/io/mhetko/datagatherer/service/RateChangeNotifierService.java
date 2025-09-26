package io.mhetko.datagatherer.service;

import io.mhetko.datagatherer.model.RateChangedEvent;
import io.mhetko.datagatherer.producer.RateChangedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateChangeNotifierService {

    private final ChangeAnalyzerService changeAnalyzerService;
    private final RateChangedProducer rateChangedProducer;

    public void notifyIfRateChanged(String base, String symbol, double threshold, double newRate) {
        if (changeAnalyzerService.hasRateChanged(base, symbol, threshold)) {
            var event = new RateChangedEvent(base, symbol, newRate);
            rateChangedProducer.sendJson(event);
        }
    }
}
