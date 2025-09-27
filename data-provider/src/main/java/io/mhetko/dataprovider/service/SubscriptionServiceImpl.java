package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.dto.SubscriptionDto;
import io.mhetko.dataprovider.mapper.SubscriptionMapper;
import io.mhetko.dataprovider.model.AppUser;
import io.mhetko.dataprovider.model.Subscription;
import io.mhetko.dataprovider.repository.AppUserRepository;
import io.mhetko.dataprovider.repository.SubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AppUserRepository appUserRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    @Override
    public SubscriptionDto addSubscription(String username, String symbol) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        String normalized = normalizeSymbol(symbol);

        if (subscriptionRepository.existsByUserIdAndSymbol(user.getId(), normalized)) {
            return subscriptionRepository.findByUserIdAndSymbol(user.getId(), normalized)
                    .map(subscriptionMapper::toDto)
                    .orElseThrow();
        }

        Subscription s = new Subscription();
        s.setUser(user);
        s.setSymbol(normalized);
        s.setActive(true);

        return subscriptionMapper.toDto(subscriptionRepository.save(s));
    }

    @Transactional
    @Override
    public void removeSubscription(UUID subscriptionId) {
        Subscription s = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + subscriptionId));
        if (s.isActive()) {
            s.setActive(false);
            subscriptionRepository.save(s);
        }
    }

    @Transactional
    @Override
    public SubscriptionDto updateSubscription(UUID subscriptionId, boolean isActive) {
        Subscription s = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + subscriptionId));

        if (s.isActive() == isActive) {
            return subscriptionMapper.toDto(s);
        }
        s.setActive(isActive);
        return subscriptionMapper.toDto(subscriptionRepository.save(s));
    }

    @Transactional(readOnly = true)
    @Override
    public List<SubscriptionDto> getUserSubscriptions(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        return subscriptionRepository.findByUserId(user.getId())
                .stream()
                .map(subscriptionMapper::toDto)
                .toList();
    }

    private static String normalizeSymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol must not be blank");
        }
        return symbol.trim().toUpperCase();
    }
}