package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.Subscription;
import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateNotificationService {

    private final ExchangeRateHistoryService historyService;
    private final EmailService emailService;

    public void checkAndNotify(Subscription subscription) {
        log.info("Checking subscription: id={}, symbol={}, threshold={}",
                subscription.getId(), subscription.getSymbol(), subscription.getThreshold());

        List<ExchangeRateHistoryEntity> history = historyService.getLastTwo(subscription.getSymbol());
        log.info("Fetched {} history records for symbol {}", history.size(), subscription.getSymbol());

        if (history.size() < 2) {
            log.warn("Not enough data to notify for symbol {}", subscription.getSymbol());
            return;
        }

        double diff = Math.abs(history.get(0).getRate() - history.get(1).getRate());
        log.info("Exchange rate difference: {}", diff);

        if (BigDecimal.valueOf(diff).compareTo(subscription.getThreshold()) >= 0) {
            log.info("Sending notification to email: {}", subscription.getUser().getEmail());
            emailService.sendNotification(
                    subscription.getUser().getEmail(),
                    "Exchange rate change " + subscription.getSymbol(),
                    "Exchange rate changed by " + diff
            );
        } else {
            log.info("Exchange rate difference does not exceed threshold, notification will not be sent.");
        }
    }

}

