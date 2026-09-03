package com.portfolio.wms.common.exception;

public record ApiErrorResponse(
        String code,
        String message
) {
}