package com.portfolio.wms.warehouse.controller;

import com.portfolio.wms.warehouse.dto.WarehouseCreateRequest;
import com.portfolio.wms.warehouse.dto.WarehouseResponse;
import com.portfolio.wms.warehouse.dto.WarehouseUpdateRequest;
import com.portfolio.wms.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public WarehouseResponse createWarehouse(
            @Valid @RequestBody WarehouseCreateRequest request
    ) {
        return warehouseService.createWarehouse(request);
    }

    @GetMapping("/{id}")
    public WarehouseResponse getWarehouse(
            @PathVariable Long id
    ) {
        return warehouseService.getWarehouse(id);
    }

    @GetMapping
    public List<WarehouseResponse> getWarehouses() {
        return warehouseService.getWarehouses();
    }

    @PutMapping("/{id}")
    public WarehouseResponse updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseUpdateRequest request
    ) {
        return warehouseService.updateWarehouse(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteWarehouse(
            @PathVariable Long id
    ) {
        warehouseService.deleteWarehouse(id);
    }
}