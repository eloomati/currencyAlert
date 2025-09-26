package io.mhetko.datagatherer.scheduler;

import io.mhetko.datagatherer.service.CurrentUpdateService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = {
                "dg.fetch-cron=0/1 * * ? * *",
                "dg.base=EUR",
                "dg.main-currencies=PLN,USD"
        }
)
class RateUpdateCronTest {

    @Autowired
    private RateUpdateScheduler rateUpdateScheduler;

    @Autowired
    private CurrentUpdateService currentUpdateService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CurrentUpdateService currentUpdateService() {
            return Mockito.mock(CurrentUpdateService.class);
        }
    }

    @Test
    @DisplayName("Scheduler wywołuje fetchAndSaveRates z poprawnymi argumentami")
    void cron_invokes_updateRates_and_calls_service() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                        verify(currentUpdateService, atLeastOnce())
                                .fetchAndSaveRates(eq("EUR"), eq(List.of("PLN", "USD")), any())
                );

        ArgumentCaptor<OffsetDateTime> asOfCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);
        verify(currentUpdateService, atLeastOnce())
                .fetchAndSaveRates(eq("EUR"), eq(List.of("PLN", "USD")), asOfCaptor.capture());

        OffsetDateTime firstAsOf = asOfCaptor.getAllValues().get(0);
        OffsetDateTime now = OffsetDateTime.now();
        assert !firstAsOf.isBefore(now.minusSeconds(3)) && !firstAsOf.isAfter(now.plusSeconds(1)) :
                "asOf powinno być blisko 'now()'";
    }
}