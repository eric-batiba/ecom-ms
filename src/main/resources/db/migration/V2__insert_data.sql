INSERT INTO category (id, name, description)
VALUES
    (nextval('category_seq'), 'Electronics', 'Devices and gadgets like smartphones, laptops, etc.'),
    (nextval('category_seq'), 'Clothing', 'Men and women clothing like shirts, jeans, etc.'),
    (nextval('category_seq'), 'Books', 'Various genres of books including fiction and non-fiction.'),
    (nextval('category_seq'), 'Furniture', 'Home and office furniture like tables, chairs, etc.'),
    (nextval('category_seq'), 'Groceries', 'Daily essentials and food items.');

INSERT INTO product (id, name, description, available_quantity, price, category_id)
VALUES
    (nextval('product_seq'), 'iPhone 14', 'Latest Apple smartphone.', 50, 999.99, currval('category_seq') - 200),
    (nextval('product_seq'), 'Laptop Dell XPS 13', 'High performance laptop.', 30, 1299.99, currval('category_seq') - 200),
    (nextval('product_seq'), 'Bluetooth Headphones', 'Wireless noise-cancelling headphones.', 100, 199.99, currval('category_seq') - 200),

    (nextval('product_seq'), 'Men s T-shirt', '100% cotton, round neck t-shirt.', 200, 19.99, currval('category_seq') - 150),
    (nextval('product_seq'), 'Women s Jeans', 'Skinny fit denim jeans.', 150, 39.99, currval('category_seq') - 150),

    (nextval('product_seq'), 'The Great Gatsby', 'Classic novel by F. Scott Fitzgerald.', 500, 10.99, currval('category_seq') - 100),
    (nextval('product_seq'), 'Educated', 'Memoir by Tara Westover.', 300, 14.99, currval('category_seq') - 100),

    (nextval('product_seq'), 'Office Chair', 'Ergonomic office chair.', 75, 199.99, currval('category_seq') - 50),
    (nextval('product_seq'), 'Wooden Dining Table', '6-seater wooden dining table.', 20, 499.99, currval('category_seq') - 50),

    (nextval('product_seq'), 'Organic Apples', 'Fresh organic apples.', 1000, 2.99, currval('category_seq'));