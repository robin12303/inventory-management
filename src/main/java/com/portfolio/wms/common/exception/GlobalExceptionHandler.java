package com.portfolio.wms.common.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(
            ProductNotFoundException e
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        "PRODUCT_NOT_FOUND",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");

        return ResponseEntity
                .badRequest()
                .body(new ApiErrorResponse(
                        "INVALID_REQUEST",
                        message
                ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidation(
            HandlerMethodValidationException e
    ) {
        String message = e.getParameterValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");

        return ResponseEntity
                .badRequest()
                .body(new ApiErrorResponse(
                        "INVALID_REQUEST",
                        message
                ));
    }

    @ExceptionHandler(WarehouseNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleWarehouseNotFound(
            WarehouseNotFoundException e
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        "WAREHOUSE_NOT_FOUND",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientStock(
            InsufficientStockException e
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        "INSUFFICIENT_STOCK",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException e
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        "DATA_INTEGRITY_CONFLICT",
                        "데이터 무결성 제약조건에 위배되었습니다."
                ));
    }

    @ExceptionHandler(InvalidTransferException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTransfer(
            InvalidTransferException e
    ) {
        return ResponseEntity
                .badRequest()
                .body(new ApiErrorResponse(
                        "INVALID_TRANSFER",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(WarehouseInUseException.class)
    public ResponseEntity<ApiErrorResponse> handleWarehouseInUse(
            WarehouseInUseException e
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        "WAREHOUSE_IN_USE",
                        e.getMessage()
                ));
    }
}