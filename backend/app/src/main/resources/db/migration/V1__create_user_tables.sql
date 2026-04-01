-- V1: User tables
CREATE TABLE IF NOT EXISTS t_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ENABLED',
    role VARCHAR(20) DEFAULT 'DEVELOPER',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time TIMESTAMP NOT NULL DEFAULT NOW(),
    last_login_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_user_permission (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    module VARCHAR(50) NOT NULL,
    access VARCHAR(10),
    action VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS t_user_product (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL
);
