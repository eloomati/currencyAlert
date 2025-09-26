package io.mhetko.datagatherer.scheduler;

import io.mhetko.datagatherer.config.CurrencyProperties;
import io.mhetko.datagatherer.service.CurrentUpdateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateUpdateSchedulerTest {

    @Mock
    private CurrentUpdateService currentUpdateService;

    @Mock
    private CurrencyProperties currencyProperties;

    @Captor
    private ArgumentCaptor<String> baseCaptor;

    @Captor
    private ArgumentCaptor<Iterable<String>> symbolsCaptor;

    @Captor
    private ArgumentCaptor<OffsetDateTime> asOfCaptor;

    @Test
    void updateRates_callsServiceWithPropsAndNow() {
        // given
        String base = "EUR";
        List<String> symbols = List.of("PLN", "USD");

        when(currencyProperties.getBase()).thenReturn(base);
        when(currencyProperties.getMainCurrencies()).thenReturn(symbols);

        RateUpdateScheduler scheduler = new RateUpdateScheduler(currentUpdateService, currencyProperties);

        OffsetDateTime before = OffsetDateTime.now();

        // when
        scheduler.updateRates();

        OffsetDateTime after = OffsetDateTime.now();

        // then
        verify(currentUpdateService, times(1))
                .fetchAndSaveRates(baseCaptor.capture(), symbolsCaptor.capture(), asOfCaptor.capture());

        assertThat(baseCaptor.getValue()).isEqualTo(base);
        assertThat(symbolsCaptor.getValue()).containsExactlyElementsOf(symbols);

        OffsetDateTime asOf = asOfCaptor.getValue();
        assertThat(asOf).isAfterOrEqualTo(before);
        assertThat(asOf).isBeforeOrEqualTo(after);
    }

    @Test
    void updateRates_worksWithEmptySymbols() {
        // given
        when(currencyProperties.getBase()).thenReturn("EUR");
        when(currencyProperties.getMainCurrencies()).thenReturn(List.of());

        RateUpdateScheduler scheduler = new RateUpdateScheduler(currentUpdateService, currencyProperties);

        // when
        scheduler.updateRates();

        // then
        verify(currentUpdateService, times(1))
                .fetchAndSaveRates(eq("EUR"), eq(List.of()), any(OffsetDateTime.class));
    }
}