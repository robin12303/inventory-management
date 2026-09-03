package com.portfolio.wms.warehouse.dto;

public record WarehouseResponse(
        Long id,
        String name,
        String code,
        String address
) {
}