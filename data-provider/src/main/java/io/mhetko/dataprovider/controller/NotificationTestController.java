package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.model.Subscription;
import io.mhetko.dataprovider.service.SubscriptionServiceImpl;
import io.mhetko.dataprovider.service.RateNotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/test/notification")
@RequiredArgsConstructor
public class NotificationTestController {

    private final RateNotificationService notificationService;
    private final SubscriptionServiceImpl subscriptionService;

    @PostMapping("/simulate")
    public String simulateConsumer(@RequestParam UUID subscriptionId, @RequestParam String symbol) {
        Subscription subscription = subscriptionService
                .findActiveBySymbol(symbol)
                .stream()
                .filter(s -> s.getId().equals(subscriptionId))
                .findFirst()
                .orElse(null);
        if (subscription == null) {
            return "Subscription not found";
        }
        notificationService.checkAndNotify(subscription);
        return "Notification checked for subscription " + subscriptionId;
    }
}
