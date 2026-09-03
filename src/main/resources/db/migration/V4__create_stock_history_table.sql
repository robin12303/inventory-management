CREATE TABLE stock_history (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               warehouse_id BIGINT NOT NULL,
                               product_id BIGINT NOT NULL,
                               movement_type VARCHAR(30) NOT NULL,
                               quantity BIGINT NOT NULL,
                               related_warehouse_id BIGINT NULL,
                               created_at DATETIME(6) NOT NULL,

                               PRIMARY KEY (id),

                               CONSTRAINT fk_stock_history_warehouse
                                   FOREIGN KEY (warehouse_id)
                                       REFERENCES warehouse(id),

                               CONSTRAINT fk_stock_history_product
                                   FOREIGN KEY (product_id)
                                       REFERENCES product(id),

                               CONSTRAINT fk_stock_history_related_warehouse
                                   FOREIGN KEY (related_warehouse_id)
                                       REFERENCES warehouse(id)
);

CREATE INDEX idx_stock_history_warehouse
    ON stock_history (warehouse_id);

CREATE INDEX idx_stock_history_product
    ON stock_history (product_id);