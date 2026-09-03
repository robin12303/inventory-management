package com.portfolio.wms.product.repository;

import com.portfolio.wms.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);
}