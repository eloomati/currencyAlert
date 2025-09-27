package io.mhetko.dataprovider.consumer;

import io.mhetko.dataprovider.consumer.model.RateChangedPayload;
import io.mhetko.dataprovider.service.ExchangeRateHistoryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateChangedConsumerTest {

    @Test
    void receiveMessage_callsSaveHistoryWithPayloadData() {
        // given
        ExchangeRateHistoryService service = mock(ExchangeRateHistoryService.class);
        RateChangedConsumer consumer = new RateChangedConsumer(service);

        RateChangedPayload payload = new RateChangedPayload();
        payload.setBase("EUR");
        payload.setSymbol("PLN");
        payload.setRate(4.25);
        OffsetDateTime asOf = OffsetDateTime.parse("2024-06-01T12:00:00Z");
        payload.setAsOf(asOf);

        // when
        consumer.receiveMessage(payload);

        // then
        ArgumentCaptor<String> baseCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> symbolCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> rateCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<OffsetDateTime> asOfCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);

        verify(service).saveHistory(
                baseCaptor.capture(),
                symbolCaptor.capture(),
                rateCaptor.capture(),
                asOfCaptor.capture()
        );

        assertThat(baseCaptor.getValue()).isEqualTo("EUR");
        assertThat(symbolCaptor.getValue()).isEqualTo("PLN");
        assertThat(rateCaptor.getValue()).isEqualTo(4.25);
        assertThat(asOfCaptor.getValue()).isEqualTo(asOf);
    }
}
