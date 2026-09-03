package com.portfolio.wms.inventory.domain;

import com.portfolio.wms.product.domain.Product;
import com.portfolio.wms.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"warehouse_id", "product_id"}
                )
        }
)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long quantity;

    public Inventory(
            Warehouse warehouse,
            Product product,
            Long quantity
    ) {
        this.warehouse = warehouse;
        this.product = product;
        this.quantity = quantity;
    }

    public void increaseQuantity(Long quantity) {
        this.quantity += quantity;
    }

    public void decreaseQuantity(Long quantity) {
        this.quantity -= quantity;
    }
}