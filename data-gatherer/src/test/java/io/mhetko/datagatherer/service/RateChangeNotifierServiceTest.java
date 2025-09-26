package io.mhetko.datagatherer.service;

import io.mhetko.datagatherer.producer.RateChangedProducer;
import io.mhetko.datagatherer.model.RateChangedEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.AmqpException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateChangeNotifierServiceTest {

    @Mock
    private ChangeAnalyzerService changeAnalyzerService;

    @Mock
    private RateChangedProducer rateChangedProducer;

    @InjectMocks
    private RateChangeNotifierService notifier;

    @Captor
    private ArgumentCaptor<RateChangedEvent> eventCaptor;

    @Test
    void notify_whenChanged_sendsEvent() {
        // given
        String base = "EUR";
        String symbol = "PLN";
        double threshold = 0.05;
        double newRate = 4.23;

        when(changeAnalyzerService.hasRateChanged(base, symbol, threshold)).thenReturn(true);

        // when
        notifier.notifyIfRateChanged(base, symbol, threshold, newRate);

        // then
        verify(changeAnalyzerService).hasRateChanged(base, symbol, threshold);
        verify(rateChangedProducer).sendJson(eventCaptor.capture());

        RateChangedEvent sent = eventCaptor.getValue();
        assertThat(sent.getBase()).isEqualTo(base);
        assertThat(sent.getSymbol()).isEqualTo(symbol);
        assertThat(sent.getRate()).isEqualTo(newRate);
    }

    @Test
    void notify_whenNotChanged_doesNothing() {
        // given & when
        when(changeAnalyzerService.hasRateChanged("EUR", "USD", 0.10)).thenReturn(false);

        notifier.notifyIfRateChanged("EUR", "USD", 0.10, 1.10);
        // then
        verify(changeAnalyzerService).hasRateChanged("EUR", "USD", 0.10);
        verifyNoInteractions(rateChangedProducer);
    }

    @Test
    void notify_producerThrows_bubblesUp() {
        // given &  when
        when(changeAnalyzerService.hasRateChanged("EUR", "GBP", 0.01)).thenReturn(true);
        doThrow(new AmqpException("broker down"))
                .when(rateChangedProducer).sendJson(any());

        // then
        assertThatThrownBy(() -> notifier.notifyIfRateChanged("EUR", "GBP", 0.01, 0.86))
                .isInstanceOf(AmqpException.class)
                .hasMessageContaining("broker down");
    }

    @Test
    void notify_passesCorrectArgsToAnalyzer() {
        // given
        String base = "CHF";
        String symbol = "PLN";
        double threshold = 0.02;

        // when
        when(changeAnalyzerService.hasRateChanged(base, symbol, threshold)).thenReturn(false);

        notifier.notifyIfRateChanged(base, symbol, threshold, 4.67);

        // then
        verify(changeAnalyzerService).hasRateChanged(base, symbol, threshold);
        verifyNoInteractions(rateChangedProducer);
    }
}
