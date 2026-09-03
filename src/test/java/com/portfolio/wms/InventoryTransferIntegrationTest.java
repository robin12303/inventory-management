package com.portfolio.wms;

import com.portfolio.wms.inventory.domain.Inventory;
import com.portfolio.wms.inventory.domain.StockHistory;
import com.portfolio.wms.inventory.domain.StockMovementType;
import com.portfolio.wms.inventory.dto.InventoryTransferRequest;
import com.portfolio.wms.inventory.repository.InventoryRepository;
import com.portfolio.wms.inventory.repository.StockHistoryRepository;
import com.portfolio.wms.inventory.service.InventoryService;
import com.portfolio.wms.product.domain.Product;
import com.portfolio.wms.product.repository.ProductRepository;
import com.portfolio.wms.warehouse.domain.Warehouse;
import com.portfolio.wms.warehouse.repository.WarehouseRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class InventoryTransferIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0");

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @BeforeEach
    void cleanUp() {
        stockHistoryRepository.deleteAll();
        inventoryRepository.deleteAll();
        warehouseRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void 목적지_재고가_없어도_창고이동이_성공한다() {

        Product product = productRepository.save(
                new Product(
                        "햄스터 사료",
                        "TEST-SKU-001",
                        new BigDecimal("15000")
                )
        );

        Warehouse fromWarehouse = warehouseRepository.save(
                new Warehouse(
                        "용인 창고",
                        "TEST-YI",
                        "경기도 용인시"
                )
        );

        Warehouse toWarehouse = warehouseRepository.save(
                new Warehouse(
                        "수원 창고",
                        "TEST-SW",
                        "경기도 수원시"
                )
        );

        inventoryRepository.save(
                new Inventory(
                        fromWarehouse,
                        product,
                        100L
                )
        );

        InventoryTransferRequest request =
                new InventoryTransferRequest(
                        fromWarehouse.getId(),
                        toWarehouse.getId(),
                        product.getId(),
                        30L
                );

        inventoryService.transfer(request);

        Inventory fromInventory =
                inventoryRepository
                        .findByWarehouseIdAndProductId(
                                fromWarehouse.getId(),
                                product.getId()
                        )
                        .orElseThrow();

        Inventory toInventory =
                inventoryRepository
                        .findByWarehouseIdAndProductId(
                                toWarehouse.getId(),
                                product.getId()
                        )
                        .orElseThrow();

        assertThat(fromInventory.getQuantity())
                .isEqualTo(70L);

        assertThat(toInventory.getQuantity())
                .isEqualTo(30L);

        List<StockHistory> histories =
                stockHistoryRepository.findAll();

        assertThat(histories)
                .hasSize(2);

        assertThat(histories)
                .extracting(StockHistory::getMovementType)
                .containsExactlyInAnyOrder(
                        StockMovementType.TRANSFER_OUT,
                        StockMovementType.TRANSFER_IN
                );
    }

    @Test
    void 반대방향_동시_창고이동에서도_재고정합성이_유지된다() throws Exception {

        Product product = productRepository.save(
                new Product(
                        "햄스터 사료",
                        "CONCURRENT-SKU-001",
                        new BigDecimal("15000")
                )
        );

        Warehouse warehouse1 = warehouseRepository.save(
                new Warehouse(
                        "창고 1",
                        "CONCURRENT-WH-1",
                        "서울"
                )
        );

        Warehouse warehouse2 = warehouseRepository.save(
                new Warehouse(
                        "창고 2",
                        "CONCURRENT-WH-2",
                        "부산"
                )
        );

        inventoryRepository.save(
                new Inventory(
                        warehouse1,
                        product,
                        100L
                )
        );

        inventoryRepository.save(
                new Inventory(
                        warehouse2,
                        product,
                        100L
                )
        );

        InventoryTransferRequest request1 =
                new InventoryTransferRequest(
                        warehouse1.getId(),
                        warehouse2.getId(),
                        product.getId(),
                        30L
                );

        InventoryTransferRequest request2 =
                new InventoryTransferRequest(
                        warehouse2.getId(),
                        warehouse1.getId(),
                        product.getId(),
                        30L
                );

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        Future<?> future1 = executor.submit(() -> {
            startLatch.await();
            inventoryService.transfer(request1);
            return null;
        });

        Future<?> future2 = executor.submit(() -> {
            startLatch.await();
            inventoryService.transfer(request2);
            return null;
        });

        // 두 스레드를 거의 동시에 출발시킴
        startLatch.countDown();

        // 데드락/예외가 발생하면 여기서 테스트 실패
        future1.get(10, TimeUnit.SECONDS);
        future2.get(10, TimeUnit.SECONDS);

        executor.shutdown();

        Inventory inventory1 =
                inventoryRepository
                        .findByWarehouseIdAndProductId(
                                warehouse1.getId(),
                                product.getId()
                        )
                        .orElseThrow();

        Inventory inventory2 =
                inventoryRepository
                        .findByWarehouseIdAndProductId(
                                warehouse2.getId(),
                                product.getId()
                        )
                        .orElseThrow();

        assertThat(inventory1.getQuantity())
                .isEqualTo(100L);

        assertThat(inventory2.getQuantity())
                .isEqualTo(100L);

        List<StockHistory> histories =
                stockHistoryRepository.findAll();

        assertThat(histories)
                .hasSize(4);

        assertThat(histories)
                .extracting(StockHistory::getMovementType)
                .containsExactlyInAnyOrder(
                        StockMovementType.TRANSFER_OUT,
                        StockMovementType.TRANSFER_IN,
                        StockMovementType.TRANSFER_OUT,
                        StockMovementType.TRANSFER_IN
                );
    }
}