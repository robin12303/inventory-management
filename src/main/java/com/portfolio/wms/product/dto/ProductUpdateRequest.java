package com.portfolio.wms.product.dto;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        String name,
        BigDecimal price
) {
}