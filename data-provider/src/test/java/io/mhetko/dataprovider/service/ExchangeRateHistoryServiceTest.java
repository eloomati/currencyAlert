package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import io.mhetko.dataprovider.repository.ExchangeRateHistoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExchangeRateHistoryServiceTest {

    private final ExchangeRateHistoryRepository repository = mock(ExchangeRateHistoryRepository.class);
    private final ExchangeRateHistoryService service = new ExchangeRateHistoryService(repository);

    @Test
    void shouldSaveHistoryEntity() {
        // given
        String base = "USD";
        String symbol = "PLN";
        Double rate = 4.12345678;
        OffsetDateTime asOf = OffsetDateTime.now();

        // when
        service.saveHistory(base, symbol, rate, asOf);

        // then
        ArgumentCaptor<ExchangeRateHistoryEntity> captor = ArgumentCaptor.forClass(ExchangeRateHistoryEntity.class);
        verify(repository, times(1)).save(captor.capture());

        ExchangeRateHistoryEntity entity = captor.getValue();
        assertThat(entity.getBase()).isEqualTo(base);
        assertThat(entity.getSymbol()).isEqualTo(symbol);
        assertThat(entity.getRate()).isEqualTo(rate);
        assertThat(entity.getAsOf()).isEqualTo(asOf);
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getIngestedAt()).isNotNull();
    }
}