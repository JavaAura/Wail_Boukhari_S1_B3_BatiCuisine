   CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- Insert 10 projects with real data
INSERT INTO projects (id, name, surface, start_date, status, client_id) VALUES
(uuid_generate_v4(), 'Cuisine Moderne Parisienne', 15.5, '2023-06-01', 'EN_COURS', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1)),
(uuid_generate_v4(), 'Rénovation Loft Lyonnais', 22.0, '2023-05-15', 'EN_ATTENTE', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1)),
(uuid_generate_v4(), 'Cuisine Provençale Traditionnelle', 18.5, '2023-07-01', 'PLANIFIE', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1)),
(uuid_generate_v4(), 'Design Minimaliste Bordelais', 12.0, '2023-06-20', 'EN_COURS', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1)),
(uuid_generate_v4(), 'Cuisine Familiale Strasbourgeoise', 25.0, '2023-08-01', 'PLANIFIE', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1)),
(uuid_generate_v4(), 'Réaménagement Studio Parisien', 8.5, '2023-07-15', 'EN_ATTENTE', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1)),
(uuid_generate_v4(), 'Cuisine Rustique Normande', 20.0, '2023-09-01', 'PLANIFIE', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1)),
(uuid_generate_v4(), 'Design Contemporain Marseillais', 17.5, '2023-08-15', 'EN_COURS', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1)),
(uuid_generate_v4(), 'Cuisine Ouverte Nantaise', 14.0, '2023-09-15', 'PLANIFIE', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1)),
(uuid_generate_v4(), 'Rénovation Appartement Haussmannien', 30.0, '2023-10-01', 'EN_ATTENTE', (SELECT id FROM clients ORDER BY RANDOM() LIMIT 1));
-- Insert clients
INSERT INTO clients (id, name, email, phone, address, is_active) VALUES
(uuid_generate_v4(), 'Jean Dupont', 'jean.dupont@email.com', '0123456789', '123 Rue de Paris, 75001 Paris', TRUE),
(uuid_generate_v4(), 'Marie Martin', 'marie.martin@email.com', '0234567890', '456 Avenue de Lyon, 69002 Lyon', TRUE),
(uuid_generate_v4(), 'Pierre Durand', 'pierre.durand@email.com', '0345678901', '789 Boulevard de Marseille, 13001 Marseille', TRUE),
(uuid_generate_v4(), 'Sophie Lefebvre', 'sophie.lefebvre@email.com', '0456789012', '101 Rue de Bordeaux, 33000 Bordeaux', TRUE),
(uuid_generate_v4(), 'Luc Moreau', 'luc.moreau@email.com', '0567890123', '202 Avenue de Strasbourg, 67000 Strasbourg', TRUE);

-- Insert materials
INSERT INTO materials (id, name, unit_price, unit, type, vat_rate, transport_cost, quality_coefficient) VALUES
(uuid_generate_v4(), 'Granite Countertop', 200.00, 'm²', 'COUNTERTOP', 20.0, 50.00, 1.2),
(uuid_generate_v4(), 'Oak Cabinet', 150.00, 'unit', 'CABINET', 20.0, 30.00, 1.1),
(uuid_generate_v4(), 'Stainless Steel Sink', 100.00, 'unit', 'PLUMBING', 20.0, 20.00, 1.0),
(uuid_generate_v4(), 'Ceramic Tile', 30.00, 'm²', 'FLOORING', 20.0, 10.00, 1.0),
(uuid_generate_v4(), 'LED Lighting', 50.00, 'unit', 'ELECTRICAL', 20.0, 5.00, 1.1);

-- Insert labor
INSERT INTO labor (id, name, hourly_rate) VALUES
(uuid_generate_v4(), 'Carpenter', 35.00),
(uuid_generate_v4(), 'Plumber', 40.00),
(uuid_generate_v4(), 'Electrician', 45.00),
(uuid_generate_v4(), 'Tiler', 30.00),
(uuid_generate_v4(), 'Painter', 25.00);
-- Add materials and labor to projects
DO $$
DECLARE
    project_id UUID;
    material_id UUID;
    labor_id UUID;
BEGIN
    FOR project_id IN SELECT id FROM projects LOOP
        -- Add materials to project
        FOR material_id IN SELECT id FROM materials LOOP
            INSERT INTO project_materials (project_id, material_id, quantity)
            VALUES (project_id, material_id, random() * 10 + 1);
        END LOOP;

        -- Add labor to project
        FOR labor_id IN SELECT id FROM labor LOOP
            INSERT INTO project_labor (project_id, labor_id, hours)
            VALUES (project_id, labor_id, random() * 40 + 10);
        END LOOP;
    END LOOP;
END $$;