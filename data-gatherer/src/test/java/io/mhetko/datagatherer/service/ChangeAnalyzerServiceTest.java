package io.mhetko.datagatherer.service;

import io.mhetko.datagatherer.model.ExchangeRateHistoryEntity;
import io.mhetko.datagatherer.repository.ExchangeRateHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeAnalyzerServiceTest {

    @Mock
    private ExchangeRateHistoryRepository historyRepository;

    private ChangeAnalyzerService service;

    private ExchangeRateHistoryEntity h(double rate, String asOfIso) {
        return ExchangeRateHistoryEntity.builder()
                .rate(rate)
                .asOf(OffsetDateTime.parse(asOfIso))
                .build();
    }

    private ChangeAnalyzerService sut() {
        if (service == null) {
            service = new ChangeAnalyzerService(historyRepository);
        }
        return service;
    }

    @Test
    void lessThanTwoRecords_returnsFalse() {
        when(historyRepository.findTop2ByBaseAndSymbolOrderByAsOfDesc("EUR", "PLN"))
                .thenReturn(List.of(h(4.20, "2024-01-01T12:00:00Z")));

        boolean changed = sut().hasRateChanged("EUR", "PLN", 0.05);

        assertThat(changed).isFalse();
        verify(historyRepository).findTop2ByBaseAndSymbolOrderByAsOfDesc("EUR", "PLN");
    }

    @Test
    void changeGreaterThanThreshold_returnsTrue() {
        when(historyRepository.findTop2ByBaseAndSymbolOrderByAsOfDesc("EUR", "PLN"))
                .thenReturn(List.of(
                        h(4.50, "2024-01-02T12:00:00Z"),
                        h(4.40, "2024-01-01T12:00:00Z")
                ));

        boolean changed = sut().hasRateChanged("EUR", "PLN", 0.05);

        assertThat(changed).isTrue(); // 0.10 > 0.05
    }

    @Test
    void changeEqualToThreshold_returnsFalse() {
        when(historyRepository.findTop2ByBaseAndSymbolOrderByAsOfDesc("EUR", "PLN"))
                .thenReturn(List.of(
                        h(4.50, "2024-01-02T12:00:00Z"),
                        h(4.45, "2024-01-01T12:00:00Z")
                ));

        boolean changed = sut().hasRateChanged("EUR", "PLN", 0.05);

        assertThat(changed).isFalse(); // 0.05 == threshold
    }

    @Test
    void downwardChange_greaterThanThreshold_returnsTrue() {
        when(historyRepository.findTop2ByBaseAndSymbolOrderByAsOfDesc("EUR", "PLN"))
                .thenReturn(List.of(
                        h(4.30, "2024-01-02T12:00:00Z"),
                        h(4.50, "2024-01-01T12:00:00Z")
                ));

        boolean changed = sut().hasRateChanged("EUR", "PLN", 0.15);

        assertThat(changed).isTrue(); // |4.30 - 4.50| = 0.20 > 0.15
    }
}
