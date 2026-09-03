package com.portfolio.wms.warehouse.repository;

import com.portfolio.wms.warehouse.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository
        extends JpaRepository<Warehouse, Long> {
    boolean existsByCode(String code);
}