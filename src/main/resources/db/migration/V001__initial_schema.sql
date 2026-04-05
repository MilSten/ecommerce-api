
CREATE TABLE addresses
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id      UUID,
    country      VARCHAR(255)                NOT NULL,
    city         VARCHAR(255)                NOT NULL,
    street       VARCHAR(255)                NOT NULL,
    postal_code  VARCHAR(255)                NOT NULL,
    is_default   BOOLEAN                     NOT NULL,
    address_type VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_addresses PRIMARY KEY (id)
);

CREATE TABLE audit_logs
(
    id          UUID                        NOT NULL,
    entity_type VARCHAR(255)                NOT NULL,
    entity_id   UUID                        NOT NULL,
    action      VARCHAR(255)                NOT NULL,
    user_id     UUID                        NOT NULL,
    changes     TEXT,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_audit_logs PRIMARY KEY (id)
);

CREATE TABLE cart_items
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    cart_id       UUID,
    product_id    UUID,
    quantity      INTEGER                     NOT NULL,
    price_at_time DECIMAL                     NOT NULL,
    added_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_cart_items PRIMARY KEY (id)
);

CREATE TABLE carts
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    UUID,
    session    VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_carts PRIMARY KEY (id)
);

CREATE TABLE categories
(
    id                 UUID                        NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name               VARCHAR(255)                NOT NULL,
    slug               VARCHAR(255)                NOT NULL,
    description        TEXT,
    parent_category_id UUID,
    is_active          BOOLEAN                     NOT NULL,
    position           INTEGER                     NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE coupon_usages
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    coupon_id  UUID,
    user_id    UUID,
    order_id   UUID,
    used_at    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_coupon_usages PRIMARY KEY (id)
);

CREATE TABLE coupons
(
    id               UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    code             VARCHAR(255)                NOT NULL,
    discount_type    VARCHAR(255)                NOT NULL,
    discount_value   DECIMAL                     NOT NULL,
    max_usage_count  INTEGER                     NOT NULL,
    min_order_amount DECIMAL                     NOT NULL,
    valid_from       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    valid_to         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_active        BOOLEAN                     NOT NULL,
    CONSTRAINT pk_coupons PRIMARY KEY (id)
);

CREATE TABLE media_files
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    filename          VARCHAR(255)                NOT NULL,
    original_filename VARCHAR(255)                NOT NULL,
    file_path         VARCHAR(255)                NOT NULL,
    file_size         BIGINT                      NOT NULL,
    mime_type         VARCHAR(255)                NOT NULL,
    uploaded_by_id    UUID,
    CONSTRAINT pk_media_files PRIMARY KEY (id)
);

CREATE TABLE order_items
(
    id                 UUID                        NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    order_id           UUID                        NOT NULL,
    product_variant_id UUID                        NOT NULL,
    product_name       VARCHAR(255)                NOT NULL,
    quantity           INTEGER                     NOT NULL,
    unit_price         DECIMAL                     NOT NULL,
    total_price        DECIMAL                     NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id)
);

CREATE TABLE order_status_history
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    order_id     UUID,
    order_status VARCHAR(255)                NOT NULL,
    changed_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    comment      VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_order_status_history PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id                  UUID                        NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id             UUID,
    email               VARCHAR(255)                NOT NULL,
    status              VARCHAR(255)                NOT NULL,
    total_price         DECIMAL                     NOT NULL,
    tax_amount          DECIMAL                     NOT NULL,
    shipping_cost       DECIMAL                     NOT NULL,
    discount_amount     DECIMAL                     NOT NULL,
    shipping_address_id UUID,
    billing_address_id  UUID,
    notes               TEXT,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE product_attributes
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    product_id UUID,
    name       VARCHAR(255)                NOT NULL,
    value      VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_product_attributes PRIMARY KEY (id)
);

CREATE TABLE product_images
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    product_id UUID                        NOT NULL,
    image_id   UUID                        NOT NULL,
    position   VARCHAR(255)                NOT NULL,
    is_main    BOOLEAN                     NOT NULL,
    CONSTRAINT pk_product_images PRIMARY KEY (id)
);

CREATE TABLE product_reviews
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    product_id  UUID,
    user_id     UUID,
    rating      INTEGER                     NOT NULL,
    title       VARCHAR(255)                NOT NULL,
    comment     VARCHAR(255)                NOT NULL,
    is_approved BOOLEAN                     NOT NULL,
    CONSTRAINT pk_product_reviews PRIMARY KEY (id)
);

CREATE TABLE product_variants
(
    id             UUID                        NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    product_id     UUID                        NOT NULL,
    sku            VARCHAR(255)                NOT NULL,
    name           TEXT                        NOT NULL,
    price          DECIMAL                     NOT NULL,
    stock_quantity INTEGER                     NOT NULL,
    CONSTRAINT pk_product_variants PRIMARY KEY (id)
);

CREATE TABLE products
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name              VARCHAR(255)                NOT NULL,
    slug              VARCHAR(255)                NOT NULL,
    description       TEXT,
    short_description TEXT,
    price             DECIMAL                     NOT NULL,
    cost              DECIMAL,
    is_active         BOOLEAN                     NOT NULL,
    stock_quantity    INTEGER                     NOT NULL,
    rating            DECIMAL                     NOT NULL,
    review_count      INTEGER                     NOT NULL,
    category_id       UUID                        NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE refresh_tokens
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id     UUID,
    token       VARCHAR(512)                NOT NULL,
    expiry_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

CREATE TABLE users
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    email         VARCHAR(255)                NOT NULL,
    password_hash VARCHAR(255)                NOT NULL,
    first_name    VARCHAR(255)                NOT NULL,
    last_name     VARCHAR(255)                NOT NULL,
    phone         VARCHAR(255),
    role          VARCHAR(255)                NOT NULL,
    is_active     BOOLEAN                     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE wishlists
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    UUID,
    product_id UUID,
    CONSTRAINT pk_wishlists PRIMARY KEY (id)
);

ALTER TABLE carts
    ADD CONSTRAINT uc_carts_user UNIQUE (user_id);

ALTER TABLE categories
    ADD CONSTRAINT uc_categories_slug UNIQUE (slug);

ALTER TABLE product_reviews
    ADD CONSTRAINT uc_product_reviews_user UNIQUE (user_id);

ALTER TABLE product_variants
    ADD CONSTRAINT uc_product_variants_sku UNIQUE (sku);

ALTER TABLE products
    ADD CONSTRAINT uc_products_slug UNIQUE (slug);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_user UNIQUE (user_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE wishlists
    ADD CONSTRAINT uc_wishlists_user UNIQUE (user_id);

CREATE INDEX idx_address_default ON addresses (is_default);

CREATE INDEX idx_category_slug ON categories (slug);

CREATE INDEX idx_coupon_code ON coupons (code);

CREATE INDEX idx_coupon_validity ON coupons (valid_from, valid_to);

CREATE INDEX idx_media_filename ON media_files (filename);

CREATE INDEX idx_media_original_filename ON media_files (original_filename);

CREATE INDEX idx_order_created ON orders (created_at);

CREATE INDEX idx_order_status ON orders (status);

CREATE INDEX idx_product_active ON products (is_active);

CREATE INDEX idx_product_attribute_name ON product_attributes (name);

CREATE INDEX idx_product_slug ON products (slug);

CREATE INDEX idx_refresh_token_expiry ON refresh_tokens (expiry_date);

CREATE INDEX idx_status_history_changed_at ON order_status_history (changed_at);

CREATE INDEX idx_user_email ON users (email);

CREATE INDEX idx_user_role ON users (role);

CREATE INDEX idx_variant_sku ON product_variants (sku);

ALTER TABLE addresses
    ADD CONSTRAINT FK_ADDRESSES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_address_user ON addresses (user_id);

ALTER TABLE carts
    ADD CONSTRAINT FK_CARTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_cart_user ON carts (user_id);

ALTER TABLE cart_items
    ADD CONSTRAINT FK_CART_ITEMS_ON_CART FOREIGN KEY (cart_id) REFERENCES carts (id);

CREATE INDEX idx_cart_item_cart ON cart_items (cart_id);

ALTER TABLE cart_items
    ADD CONSTRAINT FK_CART_ITEMS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

CREATE INDEX idx_cart_item_product ON cart_items (product_id);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_PARENT_CATEGORY FOREIGN KEY (parent_category_id) REFERENCES categories (id);

ALTER TABLE coupon_usages
    ADD CONSTRAINT FK_COUPON_USAGES_ON_COUPON FOREIGN KEY (coupon_id) REFERENCES coupons (id);

CREATE INDEX idx_coupon_usage_coupon_id ON coupon_usages (coupon_id);

ALTER TABLE coupon_usages
    ADD CONSTRAINT FK_COUPON_USAGES_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE coupon_usages
    ADD CONSTRAINT FK_COUPON_USAGES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_coupon_usage_user_id ON coupon_usages (user_id);

ALTER TABLE media_files
    ADD CONSTRAINT FK_MEDIA_FILES_ON_UPLOADED_BY FOREIGN KEY (uploaded_by_id) REFERENCES users (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_BILLING_ADDRESS FOREIGN KEY (billing_address_id) REFERENCES addresses (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_SHIPPING_ADDRESS FOREIGN KEY (shipping_address_id) REFERENCES addresses (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_order_user ON orders (user_id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

CREATE INDEX idx_order_item_order ON order_items (order_id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_PRODUCT_VARIANT FOREIGN KEY (product_variant_id) REFERENCES product_variants (id);

CREATE INDEX idx_order_item_product_variant ON order_items (product_variant_id);

ALTER TABLE order_status_history
    ADD CONSTRAINT FK_ORDER_STATUS_HISTORY_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

CREATE INDEX idx_status_history_order ON order_status_history (order_id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

CREATE INDEX idx_product_category ON products (category_id);

ALTER TABLE product_attributes
    ADD CONSTRAINT FK_PRODUCT_ATTRIBUTES_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE product_images
    ADD CONSTRAINT FK_PRODUCT_IMAGES_ON_IMAGE FOREIGN KEY (image_id) REFERENCES media_files (id);

CREATE INDEX idx_product_image_image ON product_images (image_id);

ALTER TABLE product_images
    ADD CONSTRAINT FK_PRODUCT_IMAGES_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

CREATE INDEX idx_product_image_product ON product_images (product_id);

ALTER TABLE product_reviews
    ADD CONSTRAINT FK_PRODUCT_REVIEWS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

CREATE INDEX idx_review_product ON product_reviews (product_id);

ALTER TABLE product_reviews
    ADD CONSTRAINT FK_PRODUCT_REVIEWS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_review_user ON product_reviews (user_id);

ALTER TABLE product_variants
    ADD CONSTRAINT FK_PRODUCT_VARIANTS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

CREATE INDEX idx_variant_product ON product_variants (product_id);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT FK_REFRESH_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_refresh_token_user ON refresh_tokens (user_id);

ALTER TABLE wishlists
    ADD CONSTRAINT FK_WISHLISTS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE wishlists
    ADD CONSTRAINT FK_WISHLISTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_wishlist_user ON wishlists (user_id);