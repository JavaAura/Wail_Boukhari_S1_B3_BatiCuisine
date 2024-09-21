-- Drop existing tables if they exist
DROP TABLE IF EXISTS project_components;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS labor;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS components;

-- Create Components table
CREATE TABLE components (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    taux_tva DECIMAL(5,2) NOT NULL
);

-- Create Materials table (inherits from Components)
CREATE TABLE materials (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cout_unitaire DECIMAL(10,2) NOT NULL,
    quantite DECIMAL(10,2) NOT NULL,
    cout_transport DECIMAL(10,2) NOT NULL,
    coefficient_qualite DECIMAL(5,2) NOT NULL
);

-- Create Labor table (inherits from Components)
CREATE TABLE labor (
    id UUID PRIMARY KEY REFERENCES components(id),
    taux_horaire DECIMAL(10,2) NOT NULL,
    heures_travail DECIMAL(10,2) NOT NULL,
    productivite_ouvrier DECIMAL(5,2) NOT NULL
);

-- Create Clients table
CREATE TABLE clients (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    is_professional BOOLEAN NOT NULL
);

-- Create Projects table
CREATE TABLE projects (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surface DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    client_id UUID REFERENCES clients(id)
);

-- Create Project_Components junction table
CREATE TABLE project_components (
    project_id UUID REFERENCES projects(id),
    component_id UUID REFERENCES components(id),
    quantity DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (project_id, component_id)
);
-- Create Quotes table
CREATE TABLE quotes (
    id UUID PRIMARY KEY,
    project_id UUID REFERENCES projects(id),
    total_cost DECIMAL(10,2) NOT NULL,
    issue_date DATE NOT NULL,
    validity_date DATE NOT NULL,
    content TEXT NOT NULL
);
-- Insert sample data
INSERT INTO components (id, name, type, taux_tva) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Wooden Cabinet', 'MATERIAL', 20.0),
    ('22222222-2222-2222-2222-222222222222', 'Granite Countertop', 'MATERIAL', 20.0),
    ('33333333-3333-3333-3333-333333333333', 'Plumbing Installation', 'LABOR', 20.0),
    ('44444444-4444-4444-4444-444444444444', 'Electrical Wiring', 'LABOR', 20.0),
    ('55555555-5555-5555-5555-555555555555', 'Paint', 'MATERIAL', 20.0);

INSERT INTO materials (id, name, cout_unitaire, quantite, cout_transport, coefficient_qualite) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Wood', 200.00, 5, 50.00, 1.2),
    ('22222222-2222-2222-2222-222222222222', 'Steel', 500.00, 2, 100.00, 1.5),
    ('55555555-5555-5555-5555-555555555555', 'Paint', 30.00, 10, 20.00, 1.1);

INSERT INTO labor (id, taux_horaire, heures_travail, productivite_ouvrier) VALUES
    ('33333333-3333-3333-3333-333333333333', 50.00, 8, 1.2),
    ('44444444-4444-4444-4444-444444444444', 60.00, 6, 1.1);

INSERT INTO clients (id, name, email, phone, address, is_professional) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'John Doe', 'john@example.com', '1234567890', '123 Main St, City', false),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Jane Smith', 'jane@company.com', '0987654321', '456 Oak Ave, Town', true);

INSERT INTO projects (id, name, surface, start_date, status, client_id) VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Kitchen Renovation', 20.5, '2023-06-01', 'EN_COURS', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Office Kitchen Remodel', 35.0, '2023-07-15', 'EN_ATTENTE', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

INSERT INTO project_components (project_id, component_id, quantity) VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 3),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 1),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', 1),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '44444444-4444-4444-4444-444444444444', 1),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '55555555-5555-5555-5555-555555555555', 2),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', 5),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', 2),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '33333333-3333-3333-3333-333333333333', 1),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '44444444-4444-4444-4444-444444444444', 1),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '55555555-5555-5555-5555-555555555555', 3);