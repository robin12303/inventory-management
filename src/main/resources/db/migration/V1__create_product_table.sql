CREATE TABLE product (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         name VARCHAR(255) NOT NULL,
                         sku VARCHAR(255) NOT NULL,
                         price DECIMAL(19, 2) NOT NULL,

                         PRIMARY KEY (id),
                         CONSTRAINT uk_product_sku UNIQUE (sku)
);