-- Drop existing tables if they exist
DROP TABLE IF EXISTS project_components;
DROP TABLE IF EXISTS quotes;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS labor;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS components;

-- Create Clients table
CREATE TABLE clients (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    is_professional BOOLEAN NOT NULL,
    discount_rate DOUBLE PRECISION NOT NULL
);

-- Create Projects table
CREATE TABLE projects (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    surface DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    profit_margin DECIMAL(5,2) NOT NULL,
    total_cost DECIMAL(10,2) NOT NULL,
    client_id BIGINT REFERENCES clients(id)
);

-- Create Components table
CREATE TABLE components (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    taux_tva DECIMAL(5,2) NOT NULL
);

-- Create Materials table (inherits from Components)
CREATE TABLE materials (
    id BIGINT PRIMARY KEY REFERENCES components(id),
    cout_unitaire DECIMAL(10,2) NOT NULL,
    quantite DECIMAL(10,2) NOT NULL,
    cout_transport DECIMAL(10,2) NOT NULL,
    coefficient_qualite DECIMAL(5,2) NOT NULL
);

-- Create Labor table (inherits from Components)
CREATE TABLE labor (
    id BIGINT PRIMARY KEY REFERENCES components(id),
    taux_horaire DECIMAL(10,2) NOT NULL,
    heures_travail DECIMAL(10,2) NOT NULL,
    productivite_ouvrier DECIMAL(5,2) NOT NULL
);

-- Create Project_Components junction table
CREATE TABLE project_components (
    project_id BIGINT REFERENCES projects(id),
    component_id BIGINT REFERENCES components(id),
    quantity DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (project_id, component_id)
);

-- Create Quotes table
CREATE TABLE quotes (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    project_id BIGINT REFERENCES projects(id),
    total_cost DECIMAL(10,2) NOT NULL,
    issue_date DATE NOT NULL,
    validity_date DATE NOT NULL,
    content TEXT NOT NULL
);

-- Insert sample data
INSERT INTO clients (name, email, phone_number, address, is_professional, discount_rate) VALUES
    ('John Doe', 'john@example.com', '1234567890', '123 Main St, City', false, 0.0),
    ('Jane Smith', 'jane@company.com', '0987654321', '456 Oak Ave, Town', true, 0.1);

INSERT INTO projects (name, surface, start_date, status, profit_margin, total_cost, client_id) VALUES
    ('Kitchen Renovation', 20.5, '2023-06-01', 'EN_COURS', 15.0, 0.0, 1),
    ('Office Kitchen Remodel', 35.0, '2023-07-15', 'EN_ATTENTE', 20.0, 0.0, 2);

INSERT INTO components (name, type, taux_tva) VALUES
    ('Wooden Cabinet', 'MATERIAL', 20.0),
    ('Granite Countertop', 'MATERIAL', 20.0),
    ('Plumbing Installation', 'LABOR', 20.0),
    ('Electrical Wiring', 'LABOR', 20.0);

INSERT INTO materials (id, cout_unitaire, quantite, cout_transport, coefficient_qualite) VALUES
    (1, 200.00, 5, 50.00, 1.2),
    (2, 500.00, 2, 100.00, 1.5);

INSERT INTO labor (id, taux_horaire, heures_travail, productivite_ouvrier) VALUES
    (3, 50.00, 8, 1.2),
    (4, 60.00, 6, 1.1);

INSERT INTO project_components (project_id, component_id, quantity) VALUES
    (1, 1, 3),
    (1, 2, 1),
    (1, 3, 1),
    (1, 4, 1),
    (2, 1, 5),
    (2, 2, 2),
    (2, 3, 1),
    (2, 4, 1);

    ALTER TABLE materials ALTER COLUMN cout_unitaire TYPE NUMERIC(10, 2);