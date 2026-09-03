package com.portfolio.wms.inventory.dto;

import com.portfolio.wms.inventory.domain.StockMovementType;

import java.time.LocalDateTime;

public record StockHistoryResponse(
        Long id,

        Long warehouseId,
        String warehouseName,

        Long productId,
        String productName,

        StockMovementType movementType,
        Long quantity,

        Long relatedWarehouseId,
        String relatedWarehouseName,

        LocalDateTime createdAt
) {
}