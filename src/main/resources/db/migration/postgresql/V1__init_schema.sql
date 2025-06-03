CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE marketplace_access_requests (
    uuid VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    identifier VARCHAR(255) NOT NULL,
    operation VARCHAR(50) NOT NULL,
    requester_type VARCHAR(255),
    requester_identifier VARCHAR(255),
    reviewer_identifier VARCHAR(255),
    consumer_type VARCHAR(50) NOT NULL,
    consumer_identifier VARCHAR(255) NOT NULL,
    provider_data_product_fqn VARCHAR(255) NOT NULL,
    properties TEXT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE marketplace_access_requests_ports (
    id SERIAL PRIMARY KEY,
    access_request_uuid VARCHAR(36) NOT NULL,
    port_fqn VARCHAR(255) NOT NULL,
    FOREIGN KEY (access_request_uuid) REFERENCES marketplace_access_requests(uuid) ON DELETE CASCADE
);

CREATE TABLE marketplace_access_requests_executor_responses (
    id BIGSERIAL PRIMARY KEY,
    access_request_uuid VARCHAR(36),
    access_request_identifier VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    message TEXT,
    provider_data_product_fqn VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_access_request FOREIGN KEY (access_request_uuid) REFERENCES marketplace_access_requests(uuid) ON DELETE CASCADE
);

CREATE TABLE marketplace_access_requests_executor_responses_ports (
    executor_response_id BIGINT NOT NULL,
    port_fqn VARCHAR(255) NOT NULL,
    PRIMARY KEY (executor_response_id, port_fqn),
    CONSTRAINT fk_executor_response FOREIGN KEY (executor_response_id) REFERENCES marketplace_access_requests_executor_responses(id) ON DELETE CASCADE
);

CREATE INDEX idx_access_request_consumer_identifier ON marketplace_access_requests(consumer_identifier);
CREATE INDEX idx_access_request_provider_data_product_fqn ON marketplace_access_requests(provider_data_product_fqn);
CREATE INDEX idx_marketplace_executor_responses_access_request_uuid ON marketplace_access_requests_executor_responses(access_request_uuid);
CREATE INDEX idx_marketplace_executor_responses_access_request_identifier ON marketplace_access_requests_executor_responses(access_request_identifier);
CREATE INDEX idx_executor_responses_provider_data_product_fqn ON marketplace_access_requests_executor_responses(provider_data_product_fqn);

