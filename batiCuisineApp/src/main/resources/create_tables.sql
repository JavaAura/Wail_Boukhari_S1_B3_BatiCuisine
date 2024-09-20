-- Drop existing tables if they exist
DROP TABLE IF EXISTS quotes;
DROP TABLE IF EXISTS project_labor;
DROP TABLE IF EXISTS project_materials;
DROP TABLE IF EXISTS labor;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS clients;

-- Create tables
CREATE TABLE clients (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE projects (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    surface DECIMAL(10, 2) NOT NULL,
    start_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    client_id UUID,
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE materials (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    type VARCHAR(50) NOT NULL,
    vat_rate DECIMAL(5, 2) NOT NULL,
    transport_cost DECIMAL(10, 2) NOT NULL,
    quality_coefficient DECIMAL(5, 2) NOT NULL
);

CREATE TABLE project_materials (
    project_id UUID,
    material_id UUID,
    quantity DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (project_id, material_id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (material_id) REFERENCES materials(id)
);

CREATE TABLE labor (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    hourly_rate DECIMAL(10, 2) NOT NULL
);

CREATE TABLE project_labor (
    project_id UUID,
    labor_id UUID,
    hours DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (project_id, labor_id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (labor_id) REFERENCES labor(id)
);

CREATE TABLE quotes (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    estimated_amount DECIMAL(10, 2) NOT NULL,
    issue_date DATE NOT NULL,
    validity_date DATE NOT NULL,
    accepted BOOLEAN DEFAULT FALSE,
    content TEXT NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id)
);