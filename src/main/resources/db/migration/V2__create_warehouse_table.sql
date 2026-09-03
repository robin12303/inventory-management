CREATE TABLE warehouse (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           name VARCHAR(255) NOT NULL,
                           code VARCHAR(100) NOT NULL,
                           address VARCHAR(500) NOT NULL,

                           PRIMARY KEY (id),
                           CONSTRAINT uk_warehouse_code UNIQUE (code)
);