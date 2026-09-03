package com.portfolio.wms.inventory.service;

import com.portfolio.wms.inventory.domain.StockHistory;
import com.portfolio.wms.inventory.dto.StockHistoryResponse;
import com.portfolio.wms.inventory.repository.StockHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockHistoryService {

    private final StockHistoryRepository stockHistoryRepository;

    @Transactional(readOnly = true)
    public Page<StockHistoryResponse> getStockHistories(
            Long warehouseId,
            Long productId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<StockHistory> histories;

        if (warehouseId != null && productId != null) {
            histories =
                    stockHistoryRepository
                            .findByWarehouse_IdAndProduct_IdOrderByCreatedAtDesc(
                                    warehouseId,
                                    productId,
                                    pageable
                            );

        } else if (warehouseId != null) {
            histories =
                    stockHistoryRepository
                            .findByWarehouse_IdOrderByCreatedAtDesc(
                                    warehouseId,
                                    pageable
                            );

        } else if (productId != null) {
            histories =
                    stockHistoryRepository
                            .findByProduct_IdOrderByCreatedAtDesc(
                                    productId,
                                    pageable
                            );

        } else {
            histories =
                    stockHistoryRepository
                            .findAllByOrderByCreatedAtDesc(pageable);
        }

        return histories.map(this::toResponse);
    }

    private StockHistoryResponse toResponse(
            StockHistory history
    ) {
        Long relatedWarehouseId = null;
        String relatedWarehouseName = null;

        if (history.getRelatedWarehouse() != null) {
            relatedWarehouseId =
                    history.getRelatedWarehouse().getId();

            relatedWarehouseName =
                    history.getRelatedWarehouse().getName();
        }

        return new StockHistoryResponse(
                history.getId(),

                history.getWarehouse().getId(),
                history.getWarehouse().getName(),

                history.getProduct().getId(),
                history.getProduct().getName(),

                history.getMovementType(),
                history.getQuantity(),

                relatedWarehouseId,
                relatedWarehouseName,

                history.getCreatedAt()
        );
    }
}