package io.mhetko.datagatherer.service;

import io.mhetko.datagatherer.model.CurrencyEntity;
import io.mhetko.datagatherer.model.RateEntity;
import io.mhetko.datagatherer.provider.OpenExchangeRatesClient;
import io.mhetko.datagatherer.repository.CurrencyRepository;
import io.mhetko.datagatherer.repository.RateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentUpdateServiceTest {

    @Mock private RateRepository rateRepository;
    @Mock private RateCacheService rateCacheService;
    @Mock private CurrencyRepository currencyRepository;
    @Mock private OpenExchangeRatesClient openExchangeRatesClient;

    @Captor private ArgumentCaptor<RateEntity> rateCaptor;

    private CurrentUpdateService service;

    @BeforeEach
    void setUp() {
        service = new CurrentUpdateService(
                rateRepository,
                rateCacheService,
                currencyRepository,
                openExchangeRatesClient
        );
    }

    private CurrencyEntity currency(String code) {
        CurrencyEntity c = new CurrencyEntity();
        c.setId(1L);
        c.setCode(code);
        return c;
    }

    private RateEntity existingRate(CurrencyEntity base, CurrencyEntity target,
                                    double rate, OffsetDateTime asOf) {
        return RateEntity.builder()
                .id(42L)
                .base(base)
                .target(target)
                .rate(rate)
                .asOf(asOf)
                .updatedAt(asOf)
                .build();
    }

    @Test
    void saveNewRate_createsEntityAndCache() {
        String base = "EUR", symbol = "PLN";
        double rate = 4.3210;
        OffsetDateTime asOf = OffsetDateTime.parse("2024-01-01T12:00:00Z");

        when(currencyRepository.findByCode(base)).thenReturn(Optional.of(currency(base)));
        when(currencyRepository.findByCode(symbol)).thenReturn(Optional.of(currency(symbol)));
        when(rateRepository.findByBaseAndTarget(any(), any())).thenReturn(Optional.empty());
        when(openExchangeRatesClient.latest(eq(base), any())).thenReturn(Map.of(symbol, rate));

        service.fetchAndSaveRates(base, List.of(symbol), asOf);

        verify(rateCacheService).saveRate(base, symbol, rate);
        verify(rateRepository).save(rateCaptor.capture());
        RateEntity saved = rateCaptor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getBase().getCode()).isEqualTo(base);
        assertThat(saved.getTarget().getCode()).isEqualTo(symbol);
        assertThat(saved.getRate()).isEqualTo(rate);
        assertThat(saved.getAsOf()).isEqualTo(asOf);
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void saveExistingRate_updatesEntityAndCache() {
        String base = "EUR", symbol = "USD";
        OffsetDateTime oldAsOf = OffsetDateTime.parse("2024-01-01T00:00:00Z");
        OffsetDateTime newAsOf = OffsetDateTime.parse("2024-02-01T00:00:00Z");

        CurrencyEntity baseC = currency(base);
        CurrencyEntity symC  = currency(symbol);
        RateEntity existing = existingRate(baseC, symC, 1.05, oldAsOf);

        when(currencyRepository.findByCode(base)).thenReturn(Optional.of(baseC));
        when(currencyRepository.findByCode(symbol)).thenReturn(Optional.of(symC));
        when(rateRepository.findByBaseAndTarget(baseC, symC)).thenReturn(Optional.of(existing));
        double newRate = 1.10;
        when(openExchangeRatesClient.latest(eq(base), any())).thenReturn(Map.of(symbol, newRate));

        service.fetchAndSaveRates(base, List.of(symbol), newAsOf);

        verify(rateCacheService).saveRate(base, symbol, newRate);
        verify(rateRepository).save(rateCaptor.capture());
        RateEntity saved = rateCaptor.getValue();
        assertThat(saved.getId()).isEqualTo(existing.getId());
        assertThat(saved.getBase()).isSameAs(baseC);
        assertThat(saved.getTarget()).isSameAs(symC);
        assertThat(saved.getRate()).isEqualTo(newRate);
        assertThat(saved.getAsOf()).isEqualTo(newAsOf);
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isAfterOrEqualTo(newAsOf.minusSeconds(1));
    }

    @Test
    void missingCurrency_throws() {
        String base = "EUR", symbol = "PLN";
        when(currencyRepository.findByCode(base)).thenReturn(Optional.empty());
        when(openExchangeRatesClient.latest(eq(base), any())).thenReturn(Map.of(symbol, 4.2));

        assertThatThrownBy(this::callFetchAndSaveRatesWithNow)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Base currency not found:");

        verify(rateRepository, never()).save(any());
        verify(rateCacheService, never()).saveRate(any(), any(), anyDouble());
    }

    private void callFetchAndSaveRatesWithNow() {
        service.fetchAndSaveRates("EUR", List.of("PLN"), OffsetDateTime.now());
    }

    @Test
    void fetchAndSaveRates_savesAllReturnedSymbols() {
        String base = "EUR";
        var asOf = OffsetDateTime.parse("2024-03-01T00:00:00Z");

        when(openExchangeRatesClient.latest(eq(base), any()))
                .thenReturn(Map.of("PLN", 4.30, "USD", 1.10));

        var eur = currency("EUR");
        var pln = currency("PLN");
        var usd = currency("USD");

        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.of(eur));
        when(currencyRepository.findByCode("PLN")).thenReturn(Optional.of(pln));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usd));
        when(rateRepository.findByBaseAndTarget(any(), any())).thenReturn(Optional.empty());

        service.fetchAndSaveRates(base, List.of("PLN", "USD"), asOf);

        verify(rateRepository, times(2)).save(any(RateEntity.class));
        verify(rateCacheService).saveRate(base, "PLN", 4.30);
        verify(rateCacheService).saveRate(base, "USD", 1.10);
    }

    @Test
    void getLatestRate_returnsRateIfExists() {
        String base = "EUR", symbol = "PLN";
        double rate = 4.25;
        when(rateCacheService.getLastRates(base, symbol)).thenReturn(List.of(rate));

        Double result = service.getLatestRate(base, symbol);

        assertThat(result).isEqualTo(rate);
    }

    @Test
    void getLatestRate_returnsNullIfNoData() {
        String base = "EUR", symbol = "PLN";
        when(rateCacheService.getLastRates(base, symbol)).thenReturn(List.of());

        Double result = service.getLatestRate(base, symbol);

        assertThat(result).isNull();
    }
}