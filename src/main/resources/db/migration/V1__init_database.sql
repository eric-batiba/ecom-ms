CREATE SEQUENCE IF NOT EXISTS orders_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS order_item_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS orders (
    id INTEGER NOT NULL DEFAULT nextval('orders_id_seq') PRIMARY KEY ,
    customer_id VARCHAR(255) NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    reference VARCHAR(255) NOT NULL,
    total_amount NUMERIC(15,2) NOT NULL,
    order_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    order_update TIMESTAMP
);


CREATE TABLE IF NOT EXISTS order_item(
    id INTEGER NOT NULL DEFAULT nextval('order_item_id_seq') PRIMARY KEY,
    product_id INTEGER NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    order_id INTEGER NOT NULL,
        CONSTRAINT fk_order
        FOREIGN KEY(order_id)
        REFERENCES orders(id)
);
