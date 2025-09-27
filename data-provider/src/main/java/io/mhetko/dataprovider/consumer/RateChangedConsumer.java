package io.mhetko.dataprovider.consumer;

import io.mhetko.dataprovider.consumer.model.RateChangedPayload;
import io.mhetko.dataprovider.service.ExchangeRateHistoryService;
import io.mhetko.dataprovider.service.RateNotificationService;
import io.mhetko.dataprovider.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RateChangedConsumer {

    private final ExchangeRateHistoryService exchangeRateHistoryService;
    private final RateNotificationService rateNotificationService;
    private final SubscriptionService subscriptionService;

    @RabbitListener(queues = "${amqp.queue}")
    public void receiveMessage(RateChangedPayload payload) {
        exchangeRateHistoryService.saveHistory(
                payload.getBase(),
                payload.getSymbol(),
                payload.getRate(),
                payload.getAsOf()
        );

        subscriptionService.findActiveBySymbol(payload.getSymbol())
                .forEach(rateNotificationService::checkAndNotify);
    }
}
