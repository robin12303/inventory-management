package com.portfolio.wms.common.exception;

public class DuplicateProductSkuException extends RuntimeException {

    public DuplicateProductSkuException(String sku) {
        super("이미 존재하는 SKU입니다. sku=" + sku);
    }
}