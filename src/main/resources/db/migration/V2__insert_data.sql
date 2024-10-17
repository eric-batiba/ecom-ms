INSERT INTO orders (id, customer_id, payment_method, reference, total_amount) VALUES
(nextval('orders_id_seq'), 'cust001', 'CREDIT_CARD', 'REF001', 100.50);

INSERT INTO order_item (id, product_id, quantity, order_id) VALUES
(nextval('order_item_id_seq'), 1, 2, 1);