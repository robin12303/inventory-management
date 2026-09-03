package com.portfolio.wms.inventory.service;

import com.portfolio.wms.common.exception.InsufficientStockException;
import com.portfolio.wms.common.exception.InvalidTransferException;
import com.portfolio.wms.common.exception.ProductNotFoundException;
import com.portfolio.wms.common.exception.WarehouseNotFoundException;
import com.portfolio.wms.inventory.domain.Inventory;
import com.portfolio.wms.inventory.domain.StockHistory;
import com.portfolio.wms.inventory.domain.StockMovementType;
import com.portfolio.wms.inventory.dto.InventoryResponse;
import com.portfolio.wms.inventory.dto.InventoryTransferRequest;
import com.portfolio.wms.inventory.dto.StockRequest;
import com.portfolio.wms.inventory.repository.InventoryRepository;
import com.portfolio.wms.inventory.repository.StockHistoryRepository;
import com.portfolio.wms.product.domain.Product;
import com.portfolio.wms.product.repository.ProductRepository;
import com.portfolio.wms.warehouse.domain.Warehouse;
import com.portfolio.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockHistoryRepository stockHistoryRepository;

    @Transactional
    public InventoryResponse inbound(StockRequest request) {

        Warehouse warehouse = warehouseRepository
                .findById(request.warehouseId())
                .orElseThrow(() ->
                        new WarehouseNotFoundException(request.warehouseId())
                );

        Product product = productRepository
                .findById(request.productId())
                .orElseThrow(() ->
                        new ProductNotFoundException(request.productId())
                );

        inventoryRepository.upsertIncreaseQuantity(
                request.warehouseId(),
                request.productId(),
                request.quantity()
        );

        stockHistoryRepository.save(
                new StockHistory(
                        warehouse,
                        product,
                        StockMovementType.INBOUND,
                        request.quantity(),
                        null
                )
        );

        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductId(
                        request.warehouseId(),
                        request.productId()
                )
                .orElseThrow();

        return toResponse(inventory);
    }

    @Transactional
    public InventoryResponse outbound(StockRequest request) {

        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductIdForUpdate(
                        request.warehouseId(),
                        request.productId()
                )
                .orElseThrow(() ->
                        new InsufficientStockException(
                                0L,
                                request.quantity()
                        )
                );

        if (inventory.getQuantity() < request.quantity()) {
            throw new InsufficientStockException(
                    inventory.getQuantity(),
                    request.quantity()
            );
        }

        inventory.decreaseQuantity(request.quantity());
        stockHistoryRepository.save(
                new StockHistory(
                        inventory.getWarehouse(),
                        inventory.getProduct(),
                        StockMovementType.OUTBOUND,
                        request.quantity(),
                        null
                )
        );
        return toResponse(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getWarehouse().getId(),
                inventory.getWarehouse().getName(),
                inventory.getProduct().getId(),
                inventory.getProduct().getName(),
                inventory.getQuantity()
        );
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void transfer(InventoryTransferRequest request) {

        if (request.fromWarehouseId().equals(request.toWarehouseId())) {
            throw new InvalidTransferException(
                    "출발 창고와 도착 창고는 같을 수 없습니다."
            );
        }

        // 1. 창고/상품 존재 여부 먼저 확인
        warehouseRepository.findById(request.fromWarehouseId())
                .orElseThrow(() ->
                        new WarehouseNotFoundException(
                                request.fromWarehouseId()
                        )
                );

        warehouseRepository.findById(request.toWarehouseId())
                .orElseThrow(() ->
                        new WarehouseNotFoundException(
                                request.toWarehouseId()
                        )
                );

        productRepository.findById(request.productId())
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                request.productId()
                        )
                );

        Long firstWarehouseId =
                Math.min(
                        request.fromWarehouseId(),
                        request.toWarehouseId()
                );

        Long secondWarehouseId =
                Math.max(
                        request.fromWarehouseId(),
                        request.toWarehouseId()
                );

        inventoryRepository.initializeInventoryIfAbsent(
                firstWarehouseId,
                request.productId()
        );

        inventoryRepository.initializeInventoryIfAbsent(
                secondWarehouseId,
                request.productId()
        );

        Inventory firstInventory = inventoryRepository
                .findByWarehouseIdAndProductIdForUpdate(
                        firstWarehouseId,
                        request.productId()
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "재고 초기화 후 조회에 실패했습니다."
                        )
                );

        Inventory secondInventory = inventoryRepository
                .findByWarehouseIdAndProductIdForUpdate(
                        secondWarehouseId,
                        request.productId()
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "재고 초기화 후 조회에 실패했습니다."
                        )
                );

        Inventory fromInventory;
        Inventory toInventory;

        if (request.fromWarehouseId().equals(firstWarehouseId)) {
            fromInventory = firstInventory;
            toInventory = secondInventory;
        } else {
            fromInventory = secondInventory;
            toInventory = firstInventory;
        }

        if (fromInventory.getQuantity() < request.quantity()) {
            throw new InsufficientStockException(
                    fromInventory.getQuantity(),
                    request.quantity()
            );
        }

        fromInventory.decreaseQuantity(request.quantity());
        toInventory.increaseQuantity(request.quantity());

        stockHistoryRepository.save(
                new StockHistory(
                        fromInventory.getWarehouse(),
                        fromInventory.getProduct(),
                        StockMovementType.TRANSFER_OUT,
                        request.quantity(),
                        toInventory.getWarehouse()
                )
        );

        stockHistoryRepository.save(
                new StockHistory(
                        toInventory.getWarehouse(),
                        toInventory.getProduct(),
                        StockMovementType.TRANSFER_IN,
                        request.quantity(),
                        fromInventory.getWarehouse()
                )
        );
    }


}