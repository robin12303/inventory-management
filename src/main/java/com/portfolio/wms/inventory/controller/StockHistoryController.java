package com.portfolio.wms.inventory.controller;

import com.portfolio.wms.inventory.dto.StockHistoryResponse;
import com.portfolio.wms.inventory.service.StockHistoryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock-histories")
public class StockHistoryController {

    private final StockHistoryService stockHistoryService;

    public StockHistoryController(
            StockHistoryService stockHistoryService
    ) {
        this.stockHistoryService = stockHistoryService;
    }

    @GetMapping
    public Page<StockHistoryResponse> getStockHistories(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long productId,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "page는 0 이상이어야 합니다.")
            int page,

            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 최대 100까지 가능합니다.")
            int size
    ) {
        return stockHistoryService.getStockHistories(
                warehouseId,
                productId,
                page,
                size
        );
    }
}