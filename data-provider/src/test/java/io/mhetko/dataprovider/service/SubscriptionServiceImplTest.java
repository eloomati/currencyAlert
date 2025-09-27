package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.dto.SubscriptionDto;
import io.mhetko.dataprovider.mapper.SubscriptionMapper;
import io.mhetko.dataprovider.model.AppUser;
import io.mhetko.dataprovider.model.Subscription;
import io.mhetko.dataprovider.repository.AppUserRepository;
import io.mhetko.dataprovider.repository.SubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceImplTest {

    private SubscriptionRepository subscriptionRepository;
    private AppUserRepository appUserRepository;
    private SubscriptionMapper subscriptionMapper;
    private SubscriptionServiceImpl service;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        appUserRepository = mock(AppUserRepository.class);
        subscriptionMapper = mock(SubscriptionMapper.class);
        service = new SubscriptionServiceImpl(subscriptionRepository, appUserRepository, subscriptionMapper);
    }

    @Test
    void shouldAddSubscriptionForUser() {
        String username = "testuser";
        String symbol = "btc";
        BigDecimal threshold = BigDecimal.valueOf(123.45);
        AppUser user = new AppUser();
        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setSymbol("BTC");
        sub.setActive(true);
        sub.setThreshold(threshold);
        SubscriptionDto dto = new SubscriptionDto();

        when(appUserRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(subscriptionRepository.existsByUserIdAndSymbol(user.getId(), "BTC")).thenReturn(false);
        when(subscriptionRepository.save(any())).thenReturn(sub);
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        SubscriptionDto result = service.addSubscription(username, symbol, threshold);

        assertThat(result).isSameAs(dto);
        verify(subscriptionRepository).save(any());
    }

    @Test
    void shouldReturnExistingSubscriptionIfExists() {
        String username = "testuser";
        String symbol = "eth";
        BigDecimal threshold = BigDecimal.valueOf(99.99);
        Subscription sub = new Subscription();
        SubscriptionDto dto = new SubscriptionDto();
        AppUser user = new AppUser();

        when(appUserRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(subscriptionRepository.existsByUserIdAndSymbol(user.getId(), "ETH")).thenReturn(true);
        when(subscriptionRepository.findByUserIdAndSymbol(user.getId(), "ETH")).thenReturn(Optional.of(sub));
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        SubscriptionDto result = service.addSubscription(username, symbol, threshold);

        assertThat(result).isSameAs(dto);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        String username = "testuser";
        BigDecimal threshold = BigDecimal.ZERO;
        when(appUserRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addSubscription(username, "btc", threshold))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldSoftRemoveSubscriptionIfActive() {
        UUID subId = UUID.randomUUID();
        Subscription sub = new Subscription();
        sub.setActive(true);

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(sub));

        service.removeSubscription(subId);

        assertThat(sub.isActive()).isFalse();
        verify(subscriptionRepository).save(sub);
    }

    @Test
    void shouldNotRemoveSubscriptionIfAlreadyInactive() {
        UUID subId = UUID.randomUUID();
        Subscription sub = new Subscription();
        sub.setActive(false);

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(sub));

        service.removeSubscription(subId);

        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfSubscriptionNotFoundOnRemove() {
        UUID subId = UUID.randomUUID();
        when(subscriptionRepository.findById(subId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeSubscription(subId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldReturnUserSubscriptionsByUsername() {
        String username = "testuser";
        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        Subscription sub = new Subscription();
        SubscriptionDto dto = new SubscriptionDto();

        when(appUserRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUserId(user.getId())).thenReturn(List.of(sub));
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        List<SubscriptionDto> result = service.getUserSubscriptions(username);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void shouldThrowIfUserNotFoundOnGetSubscriptions() {
        String username = "testuser";
        when(appUserRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserSubscriptions(username))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldFindActiveSubscriptionsBySymbol() {
        String symbol = "btc";
        String normalized = "BTC";
        Subscription sub1 = new Subscription();
        Subscription sub2 = new Subscription();

        when(subscriptionRepository.findBySymbolAndActiveTrue(normalized)).thenReturn(List.of(sub1, sub2));

        List<Subscription> result = service.findActiveBySymbol(symbol);

        assertThat(result).containsExactly(sub1, sub2);
        verify(subscriptionRepository).findBySymbolAndActiveTrue(normalized);
    }
}