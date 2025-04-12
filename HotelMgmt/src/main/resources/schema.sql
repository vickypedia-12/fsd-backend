-- Category Table
CREATE TABLE Category (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL
);

-- Supplier Table
CREATE TABLE Supplier (
    supplier_id SERIAL PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    supplier_address VARCHAR(255),
    contact_info VARCHAR(255)
);

-- Product Table
CREATE TABLE Product (
    product_id SERIAL PRIMARY KEY,
    product_name VARCHAR(255),
    product_description TEXT,
    unit_price DECIMAL(10, 2),
    reorder_level INTEGER,
    category_id INTEGER REFERENCES Category(category_id)
);

-- Product_Supplier Join Table for many-to-many relationship
CREATE TABLE Product_Supplier (
    product_id INTEGER REFERENCES Product(product_id),
    supplier_id INTEGER REFERENCES Supplier(supplier_id),
    PRIMARY KEY (product_id, supplier_id)
);