package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.AppUser;
import io.mhetko.dataprovider.model.Subscription;
import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class RateNotificationServiceTest {

    private ExchangeRateHistoryService historyService;
    private EmailService emailService;
    private RateNotificationService rateNotificationService;

    @BeforeEach
    void setUp() {
        historyService = mock(ExchangeRateHistoryService.class);
        emailService = mock(EmailService.class);
        rateNotificationService = new RateNotificationService(historyService, emailService);
    }

    @Test
    void shouldSendNotificationWhenDiffIsGreaterOrEqualThreshold() {
        Subscription sub = new Subscription();
        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        sub.setUser(user);
        sub.setSymbol("BTC");
        sub.setThreshold(BigDecimal.valueOf(100.0));

        ExchangeRateHistoryEntity h1 = ExchangeRateHistoryEntity.builder()
                .rate(200.0)
                .asOf(OffsetDateTime.now())
                .build();
        ExchangeRateHistoryEntity h2 = ExchangeRateHistoryEntity.builder()
                .rate(100.0)
                .asOf(OffsetDateTime.now().minusHours(1))
                .build();

        when(historyService.getLastTwo("BTC")).thenReturn(List.of(h1, h2));

        rateNotificationService.checkAndNotify(sub);

        verify(emailService).sendNotification(
                eq("test@example.com"),
                contains("BTC"),
                contains("100.0")
        );
    }

    @Test
    void shouldNotSendNotificationWhenDiffIsLessThanThreshold() {
        Subscription sub = new Subscription();
        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        sub.setUser(user);
        sub.setSymbol("BTC");
        sub.setThreshold(BigDecimal.valueOf(100.0));

        ExchangeRateHistoryEntity h1 = ExchangeRateHistoryEntity.builder()
                .rate(150.0)
                .asOf(OffsetDateTime.now())
                .build();
        ExchangeRateHistoryEntity h2 = ExchangeRateHistoryEntity.builder()
                .rate(100.0)
                .asOf(OffsetDateTime.now().minusHours(1))
                .build();

        when(historyService.getLastTwo("BTC")).thenReturn(List.of(h1, h2));

        rateNotificationService.checkAndNotify(sub);

        verify(emailService, never()).sendNotification(anyString(), anyString(), anyString());
    }

    @Test
    void shouldNotSendNotificationWhenHistoryIsInsufficient() {
        Subscription sub = new Subscription();
        sub.setSymbol("BTC");
        sub.setThreshold(BigDecimal.valueOf(100.0));

        when(historyService.getLastTwo("BTC")).thenReturn(List.of());

        rateNotificationService.checkAndNotify(sub);

        verify(emailService, never()).sendNotification(anyString(), anyString(), anyString());
    }
}