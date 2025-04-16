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
    quantity_in_stock INTEGER DEFAULT 0,
    category_id INTEGER REFERENCES Category(category_id)
);

-- Product_Supplier Join Table for many-to-many relationship
CREATE TABLE Product_Supplier (
    product_id INTEGER REFERENCES Product(product_id),
    supplier_id INTEGER REFERENCES Supplier(supplier_id),
    PRIMARY KEY (product_id, supplier_id)
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admins (
    id SERIAL PRIMARY KEY,
    user_id INT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    otp_secret VARCHAR(255) NOT NULL,
    last_login_ip VARCHAR(50),
    last_login_location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_preferences (
    id SERIAL PRIMARY KEY,
    user_id INT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    diet_type VARCHAR(50) CHECK (diet_type IN ('Veg', 'Non-Veg', 'Vegan', 'Jain', 'No preference')),
    favorite_cuisines TEXT, -- Multi-select stored as a comma-separated string
    spice_tolerance_level VARCHAR(50) CHECK (spice_tolerance_level IN ('Low', 'Medium', 'High')),
    favorite_dish VARCHAR(255),
    food_allergies TEXT,
    dining_preference VARCHAR(50) CHECK (dining_preference IN ('Dine-in', 'Takeaway', 'Home Delivery', 'No preference')),
    usually_dine_with VARCHAR(50) CHECK (usually_dine_with IN ('Alone', 'Friends', 'Family', 'Colleagues')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);