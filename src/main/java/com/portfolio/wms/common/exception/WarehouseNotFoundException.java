package com.portfolio.wms.common.exception;

public class WarehouseNotFoundException extends RuntimeException {

    public WarehouseNotFoundException(Long id) {
        super("창고를 찾을 수 없습니다. id=" + id);
    }
}