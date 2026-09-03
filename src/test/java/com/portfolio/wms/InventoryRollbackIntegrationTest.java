package com.portfolio.wms;

import com.portfolio.wms.inventory.domain.Inventory;
import com.portfolio.wms.inventory.domain.StockHistory;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class InventoryRollbackIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer mysql =
            new MySQLContainer("mysql:8.0");

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    /*
     * 이 테스트에서는 StockHistoryRepository만 가짜 객체로 교체합니다.
     */
    @MockitoBean
    private StockHistoryRepository stockHistoryRepository;

    @BeforeEach
    void cleanUp() {
        inventoryRepository.deleteAll();
        warehouseRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void 이력저장중_예외가_발생하면_재고변경도_롤백된다() {

        Product product = productRepository.save(
                new Product(
                        "햄스터 사료",
                        "ROLLBACK-SKU-001",
                        new BigDecimal("15000")
                )
        );

        Warehouse fromWarehouse = warehouseRepository.save(
                new Warehouse(
                        "출발 창고",
                        "ROLLBACK-WH-1",
                        "서울"
                )
        );

        Warehouse toWarehouse = warehouseRepository.save(
                new Warehouse(
                        "도착 창고",
                        "ROLLBACK-WH-2",
                        "부산"
                )
        );

        inventoryRepository.save(
                new Inventory(
                        fromWarehouse,
                        product,
                        100L
                )
        );

        inventoryRepository.save(
                new Inventory(
                        toWarehouse,
                        product,
                        0L
                )
        );

        /*
         * 첫 번째 history 저장은 성공한 것처럼 처리하고
         * 두 번째 history 저장에서 강제로 예외 발생
         */
        AtomicInteger count = new AtomicInteger();

        when(stockHistoryRepository.save(any(StockHistory.class)))
                .thenAnswer(invocation -> {

                    if (count.incrementAndGet() == 2) {
                        throw new RuntimeException(
                                "강제 이력 저장 실패"
                        );
                    }

                    return invocation.getArgument(0);
                });

        InventoryTransferRequest request =
                new InventoryTransferRequest(
                        fromWarehouse.getId(),
                        toWarehouse.getId(),
                        product.getId(),
                        30L
                );

        assertThatThrownBy(() ->
                inventoryService.transfer(request)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("강제 이력 저장 실패");

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

        /*
         * transfer()에서는 잠깐 70 / 30으로 변경했지만
         * 예외 때문에 DB 트랜잭션이 ROLLBACK되어야 함
         */
        assertThat(fromInventory.getQuantity())
                .isEqualTo(100L);

        assertThat(toInventory.getQuantity())
                .isEqualTo(0L);
    }
}