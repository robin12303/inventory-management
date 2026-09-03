CREATE TABLE inventory (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           warehouse_id BIGINT NOT NULL,
                           product_id BIGINT NOT NULL,
                           quantity BIGINT NOT NULL DEFAULT 0,

                           PRIMARY KEY (id),

                           CONSTRAINT fk_inventory_warehouse
                               FOREIGN KEY (warehouse_id)
                                   REFERENCES warehouse(id),

                           CONSTRAINT fk_inventory_product
                               FOREIGN KEY (product_id)
                                   REFERENCES product(id),

                           CONSTRAINT uk_inventory_warehouse_product
                               UNIQUE (warehouse_id, product_id)
);