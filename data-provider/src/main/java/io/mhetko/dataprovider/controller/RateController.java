package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import io.mhetko.dataprovider.service.ExchangeRateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/api/rate")
@RequiredArgsConstructor
public class RateController {

    private final ExchangeRateHistoryService historyService;

    @GetMapping("/{base}")
    @Operation(
            summary = "Pobierz najnowsze kursy walut",
            description = "Zwraca najnowsze kursy walut dla podanej waluty bazowej.",
            tags = {"ExchangeRate"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pomyślnie pobrano najnowsze kursy",
                    content = @Content(schema = @Schema(implementation = ExchangeRateHistoryEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nie znaleziono kursów dla podanej waluty bazowej"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe dane wejściowe"
            )
    })
    public List<ExchangeRateHistoryEntity> getLatestRates(@PathVariable String base) {
        return historyService.getLatestRatesByBase(base);
    }
}