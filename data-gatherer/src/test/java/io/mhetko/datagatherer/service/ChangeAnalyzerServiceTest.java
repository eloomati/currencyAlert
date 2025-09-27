package io.mhetko.datagatherer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeAnalyzerServiceTest {

    @Mock
    private RateCacheService rateCacheService;

    private ChangeAnalyzerService service;

    private ChangeAnalyzerService sut() {
        if (service == null) {
            service = new ChangeAnalyzerService(rateCacheService);
        }
        return service;
    }

    @Test
    void lessThanTwoRecords_returnsFalse() {
        when(rateCacheService.getLastRates("EUR", "PLN"))
                .thenReturn(List.of(4.20));

        boolean changed = sut().hasRateChanged("EUR", "PLN", 0.05);

        assertThat(changed).isFalse();
        verify(rateCacheService).getLastRates("EUR", "PLN");
    }

    @Test
    void changeGreaterThanThreshold_returnsTrue() {
        when(rateCacheService.getLastRates("EUR", "PLN"))
                .thenReturn(List.of(4.50, 4.40));

        boolean changed = sut().hasRateChanged("EUR", "PLN", 0.05);

        assertThat(changed).isTrue(); // 0.10 > 0.05
    }

    @Test
    void changeEqualToThreshold_returnsFalse() {
        when(rateCacheService.getLastRates("EUR", "PLN"))
                .thenReturn(List.of(4.50, 4.45));

        boolean changed = sut().hasRateChanged("EUR", "PLN", 0.05);

        assertThat(changed).isFalse(); // 0.05 == threshold
    }

    @Test
    void downwardChange_greaterThanThreshold_returnsTrue() {
        when(rateCacheService.getLastRates("EUR", "PLN"))
                .thenReturn(List.of(4.30, 4.50));

        boolean changed = sut().hasRateChanged("EUR", "PLN", 0.15);

        assertThat(changed).isTrue(); // |4.30 - 4.50| = 0.20 > 0.15
    }
}