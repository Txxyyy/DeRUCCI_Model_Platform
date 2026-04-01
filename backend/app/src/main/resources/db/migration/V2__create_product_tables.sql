-- V2: Product tables
CREATE TABLE IF NOT EXISTS t_product_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS t_product (
    id BIGSERIAL PRIMARY KEY,
    pid VARCHAR(50) UNIQUE,
    name VARCHAR(100) NOT NULL,
    model VARCHAR(100) UNIQUE,
    brand VARCHAR(100),
    category VARCHAR(100),
    description VARCHAR(500),
    image_url VARCHAR(500),
    protocol VARCHAR(20),
    thing_model_id BIGINT,
    status VARCHAR(20) DEFAULT 'DEVELOPING',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time TIMESTAMP NOT NULL DEFAULT NOW()
);
