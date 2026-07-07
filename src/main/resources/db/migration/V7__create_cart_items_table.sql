CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,

    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id) REFERENCES carts(id),

    CONSTRAINT fk_cart_items_product
        FOREIGN KEY (product_id) REFERENCES products(id),

    -- a product can only appear once per cart
    CONSTRAINT uq_cart_items_product
        UNIQUE (cart_id, product_id)
);
