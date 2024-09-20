package com.baticuisine.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Material;

public class MaterialRepository {
    private static final Logger LOGGER = Logger.getLogger(MaterialRepository.class.getName());
    private final Connection connection;

    public MaterialRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Material material) {
        String sql = "INSERT INTO materials (id, name, cout_unitaire, quantite, taux_tva, cout_transport, coefficient_qualite) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, material.getId());
            pstmt.setString(2, material.getName());
            pstmt.setDouble(3, material.getCoutUnitaire());
            pstmt.setDouble(4, material.getQuantite());
            pstmt.setDouble(5, material.getTauxTVA());
            pstmt.setDouble(6, material.getCoutTransport());
            pstmt.setDouble(7, material.getCoefficientQualite());
            pstmt.executeUpdate();
            LOGGER.info("Material saved: " + material.getName());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving material", e);
            throw new RuntimeException("Error saving material", e);
        }
    }

    public List<Material> findAll() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all materials", e);
            throw new RuntimeException("Error finding all materials", e);
        }
        return materials;
    }

    public Optional<Material> findByName(String name) {
        String sql = "SELECT * FROM materials WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMaterial(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding material by name", e);
            throw new RuntimeException("Error finding material by name", e);
        }
        return Optional.empty();
    }

    public void update(Material material) {
        String sql = "UPDATE materials SET cout_unitaire = ?, quantite = ?, taux_tva = ?, cout_transport = ?, coefficient_qualite = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, material.getCoutUnitaire());
            pstmt.setDouble(2, material.getQuantite());
            pstmt.setDouble(3, material.getTauxTVA());
            pstmt.setDouble(4, material.getCoutTransport());
            pstmt.setDouble(5, material.getCoefficientQualite());
            pstmt.setString(6, material.getName());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Material updated: " + material.getName());
            } else {
                LOGGER.warning("Material not found for update: " + material.getName());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating material", e);
            throw new RuntimeException("Error updating material", e);
        }
    }

    public void delete(String materialName) {
        String sql = "DELETE FROM materials WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, materialName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Material deleted: " + materialName);
            } else {
                LOGGER.warning("Material not found for deletion: " + materialName);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting material", e);
            throw new RuntimeException("Error deleting material", e);
        }
    }

    private Material mapResultSetToMaterial(ResultSet rs) throws SQLException {
        return new Material(
                rs.getString("name"),
                rs.getDouble("cout_unitaire"),
                rs.getDouble("quantite"),
                rs.getDouble("taux_tva"),
                rs.getDouble("cout_transport"),
                rs.getDouble("coefficient_qualite")
        );
    }

    public void addMaterialToProject(UUID projectId, UUID materialId, double quantity) {
        String sql = "INSERT INTO project_materials (project_id, material_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, projectId);
            pstmt.setObject(2, materialId);
            pstmt.setDouble(3, quantity);
            pstmt.executeUpdate();
            LOGGER.info("Material added to project: " + projectId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding material to project", e);
            throw new RuntimeException("Error adding material to project", e);
        }
    }

    public void addLaborToProject(UUID projectId, UUID laborId) {
        String sql = "INSERT INTO project_labor (project_id, labor_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, projectId);
            pstmt.setObject(2, laborId);
            pstmt.executeUpdate();
            LOGGER.info("Labor added to project: " + projectId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding labor to project", e);
            throw new RuntimeException("Error adding labor to project", e);
        }
    }
}
