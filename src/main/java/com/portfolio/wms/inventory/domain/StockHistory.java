package com.portfolio.wms.inventory.domain;

import com.portfolio.wms.product.domain.Product;
import com.portfolio.wms.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class StockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private StockMovementType movementType;

    @Column(nullable = false)
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_warehouse_id")
    private Warehouse relatedWarehouse;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public StockHistory(
            Warehouse warehouse,
            Product product,
            StockMovementType movementType,
            Long quantity,
            Warehouse relatedWarehouse
    ) {
        this.warehouse = warehouse;
        this.product = product;
        this.movementType = movementType;
        this.quantity = quantity;
        this.relatedWarehouse = relatedWarehouse;
        this.createdAt = LocalDateTime.now();
    }
}