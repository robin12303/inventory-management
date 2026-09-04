package com.portfolio.wms.common.exception;

public class WarehouseInUseException extends RuntimeException {

    public WarehouseInUseException(Long warehouseId) {
        super("재고가 존재하는 창고는 삭제할 수 없습니다.");
    }
}