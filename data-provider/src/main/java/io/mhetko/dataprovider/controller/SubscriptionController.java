package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.dto.SubscriptionDto;
import io.mhetko.dataprovider.model.SubscriptionDeleteResponse;
import io.mhetko.dataprovider.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(
            summary = "Dodaj subskrypcję kursu walut",
            description = "Dodaje nową subskrypcję dla zalogowanego użytkownika na wybrany symbol waluty i próg.",
            tags = {"Subscription"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pomyślnie dodano subskrypcję",
                    content = @Content(schema = @Schema(implementation = SubscriptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe dane wejściowe"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Brak autoryzacji"
            )
    })
    public ResponseEntity<SubscriptionDto> addSubscription(
            @RequestParam String symbol,
            @RequestParam(required = false) BigDecimal threshold,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        String username = principal.getUsername();
        SubscriptionDto dto = subscriptionService.addSubscription(username, symbol, threshold);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{subscriptionId}")
    @Operation(
            summary = "Usuń subskrypcję",
            description = "Usuwa subskrypcję o podanym identyfikatorze.",
            tags = {"Subscription"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pomyślnie usunięto subskrypcję",
                    content = @Content(schema = @Schema(implementation = SubscriptionDeleteResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nie znaleziono subskrypcji"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Brak autoryzacji"
            )
    })
    public ResponseEntity<SubscriptionDeleteResponse> removeSubscription(@PathVariable UUID subscriptionId) {
        subscriptionService.removeSubscription(subscriptionId);
        return ResponseEntity.ok(new SubscriptionDeleteResponse(
                subscriptionId,
                "Subscription has been successfully deleted."
        ));
    }

    @PutMapping("/{subscriptionId}")
    @Operation(
            summary = "Aktualizuj subskrypcję",
            description = "Aktualizuje status aktywności i próg subskrypcji o podanym identyfikatorze.",
            tags = {"Subscription"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pomyślnie zaktualizowano subskrypcję",
                    content = @Content(schema = @Schema(implementation = SubscriptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nie znaleziono subskrypcji"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe dane wejściowe"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Brak autoryzacji"
            )
    })
    public ResponseEntity<SubscriptionDto> updateSubscription(
            @PathVariable UUID subscriptionId,
            @RequestParam boolean active,
            @RequestParam(required = false) BigDecimal threshold) {
        SubscriptionDto dto = subscriptionService.updateSubscription(subscriptionId, active, threshold);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Pobierz subskrypcje użytkownika",
            description = "Zwraca listę subskrypcji zalogowanego użytkownika.",
            tags = {"Subscription"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pomyślnie pobrano subskrypcje",
                    content = @Content(schema = @Schema(implementation = SubscriptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Brak autoryzacji"
            )
    })
    public ResponseEntity<List<SubscriptionDto>> getUserSubscriptions(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        String username = principal.getUsername();
        List<SubscriptionDto> dtos = subscriptionService.getUserSubscriptions(username);
        return ResponseEntity.ok(dtos);
    }
}