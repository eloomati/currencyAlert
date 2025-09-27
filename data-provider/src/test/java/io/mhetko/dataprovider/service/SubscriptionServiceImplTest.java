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
        UUID userId = UUID.randomUUID();
        String symbol = "btc";
        AppUser user = new AppUser();
        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setSymbol("BTC");
        sub.setActive(true);
        SubscriptionDto dto = new SubscriptionDto();

        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.existsByUserIdAndSymbol(userId, "BTC")).thenReturn(false);
        when(subscriptionRepository.save(any())).thenReturn(sub);
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        SubscriptionDto result = service.addSubscription(userId, symbol);

        assertThat(result).isSameAs(dto);
        verify(subscriptionRepository).save(any());
    }

    @Test
    void shouldReturnExistingSubscriptionIfExists() {
        UUID userId = UUID.randomUUID();
        String symbol = "eth";
        Subscription sub = new Subscription();
        SubscriptionDto dto = new SubscriptionDto();
        AppUser user = new AppUser();

        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.existsByUserIdAndSymbol(userId, "ETH")).thenReturn(true);
        when(subscriptionRepository.findByUserIdAndSymbol(userId, "ETH")).thenReturn(Optional.of(sub));
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        SubscriptionDto result = service.addSubscription(userId, symbol);

        assertThat(result).isSameAs(dto);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(appUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addSubscription(userId, "btc"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldRemoveSubscriptionIfExists() {
        UUID subId = UUID.randomUUID();
        when(subscriptionRepository.existsById(subId)).thenReturn(true);

        service.removeSubscription(subId);

        verify(subscriptionRepository).deleteById(subId);
    }

    @Test
    void shouldNotRemoveSubscriptionIfNotExists() {
        UUID subId = UUID.randomUUID();
        when(subscriptionRepository.existsById(subId)).thenReturn(false);

        service.removeSubscription(subId);

        verify(subscriptionRepository, never()).deleteById(any());
    }

    @Test
    void shouldUpdateSubscriptionActiveStatus() {
        UUID subId = UUID.randomUUID();
        Subscription sub = new Subscription();
        sub.setActive(false);
        SubscriptionDto dto = new SubscriptionDto();

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(sub));
        when(subscriptionRepository.save(sub)).thenReturn(sub);
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        SubscriptionDto result = service.updateSubscription(subId, true);

        assertThat(sub.isActive()).isTrue();
        assertThat(result).isSameAs(dto);
    }

    @Test
    void shouldReturnSubscriptionUnchangedIfStatusSame() {
        UUID subId = UUID.randomUUID();
        Subscription sub = new Subscription();
        sub.setActive(true);
        SubscriptionDto dto = new SubscriptionDto();

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(sub));
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        SubscriptionDto result = service.updateSubscription(subId, true);

        assertThat(result).isSameAs(dto);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfSubscriptionNotFoundOnUpdate() {
        UUID subId = UUID.randomUUID();
        when(subscriptionRepository.findById(subId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateSubscription(subId, true))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldReturnUserSubscriptions() {
        UUID userId = UUID.randomUUID();
        Subscription sub = new Subscription();
        SubscriptionDto dto = new SubscriptionDto();

        when(subscriptionRepository.findByUserId(userId)).thenReturn(List.of(sub));
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        List<SubscriptionDto> result = service.getUserSubscriptions(userId);

        assertThat(result).containsExactly(dto);
    }
}