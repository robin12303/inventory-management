package com.portfolio.wms.inventory.repository;

import com.portfolio.wms.inventory.domain.StockHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHistoryRepository
        extends JpaRepository<StockHistory, Long> {

    @EntityGraph(attributePaths = {
            "warehouse",
            "product",
            "relatedWarehouse"
    })
    Page<StockHistory> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "warehouse",
            "product",
            "relatedWarehouse"
    })
    Page<StockHistory> findByWarehouse_IdOrderByCreatedAtDesc(
            Long warehouseId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "warehouse",
            "product",
            "relatedWarehouse"
    })
    Page<StockHistory> findByProduct_IdOrderByCreatedAtDesc(
            Long productId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "warehouse",
            "product",
            "relatedWarehouse"
    })
    Page<StockHistory> findByWarehouse_IdAndProduct_IdOrderByCreatedAtDesc(
            Long warehouseId,
            Long productId,
            Pageable pageable
    );
}