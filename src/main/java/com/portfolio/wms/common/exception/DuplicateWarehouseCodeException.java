package com.portfolio.wms.common.exception;

public class DuplicateWarehouseCodeException extends RuntimeException {

    public DuplicateWarehouseCodeException(String code) {
        super("이미 존재하는 창고 코드입니다. code=" + code);
    }
}