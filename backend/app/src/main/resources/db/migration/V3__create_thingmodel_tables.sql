-- V3: ThingModel tables
CREATE TABLE IF NOT EXISTS t_thing_model (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) UNIQUE,
    description VARCHAR(500),
    category VARCHAR(100),
    properties_json TEXT,
    events_json TEXT,
    commands_json TEXT,
    status VARCHAR(20) DEFAULT 'DRAFT',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS t_thing_model_point (
    id BIGSERIAL PRIMARY KEY,
    thing_model_id BIGINT NOT NULL,
    point_id VARCHAR(100),
    name VARCHAR(100),
    point_type VARCHAR(20),
    data_type VARCHAR(20),
    access VARCHAR(20),
    unit VARCHAR(50),
    range_json VARCHAR(500),
    enum_values_json TEXT,
    max_length INTEGER,
    element_type VARCHAR(20),
    default_value VARCHAR(200),
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS t_thing_model_template (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) UNIQUE,
    category VARCHAR(100),
    description VARCHAR(500),
    properties_json TEXT,
    events_json TEXT,
    commands_json TEXT,
    property_count INTEGER DEFAULT 0,
    event_count INTEGER DEFAULT 0,
    command_count INTEGER DEFAULT 0,
    version VARCHAR(20),
    system BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'DRAFT',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time TIMESTAMP NOT NULL DEFAULT NOW()
);
