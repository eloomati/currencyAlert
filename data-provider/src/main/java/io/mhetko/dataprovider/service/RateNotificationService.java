package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.Subscription;
import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateNotificationService {

    private final ExchangeRateHistoryService historyService;
    private final EmailService emailService;

    public void checkAndNotify(Subscription subscription) {
        List<ExchangeRateHistoryEntity> history = historyService.getLastTwo(subscription.getSymbol());
        if (history.size() < 2) return;

        double diff = Math.abs(history.get(0).getRate() - history.get(1).getRate());
        if (BigDecimal.valueOf(diff).compareTo(subscription.getThreshold()) >= 0) {
            emailService.sendNotification(
                    subscription.getUser().getEmail(),
                    "Zmiana kursu " + subscription.getSymbol(),
                    "Kurs zmienił się o " + diff
            );
        }
    }
}

