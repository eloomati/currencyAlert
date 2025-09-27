package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import io.mhetko.dataprovider.service.ExchangeRateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final ExchangeRateHistoryService historyService;

    @GetMapping("/{base}")
    public List<ExchangeRateHistoryEntity> getHistory(@PathVariable String base) {
        return historyService.getHistoryByBase(base);
    }
}
