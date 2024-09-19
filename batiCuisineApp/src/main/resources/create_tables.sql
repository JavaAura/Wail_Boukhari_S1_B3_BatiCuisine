-- Create enum types for better data integrity
CREATE TYPE project_status AS ENUM ('EN_COURS', 'TERMINE', 'ANNULE');
CREATE TYPE material_type AS ENUM ('APPLIANCE', 'CABINET', 'COUNTERTOP', 'PLUMBING', 'ELECTRICAL', 'FLOORING', 'PAINT', 'HARDWARE', 'OTHER');

-- Create clients table
CREATE TABLE clients (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL
);

-- Create projects table
CREATE TABLE projects (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    surface DECIMAL(10, 2) NOT NULL,
    start_date DATE NOT NULL,
    status project_status NOT NULL,
    client_id UUID,
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- Create materials table
CREATE TABLE materials (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    type material_type NOT NULL
);

-- Create project_materials table (for many-to-many relationship)
CREATE TABLE project_materials (
    project_id UUID,
    material_id UUID,
    quantity DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (project_id, material_id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (material_id) REFERENCES materials(id)
);

-- Create labor table
CREATE TABLE labor (
    id UUID PRIMARY KEY,
    project_id UUID,
    description VARCHAR(200) NOT NULL,
    hours DECIMAL(10, 2) NOT NULL,
    hourly_rate DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id)
);

-- Create indexes for better performance
CREATE INDEX idx_projects_client_id ON projects(client_id);
CREATE INDEX idx_project_materials_project_id ON project_materials(project_id);
CREATE INDEX idx_project_materials_material_id ON project_materials(material_id);
CREATE INDEX idx_labor_project_id ON labor(project_id);