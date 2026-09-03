package com.portfolio.wms.common.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long current, Long requested) {
        super(
                "재고가 부족합니다. 현재 재고="
                        + current
                        + ", 요청 수량="
                        + requested
        );
    }
}