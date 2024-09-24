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
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    is_professional BOOLEAN NOT NULL,
    discount_rate NUMERIC(5, 2) NOT NULL
);

-- Create Projects table
CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surface NUMERIC(10, 2) NOT NULL,
    start_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    profit_margin NUMERIC(5, 2) NOT NULL,
    total_cost NUMERIC(10, 2) NOT NULL,
    client_id INTEGER REFERENCES clients(id)
);

-- Create Components table
CREATE TABLE components (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    tva_cost NUMERIC(5, 2) NOT NULL
);

-- Create Materials table (inherits from Components)
CREATE TABLE materials (
    id INTEGER PRIMARY KEY REFERENCES components(id),
    unit_cost NUMERIC(10, 2) NOT NULL,
    quantite NUMERIC(10, 2) NOT NULL,
    transport_cost NUMERIC(10, 2) NOT NULL,
    coefficient_qualite NUMERIC(5, 2) NOT NULL
);

-- Create Labor table (inherits from Components)
CREATE TABLE labor (
    id INTEGER PRIMARY KEY REFERENCES components(id),
    hourly_rate NUMERIC(10, 2) NOT NULL,
    work_hours NUMERIC(10, 2) NOT NULL,
    worker_productivity NUMERIC(5, 2) NOT NULL
);

-- Create Project_Components junction table
CREATE TABLE project_components (
    project_id INTEGER REFERENCES projects(id),
    component_id INTEGER REFERENCES components(id),
    quantity NUMERIC(10, 2) NOT NULL,
    PRIMARY KEY (project_id, component_id)
);

-- Create Quotes table
CREATE TABLE quotes (
    id SERIAL PRIMARY KEY,
    project_id INTEGER REFERENCES projects(id),
    total_cost NUMERIC(10, 2) NOT NULL,
    issue_date DATE NOT NULL,
    validity_date DATE NOT NULL,
    content TEXT NOT NULL,
    is_accepted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create indexes
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_phone_number ON clients(phone_number);
CREATE INDEX idx_projects_client_id ON projects(client_id);
CREATE INDEX idx_components_name ON components(name);
CREATE INDEX idx_materials_unit_cost ON materials(unit_cost);
CREATE INDEX idx_labor_hourly_rate ON labor(hourly_rate);
CREATE INDEX idx_project_components_project_id ON project_components(project_id);
CREATE INDEX idx_project_components_component_id ON project_components(component_id);
CREATE INDEX idx_quotes_project_id ON quotes(project_id);

-- Insert sample data
INSERT INTO clients (name, email, phone_number, address, is_professional, discount_rate) VALUES
    ('John Doe', 'john@example.com', '1234567890', '123 Main St, City', false, 0.0),
    ('Jane Smith', 'jane@company.com', '0987654321', '456 Oak Ave, Town', true, 0.1),
    ('Alice Johnson', 'alice@home.com', '1112223333', '789 Pine Rd, Village', false, 0.05),
    ('Bob Brown', 'bob@work.com', '4445556666', '101 Maple St, City', true, 0.15),
    ('Charlie Davis', 'charlie@biz.com', '7778889999', '202 Birch Ln, Town', true, 0.2);

INSERT INTO projects (name, surface, start_date, status, profit_margin, total_cost, client_id) VALUES
    ('Kitchen Renovation', 20.5, '2023-06-01', 'EN_COURS', 15.0, 0.0, 1),
    ('Office Kitchen Remodel', 35.0, '2023-07-15', 'EN_ATTENTE', 20.0, 0.0, 2),
    ('Bathroom Upgrade', 15.0, '2023-08-01', 'EN_COURS', 10.0, 0.0, 3),
    ('Living Room Extension', 50.0, '2023-09-01', 'EN_ATTENTE', 25.0, 0.0, 4),
    ('Garage Conversion', 30.0, '2023-10-01', 'EN_COURS', 18.0, 0.0, 5);

INSERT INTO components (name, type, tva_cost) VALUES
    ('Wooden Cabinet', 'MATERIAL', 20.0),
    ('Granite Countertop', 'MATERIAL', 20.0),
    ('Plumbing Installation', 'LABOR', 20.0),
    ('Electrical Wiring', 'LABOR', 20.0),
    ('Tile Flooring', 'MATERIAL', 20.0),
    ('Paint', 'MATERIAL', 20.0),
    ('Drywall Installation', 'LABOR', 20.0),
    ('HVAC Installation', 'LABOR', 20.0),
    ('Lighting Fixtures', 'MATERIAL', 20.0),
    ('Roofing', 'LABOR', 20.0);

INSERT INTO materials (id, unit_cost, quantite, transport_cost, coefficient_qualite) VALUES
    (1, 200.00, 5, 50.00, 1.2),
    (2, 500.00, 2, 100.00, 1.5),
    (5, 100.00, 10, 20.00, 1.1),
    (6, 50.00, 20, 10.00, 1.0),
    (9, 150.00, 8, 30.00, 1.3);

INSERT INTO labor (id, hourly_rate, work_hours, worker_productivity) VALUES
    (3, 50.00, 8, 1.2),
    (4, 60.00, 6, 1.1),
    (7, 40.00, 10, 1.0),
    (8, 70.00, 5, 1.4),
    (10, 80.00, 4, 1.3);

INSERT INTO project_components (project_id, component_id, quantity) VALUES
    (1, 1, 3),
    (1, 2, 1),
    (1, 3, 1),
    (1, 4, 1),
    (1, 5, 2),
    (2, 1, 5),
    (2, 2, 2),
    (2, 3, 1),
    (2, 4, 1),
    (2, 6, 3),
    (3, 5, 4),
    (3, 6, 5),
    (3, 7, 2),
    (3, 8, 1),
    (3, 9, 3),
    (4, 1, 2),
    (4, 2, 1),
    (4, 3, 1),
    (4, 4, 1),
    (4, 10, 1),
    (5, 5, 3),
    (5, 6, 4),
    (5, 7, 2),
    (5, 8, 1),
    (5, 9, 2);