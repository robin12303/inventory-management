package com.portfolio.wms.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockRequest(

        @NotNull
        Long warehouseId,

        @NotNull
        Long productId,

        @NotNull
        @Positive(message = "수량은 0보다 커야 합니다.")
        Long quantity
) {
}