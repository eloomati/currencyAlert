package io.mhetko.dataprovider.controller;

import io.mhetko.dataprovider.model.ExchangeRateHistoryEntity;
import io.mhetko.dataprovider.service.ExchangeRateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rate")
@RequiredArgsConstructor
public class RateController {

    private final ExchangeRateHistoryService historyService;

    @GetMapping("/{base}")
    public List<ExchangeRateHistoryEntity> getLatestRates(@PathVariable String base) {
        return historyService.getLatestRatesByBase(base);
    }
}