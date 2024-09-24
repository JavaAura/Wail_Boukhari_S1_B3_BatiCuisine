package com.baticuisine.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baticuisine.database.DatabaseConnection;
import com.baticuisine.model.Component;
import com.baticuisine.model.Labor;
import com.baticuisine.model.Material;

public class ComponentRepositoryImpl implements ComponentRepository {
    private static ComponentRepositoryImpl instance;
    private final Connection connection;
    private final Map<String, List<Component>> componentsByType = new HashMap<>();

    private ComponentRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public static synchronized ComponentRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ComponentRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Component save(Component component) {
        String sql;
        if (component instanceof Material) {
            sql = "INSERT INTO components (name, component_type, vat_rate, unit_cost, quantity, transport_cost, quality_coefficient) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO components (name, component_type, vat_rate, hourly_rate, hours_worked, worker_productivity) VALUES (?, ?, ?, ?, ?, ?)";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, component.getName());
            pstmt.setString(2, component.getComponentType());
            pstmt.setDouble(3, component.getVatRate());

            if (component instanceof Material) {
                Material material = (Material) component;
                pstmt.setDouble(4, material.getUnitCost());
                pstmt.setDouble(5, material.getQuantity());
                pstmt.setDouble(6, material.getTransportCost());
                pstmt.setDouble(7, material.getQualityCoefficient());
            } else if (component instanceof Labor) {
                Labor labor = (Labor) component;
                pstmt.setDouble(4, labor.getHourlyRate());
                pstmt.setDouble(5, labor.getHoursWorked());
                pstmt.setDouble(6, labor.getWorkerProductivity());
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating component failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    component.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating component failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving component", e);
        }
        return component;
    }

    @Override
    public Optional<Component> findById(Long id) {
        String sql = "SELECT * FROM components WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToComponent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding component by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Component> findAll() {
        List<Component> components = new ArrayList<>();
        String sql = "SELECT * FROM components";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                components.add(mapResultSetToComponent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all components", e);
        }
        return components;
    }

    @Override
    public List<Component> findByType(String type) {
        if (componentsByType.containsKey(type)) {
            return componentsByType.get(type);
        }
 
        List<Component> components = new ArrayList<>();
        String sql = "SELECT * FROM components WHERE component_type = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Component component = mapResultSetToComponent(rs);
                components.add(component);
            }
            componentsByType.put(type, components);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding components by type", e);
        }
        return components;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM components WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting component", e);
        }
    }

    @Override
    public void update(Component component) {
        String sql;
        if (component instanceof Material) {
            sql = "UPDATE components SET name = ?, component_type = ?, vat_rate = ?, unit_cost = ?, quantity = ?, transport_cost = ?, quality_coefficient = ? WHERE id = ?";
        } else {
            sql = "UPDATE components SET name = ?, component_type = ?, vat_rate = ?, hourly_rate = ?, hours_worked = ?, worker_productivity = ? WHERE id = ?";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, component.getName());
            pstmt.setString(2, component.getComponentType());
            pstmt.setDouble(3, component.getVatRate());

            if (component instanceof Material) {
                Material material = (Material) component;
                pstmt.setDouble(4, material.getUnitCost());
                pstmt.setDouble(5, material.getQuantity());
                pstmt.setDouble(6, material.getTransportCost());
                pstmt.setDouble(7, material.getQualityCoefficient());
                pstmt.setLong(8, material.getId());
            } else if (component instanceof Labor) {
                Labor labor = (Labor) component;
                pstmt.setDouble(4, labor.getHourlyRate());
                pstmt.setDouble(5, labor.getHoursWorked());
                pstmt.setDouble(6, labor.getWorkerProductivity());
                pstmt.setLong(7, labor.getId());
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating component", e);
        }
    }

    private Component mapResultSetToComponent(ResultSet rs) throws SQLException {
        String componentType = rs.getString("component_type");
        if ("Material".equals(componentType)) {
            Material material = new Material(
                rs.getString("name"),
                rs.getDouble("vat_rate"),
                rs.getDouble("unit_cost"),
                rs.getDouble("quantity"),
                rs.getDouble("transport_cost"),
                rs.getDouble("quality_coefficient")
            );
            material.setId(rs.getLong("id"));
            return material;
        } else if ("Labor".equals(componentType)) {
            Labor labor = new Labor(
                rs.getString("name"),
                rs.getDouble("vat_rate"),
                rs.getDouble("hourly_rate"),
                rs.getDouble("hours_worked"),
                rs.getDouble("worker_productivity")
            );
            labor.setId(rs.getLong("id"));
            return labor;
        } else {
            throw new IllegalStateException("Unknown component type: " + componentType);
        }
    }
}