package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.dto.SubscriptionDto;
import io.mhetko.dataprovider.model.SubscriptionDeleteResponse;
import io.mhetko.dataprovider.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionDto> addSubscription(
            @RequestParam String symbol,
            @RequestParam(required = false) BigDecimal threshold,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        String username = principal.getUsername();
        SubscriptionDto dto = subscriptionService.addSubscription(username, symbol, threshold);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionDeleteResponse> removeSubscription(@PathVariable UUID subscriptionId) {
        subscriptionService.removeSubscription(subscriptionId);
        return ResponseEntity.ok(new SubscriptionDeleteResponse(
                subscriptionId,
                "Subscription has been successfully deleted."
        ));
    }

    @PutMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionDto> updateSubscription(
            @PathVariable UUID subscriptionId,
            @RequestParam boolean active,
            @RequestParam(required = false) BigDecimal threshold) {
        SubscriptionDto dto = subscriptionService.updateSubscription(subscriptionId, active, threshold);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<List<SubscriptionDto>> getUserSubscriptions(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        String username = principal.getUsername();
        List<SubscriptionDto> dtos = subscriptionService.getUserSubscriptions(username);
        return ResponseEntity.ok(dtos);
    }
}
