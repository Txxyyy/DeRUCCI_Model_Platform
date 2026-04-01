-- V4: Device table
CREATE TABLE IF NOT EXISTS t_device (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    device_key VARCHAR(100) UNIQUE,
    serial_number VARCHAR(100) UNIQUE,
    product_id BIGINT,
    product_name VARCHAR(100),
    thing_model_id BIGINT,
    firmware_version VARCHAR(50),
    status VARCHAR(20) DEFAULT 'OFFLINE',
    last_online_time TIMESTAMP,
    last_offline_time TIMESTAMP,
    tags TEXT,
    properties TEXT,
    device_type VARCHAR(20) DEFAULT 'TEST',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time TIMESTAMP NOT NULL DEFAULT NOW()
);
