// data-provider/src/main/java/io/mhetko/dataprovider/controller/HistoryController.java
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
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final ExchangeRateHistoryService historyService;

    @GetMapping("/{base}")
    @Operation(
            summary = "Pobierz historię kursów walut",
            description = "Zwraca historię kursów walut dla podanej waluty bazowej, pogrupowaną po symbolu.",
            tags = {"ExchangeRateHistory"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pomyślnie pobrano historię kursów",
                    content = @Content(schema = @Schema(implementation = ExchangeRateHistoryEntity.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Nie znaleziono historii dla podanej waluty bazowej"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nieprawidłowe dane wejściowe"
            )
    })
    public Map<String, List<ExchangeRateHistoryEntity>> getHistory(@PathVariable String base) {
        return historyService.getHistoryByBaseGrouped(base);
    }
}