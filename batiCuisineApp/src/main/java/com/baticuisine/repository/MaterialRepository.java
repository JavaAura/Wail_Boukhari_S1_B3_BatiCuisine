package com.baticuisine.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.baticuisine.model.Material;

public class MaterialRepository {
    private static final Logger LOGGER = Logger.getLogger(MaterialRepository.class.getName());
    private final Connection connection;

    public MaterialRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Material material) {
        String sql = "INSERT INTO materials (id, name, unit_price, unit, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, material.getId());
            pstmt.setString(2, material.getName());
            pstmt.setDouble(3, material.getUnitPrice());
            pstmt.setString(4, material.getUnit());
            pstmt.setString(5, material.getType());
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
        String sql = "UPDATE materials SET unit_price = ?, unit = ?, type = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, material.getUnitPrice());
            pstmt.setString(2, material.getUnit());
            pstmt.setString(3, material.getType());
            pstmt.setObject(4, material.getId());
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
            (UUID) rs.getObject("id"),
            rs.getString("name"),
            rs.getDouble("unit_price"),
            rs.getString("unit"),
            String.valueOf(rs.getString("type"))
        );
    }
}
