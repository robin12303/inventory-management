package com.portfolio.wms.inventory.repository;

import com.portfolio.wms.inventory.domain.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository
        extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByWarehouseIdAndProductId(
            Long warehouseId,
            Long productId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       select i
       from Inventory i
       where i.warehouse.id = :warehouseId
         and i.product.id = :productId
       """)
    Optional<Inventory> findByWarehouseIdAndProductIdForUpdate(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query(
            value = """
                INSERT INTO inventory (
                    warehouse_id,
                    product_id,
                    quantity
                )
                VALUES (
                    :warehouseId,
                    :productId,
                    :quantity
                )
                ON DUPLICATE KEY UPDATE
                    quantity = quantity + :quantity
                """,
            nativeQuery = true
    )
    int upsertIncreaseQuantity(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId,
            @Param("quantity") Long quantity
    );

    @Modifying
    @Query(value = """
    INSERT INTO inventory (warehouse_id, product_id, quantity)
    VALUES (:warehouseId, :productId, 0)
    ON DUPLICATE KEY UPDATE
        quantity = quantity
    """, nativeQuery = true)
    int initializeInventoryIfAbsent(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId
    );
}