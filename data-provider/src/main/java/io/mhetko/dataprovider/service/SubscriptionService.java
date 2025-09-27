package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.dto.SubscriptionDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    SubscriptionDto addSubscription(String username, String symbol, BigDecimal threshold);
    void removeSubscription(UUID subscriptionId);
    SubscriptionDto updateSubscription(UUID subscriptionId, boolean isActive, BigDecimal threshold);
    List<SubscriptionDto> getUserSubscriptions(String username);
}
