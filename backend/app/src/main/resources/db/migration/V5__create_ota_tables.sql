-- V5: OTA tables
CREATE TABLE IF NOT EXISTS t_firmware (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    version VARCHAR(50),
    product_id BIGINT,
    file_url VARCHAR(500),
    file_size BIGINT,
    file_md5 VARCHAR(100),
    description VARCHAR(500),
    status VARCHAR(20) DEFAULT 'DRAFT',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS t_ota_task (
    id BIGSERIAL PRIMARY KEY,
    firmware_id BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',
    target_device_ids TEXT,
    success_count INTEGER DEFAULT 0,
    failure_count INTEGER DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time TIMESTAMP NOT NULL DEFAULT NOW()
);
