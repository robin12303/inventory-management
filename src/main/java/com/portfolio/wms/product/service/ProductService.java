package com.portfolio.wms.product.service;

import com.portfolio.wms.common.exception.DuplicateProductSkuException;
import com.portfolio.wms.common.exception.ProductNotFoundException;
import com.portfolio.wms.product.domain.Product;
import com.portfolio.wms.product.dto.ProductCreateRequest;
import com.portfolio.wms.product.dto.ProductResponse;
import com.portfolio.wms.product.dto.ProductUpdateRequest;
import com.portfolio.wms.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse create(ProductCreateRequest request) {

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateProductSkuException(request.sku());
        }

        Product product = new Product(
                request.name(),
                request.sku(),
                request.price()
        );

        return toResponse(productRepository.save(product));
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice()
        );
    }

    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getSku(),
                        product.getPrice()
                ))
                .toList();
    }

    public ProductResponse updateProduct(
            Long id,
            ProductUpdateRequest request
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.update(
                request.name(),
                request.price()
        );

        Product savedProduct = productRepository.save(product);

        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getSku(),
                savedProduct.getPrice()
        );
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.delete(product);
    }
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice()
        );
    }
}