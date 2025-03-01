DROP TABLE IF EXISTS payments CASCADE;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE payments (
    payment_id      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id        UUID             NOT NULL,
    total_payment   DOUBLE PRECISION NOT NULL,
    delivery_total  DOUBLE PRECISION NOT NULL,
    fee_total       DOUBLE PRECISION NOT NULL,
    payment_state   VARCHAR(50)      NOT NULL
);