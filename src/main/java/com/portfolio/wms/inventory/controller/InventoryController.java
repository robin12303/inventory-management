package com.portfolio.wms.inventory.controller;

import com.portfolio.wms.inventory.dto.InventoryResponse;
import com.portfolio.wms.inventory.dto.InventoryTransferRequest;
import com.portfolio.wms.inventory.dto.StockRequest;
import com.portfolio.wms.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/inbound")
    public InventoryResponse inbound(
            @Valid @RequestBody StockRequest request
    ) {
        return inventoryService.inbound(request);
    }

    @PostMapping("/outbound")
    public InventoryResponse outbound(
            @Valid @RequestBody StockRequest request
    ) {
        return inventoryService.outbound(request);
    }

    @GetMapping
    public List<InventoryResponse> getInventories() {
        return inventoryService.getInventories();
    }

    @PostMapping("/transfer")
    public void transfer(
            @Valid @RequestBody InventoryTransferRequest request
    ) {
        inventoryService.transfer(request);
    }
}