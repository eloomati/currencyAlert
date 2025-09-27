package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.dto.SubscriptionDto;
import io.mhetko.dataprovider.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        String username = principal.getUsername();
        SubscriptionDto dto = subscriptionService.addSubscription(username, symbol);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> removeSubscription(@PathVariable UUID subscriptionId) {
        subscriptionService.removeSubscription(subscriptionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionDto> updateSubscription(
            @PathVariable UUID subscriptionId,
            @RequestParam boolean active) {
        SubscriptionDto dto = subscriptionService.updateSubscription(subscriptionId, active);
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