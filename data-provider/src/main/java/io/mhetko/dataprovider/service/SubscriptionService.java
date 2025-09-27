package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.dto.SubscriptionDto;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    SubscriptionDto addSubscription(String username, String symbol);
    void removeSubscription(UUID subscriptionId);
    SubscriptionDto updateSubscription(UUID subscriptionId, boolean isActive);
    List<SubscriptionDto> getUserSubscriptions(String username);
}
