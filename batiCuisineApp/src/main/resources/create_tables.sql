-- Drop existing tables and types if they exist
DROP TABLE IF EXISTS quotes;
DROP TABLE IF EXISTS project_labor;
DROP TABLE IF EXISTS project_materials;
DROP TABLE IF EXISTS labor;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS clients;
DROP TYPE IF EXISTS project_status;
DROP TYPE IF EXISTS material_type;

-- Create types
CREATE TYPE project_status AS ENUM ('EN_COURS', 'EN_ATTENTE', 'TERMINE', 'ANNULE');
CREATE TYPE material_type AS ENUM ('COUNTERTOP', 'CABINET', 'PLUMBING', 'FLOORING', 'ELECTRICAL');

-- Create tables
CREATE TABLE clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20)
);

CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    surface DECIMAL(10, 2),
    start_date DATE,
    status project_status,
    client_id UUID REFERENCES clients(id)
);

CREATE TABLE materials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    cout_unitaire DECIMAL(10, 2) NOT NULL,
    taux_tva DECIMAL(5, 2) NOT NULL,
    cout_transport DECIMAL(10, 2) NOT NULL,
    coefficient_qualite DECIMAL(5, 2) NOT NULL,
    type material_type NOT NULL
);

CREATE TABLE project_materials (
    project_id UUID REFERENCES projects(id),
    material_id UUID REFERENCES materials(id),
    quantite DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (project_id, material_id)
);

CREATE TABLE labor (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    taux_horaire DECIMAL(10, 2) NOT NULL,
    taux_tva DECIMAL(5, 2) NOT NULL
);

CREATE TABLE project_labor (
    project_id UUID REFERENCES projects(id),
    labor_id UUID REFERENCES labor(id),
    heures_travail DECIMAL(10, 2) NOT NULL,
    productivite_ouvrier DECIMAL(5, 2) NOT NULL,
    PRIMARY KEY (project_id, labor_id)
);

CREATE TABLE quotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID REFERENCES projects(id),
    estimated_amount DECIMAL(10, 2) NOT NULL,
    issue_date DATE NOT NULL,
    validity_date DATE NOT NULL,
    content TEXT
);

