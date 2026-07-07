CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    version BIGINT DEFAULT 0, -- optimistic locking
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Index for frequent lookups by category
CREATE INDEX idx_products_category_id ON products(category_id);

-- Index for case-insensitive unique name per category
CREATE UNIQUE INDEX uq_products_category_name_lower ON products(category_id, LOWER(name));