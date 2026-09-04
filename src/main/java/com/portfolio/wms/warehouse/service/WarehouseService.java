package com.portfolio.wms.warehouse.service;

import com.portfolio.wms.common.exception.WarehouseInUseException;
import com.portfolio.wms.common.exception.WarehouseNotFoundException;
import com.portfolio.wms.inventory.repository.InventoryRepository;
import com.portfolio.wms.warehouse.domain.Warehouse;
import com.portfolio.wms.warehouse.dto.WarehouseCreateRequest;
import com.portfolio.wms.warehouse.dto.WarehouseResponse;
import com.portfolio.wms.warehouse.dto.WarehouseUpdateRequest;
import com.portfolio.wms.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    public WarehouseResponse createWarehouse(
            WarehouseCreateRequest request
    ) {
        Warehouse warehouse = new Warehouse(
                request.name(),
                request.code(),
                request.address()
        );

        Warehouse saved = warehouseRepository.save(warehouse);

        return toResponse(saved);
    }

    public WarehouseResponse getWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        return toResponse(warehouse);
    }

    public List<WarehouseResponse> getWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WarehouseResponse updateWarehouse(
            Long id,
            WarehouseUpdateRequest request
    ) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        warehouse.update(
                request.name(),
                request.address()
        );

        Warehouse saved = warehouseRepository.save(warehouse);

        return toResponse(saved);
    }

    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        if (inventoryRepository.existsByWarehouseId(id)) {
            throw new WarehouseInUseException(id);
        }

        warehouseRepository.delete(warehouse);
    }

    private WarehouseResponse toResponse(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getCode(),
                warehouse.getAddress()
        );
    }
}