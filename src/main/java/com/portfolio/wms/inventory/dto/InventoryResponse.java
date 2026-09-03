package com.portfolio.wms.inventory.dto;

public record InventoryResponse(
        Long inventoryId,
        Long warehouseId,
        String warehouseName,
        Long productId,
        String productName,
        Long quantity
) {
}