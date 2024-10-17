CREATE SEQUENCE IF NOT EXISTS category_seq INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS product_seq INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS category(
    id INTEGER NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS product (
    id INTEGER NOT NULL PRIMARY KEY ,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    available_quantity DOUBLE PRECISION NOT NULL,
    price NUMERIC(15,2) NOT NULL,
    category_id INT,
        CONSTRAINT fk_category
        FOREIGN KEY(category_id)
        REFERENCES category(id)
);
