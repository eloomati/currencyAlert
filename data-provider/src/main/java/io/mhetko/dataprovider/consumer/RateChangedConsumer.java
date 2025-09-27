package io.mhetko.dataprovider.consumer;

import io.mhetko.dataprovider.consumer.model.RateChangedPayload;
import io.mhetko.dataprovider.service.ExchangeRateHistoryService;
import io.mhetko.dataprovider.service.RateNotificationService;
import io.mhetko.dataprovider.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataIntegrityViolationException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateChangedConsumer {

    private final ExchangeRateHistoryService exchangeRateHistoryService;
    private final RateNotificationService rateNotificationService;
    private final SubscriptionService subscriptionService;

    @RabbitListener(queues = "${amqp.queue}")
    public void receiveMessage(RateChangedPayload payload) {
        try {
            exchangeRateHistoryService.saveHistory(
                    payload.getBase(),
                    payload.getSymbol(),
                    payload.getRate(),
                    payload.getAsOf()
            );
        } catch (DataIntegrityViolationException e) {
            log.info("Duplikat kursu: " + payload);
        }

        subscriptionService.findActiveBySymbol(payload.getSymbol())
                .forEach(rateNotificationService::checkAndNotify);
    }
}
