package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import io.mhetko.dataprovider.service.ExchangeRateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final ExchangeRateHistoryService historyService;

    @GetMapping("/{base}")
    public Map<String, List<ExchangeRateHistoryEntity>> getHistory(@PathVariable String base) {
        return historyService.getHistoryByBaseGrouped(base);
    }
}