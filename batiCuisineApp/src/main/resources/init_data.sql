-- Create tables
CREATE TABLE clients (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL
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
    type VARCHAR(20) NOT NULL
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
    project_id UUID,
    description VARCHAR(200) NOT NULL,
    hours DECIMAL(10, 2) NOT NULL,
    rate DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id)
);

-- Create indexes for better performance
CREATE INDEX idx_projects_client_id ON projects(client_id);
CREATE INDEX idx_project_materials_project_id ON project_materials(project_id);
CREATE INDEX idx_project_materials_material_id ON project_materials(material_id);
CREATE INDEX idx_labor_project_id ON labor(project_id);

-- Create enum types for better data integrity
CREATE TYPE project_status AS ENUM ('EN_COURS', 'TERMINE', 'ANNULE');
CREATE TYPE material_type AS ENUM ('APPLIANCE', 'CABINET', 'COUNTERTOP', 'PLUMBING', 'ELECTRICAL', 'FLOORING', 'PAINT', 'HARDWARE', 'OTHER');

-- Alter tables to use enum types
ALTER TABLE projects ALTER COLUMN status TYPE project_status USING status::project_status;
ALTER TABLE materials ALTER COLUMN type TYPE material_type USING type::material_type;