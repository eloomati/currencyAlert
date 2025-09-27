package io.mhetko.dataprovider.consumer;

import io.mhetko.dataprovider.consumer.model.RateChangedPayload;
import io.mhetko.dataprovider.model.Subscription;
import io.mhetko.dataprovider.service.ExchangeRateHistoryService;
import io.mhetko.dataprovider.service.RateNotificationService;
import io.mhetko.dataprovider.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateChangedConsumerTest {

    @Test
    void receiveMessage_callsSaveHistoryAndNotifiesActiveSubscriptions() {
        // given
        ExchangeRateHistoryService historyService = mock(ExchangeRateHistoryService.class);
        RateNotificationService notificationService = mock(RateNotificationService.class);
        SubscriptionService subscriptionService = mock(SubscriptionService.class);

        RateChangedConsumer consumer = new RateChangedConsumer(
                historyService, notificationService, subscriptionService
        );

        RateChangedPayload payload = new RateChangedPayload();
        payload.setBase("EUR");
        payload.setSymbol("PLN");
        payload.setRate(4.25);
        OffsetDateTime asOf = OffsetDateTime.parse("2024-06-01T12:00:00Z");
        payload.setAsOf(asOf);

        Subscription sub1 = mock(Subscription.class);
        Subscription sub2 = mock(Subscription.class);

        when(subscriptionService.findActiveBySymbol("PLN")).thenReturn(List.of(sub1, sub2));

        // when
        consumer.receiveMessage(payload);

        // then
        verify(historyService).saveHistory("EUR", "PLN", 4.25, asOf);
        verify(subscriptionService).findActiveBySymbol("PLN");
        verify(notificationService).checkAndNotify(sub1);
        verify(notificationService).checkAndNotify(sub2);
    }
}