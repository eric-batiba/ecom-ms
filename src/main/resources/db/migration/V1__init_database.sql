CREATE SEQUENCE IF NOT EXISTS payment_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS payment (
    id INTEGER NOT NULL DEFAULT nextval('payment_id_seq') PRIMARY KEY ,
    total_amount NUMERIC(15,2) NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    order_id INTEGER NOT NULL,
    payment_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_update TIMESTAMP
);

