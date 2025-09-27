package io.mhetko.dataprovider.model;

import java.util.UUID;

public record SubscriptionDeleteResponse(UUID subscriptionId, String message) {}