USE `product-db`;

DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE `suppliers`
(
    `supplier_id` CHAR(32)     NOT NULL PRIMARY KEY,
    `user_id`     CHAR(32)     NOT NULL,
    `ghn_shop_id` INT          NOT NULL,
    `address_id`  CHAR(32)     NOT NULL,
    `name`        VARCHAR(255) NOT NULL,
    `avatar`      VARCHAR(255),
    `contact_url` VARCHAR(255),
    `rate`        FLOAT4   DEFAULT 0.0,
    `create_at`   DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET = utf8mb4
    COMMENT = 'Store all supplier';

DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`
(
    `product_id`          CHAR(32)     NOT NULL PRIMARY KEY,
    `supplier_id`         CHAR(32)     NOT NULL,
    `sku`                 CHAR(15)     NOT NULL UNIQUE,
    `name`                VARCHAR(255) NOT NULL,
    `description`         TEXT,
    `brand`               VARCHAR(255),
    `price`               FLOAT4,
    `listed_price`        FLOAT4,
    `amount`              INT4         NOT NULL,
    `sold_amount`         INT4         NOT NULL DEFAULT 0,
    `rate`                FLOAT4                DEFAULT 0.0,
    `total_rate_amount`   INT4,
    `discount`            INT4                  DEFAULT 0,
    `saleable`            BOOLEAN               DEFAULT TRUE,
    `start_date_discount` DATETIME              DEFAULT CURRENT_TIMESTAMP,
    `end_date_discount`   DATETIME              DEFAULT CURRENT_TIMESTAMP,
    `weight`              INT4,
    `height`              INT4,
    `length`              INT4,
    `width`               INT4,
    `created_at`          DATETIME              DEFAULT CURRENT_TIMESTAMP,

    INDEX                 id_index (product_id),
    INDEX                 sku_index (sku),

    FOREIGN KEY (supplier_id) REFERENCES suppliers (supplier_id) ON DELETE CASCADE

) CHARACTER SET = utf8mb4
    COMMENT = 'Store all product';

DROP TABLE IF EXISTS `product_extras`;
CREATE TABLE `product_extras`
(
    `product_id`       CHAR(32) NOT NULL PRIMARY KEY,
    `enable_3D_viewer` BOOLEAN DEFAULT FALSE,
    `enable_ar_viewer` BOOLEAN DEFAULT FALSE,
    `source`           VARCHAR(255),

    INDEX              id_index (product_id),

    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE
) CHARACTER SET = utf8mb4
    COMMENT = 'Store all product extra';

DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`
(
    `category_id` CHAR(32)     NOT NULL PRIMARY KEY,
    `name`        VARCHAR(255) NOT NULL,
    `image`       VARCHAR(255) NOT NULL
) CHARACTER SET = utf8mb4
    COMMENT = 'Store all categories';

DROP TABLE IF EXISTS `categories_products`;
CREATE TABLE `categories_products`
(
    `category_product_id` CHAR(32) NOT NULL PRIMARY KEY,
    `product_id`          CHAR(32) NOT NULL,
    `category_id`         CHAR(32) NOT NULL,

    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE CASCADE
) CHARACTER SET = utf8mb4
    COMMENT = 'Store all categories and products relationship';

DROP TABLE IF EXISTS `users_products`;
CREATE TABLE `users_products`
(
    `user_product_id` CHAR(32) NOT NULL PRIMARY KEY,
    `product_id`      CHAR(32) NOT NULL,
    `user_id`         CHAR(32) NOT NULL,
    `rate`            FLOAT4   DEFAULT 0.0,
    `match_rate`      FLOAT4   DEFAULT 0.0,
    `is_reviewed`     BOOLEAN  DEFAULT FALSE,
    `comment`         TEXT,
    `updated_at`      DATETIME DEFAULT CURRENT_TIMESTAMP,
    `created_at`      DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX             product_id_index (product_id),

    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE

) CHARACTER SET = utf8mb4
    COMMENT = 'Store all users and products relationship';

DROP TABLE IF EXISTS `images`;
CREATE TABLE `images`
(
    `image_id` CHAR(32)     NOT NULL PRIMARY KEY,
    `owner_id` CHAR(32)     NOT NULL,
    `url`      VARCHAR(255) NOT NULL,

    INDEX      owner_id_index (owner_id)

) CHARACTER SET = utf8mb4
    COMMENT = 'Store all images';

DROP TABLE IF EXISTS `promotions`;
CREATE TABLE `promotions`
(
    `promotion_id` CHAR(32) NOT NULL PRIMARY KEY,
    `supplier_id`  CHAR(32) NOT NULL,
    `name`         VARCHAR(255),
    `value`        FLOAT4,
    `enable`       BOOLEAN DEFAULT TRUE,
    `image`        VARCHAR(255),
    `total_amount` INT     DEFAULT 0
) CHARACTER SET = utf8mb4
    COMMENT = 'Store all promotion';

