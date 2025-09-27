package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import io.mhetko.dataprovider.repository.ExchangeRateHistoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @Test
    void shouldGroupHistoryBySymbol() {
        // given
        String base = "USD";
        ExchangeRateHistoryEntity e1 = ExchangeRateHistoryEntity.builder()
                .base(base).symbol("PLN").rate(4.1).asOf(OffsetDateTime.now()).build();
        ExchangeRateHistoryEntity e2 = ExchangeRateHistoryEntity.builder()
                .base(base).symbol("EUR").rate(0.92).asOf(OffsetDateTime.now()).build();
        ExchangeRateHistoryEntity e3 = ExchangeRateHistoryEntity.builder()
                .base(base).symbol("PLN").rate(4.2).asOf(OffsetDateTime.now().minusDays(1)).build();

        when(repository.findByBaseOrderByAsOfDesc(base)).thenReturn(Arrays.asList(e1, e2, e3));

        // when
        Map<String, List<ExchangeRateHistoryEntity>> result = service.getHistoryByBaseGrouped(base);

        // then
        assertThat(result).containsKeys("PLN", "EUR");
        assertThat(result.get("PLN")).containsExactly(e1, e3);
        assertThat(result.get("EUR")).containsExactly(e2);
    }

    @Test
    void shouldReturnLatestRatesByBase() {
        // given
        String base = "USD";
        ExchangeRateHistoryEntity e1 = ExchangeRateHistoryEntity.builder()
                .base(base).symbol("PLN").rate(4.1).asOf(OffsetDateTime.now()).build();
        ExchangeRateHistoryEntity e2 = ExchangeRateHistoryEntity.builder()
                .base(base).symbol("EUR").rate(0.92).asOf(OffsetDateTime.now()).build();

        when(repository.findLatestRatesByBase(base)).thenReturn(Arrays.asList(e1, e2));

        // when
        List<ExchangeRateHistoryEntity> result = service.getLatestRatesByBase(base);

        // then
        assertThat(result).containsExactly(e1, e2);
    }
}