package com.baticuisine.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.baticuisine.model.Client;
import com.baticuisine.model.Labor;
import com.baticuisine.model.Material;
import com.baticuisine.model.Project;
import com.baticuisine.model.enums.ProjectStatus;

public class ProjectRepositoryImpl implements ProjectRepository {
    private static final Logger LOGGER = Logger.getLogger(ProjectRepositoryImpl.class.getName());
    private static ProjectRepositoryImpl instance;
    private final Connection connection;

    private ProjectRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    public static synchronized ProjectRepositoryImpl getInstance(Connection connection) {
        if (instance == null) {
            instance = new ProjectRepositoryImpl(connection);
        }
        return instance;
    }

    @Override
    public Project save(Project project) {
        if (project.getId() != null && findById(project.getId()).isPresent()) {
            return update(project);
        } else {
            return insertProject(project);
        }
    }

    private Project insertProject(Project project) {
        String sql = "INSERT INTO projects (name, profit_margin, total_cost, status, surface, start_date, client_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, project.getProjectName());
            pstmt.setDouble(2, project.getProfitMargin());
            pstmt.setDouble(3, project.getTotalCost());
            pstmt.setString(4, project.getProjectStatus().name());
            pstmt.setDouble(5, project.getSurface());
            pstmt.setDate(6, Date.valueOf(project.getStartDate()));
            if (project.getClient() != null && project.getClient().getId() != null) {
                pstmt.setLong(7, project.getClient().getId());
            } else {
                throw new IllegalArgumentException("Client ID cannot be null");
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating project failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getLong(1));
                    saveMaterialsAndLabor(project); // Save materials and labor
                } else {
                    throw new SQLException("Creating project failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving project", e);
        }
        return project;
    }

    private void saveMaterialsAndLabor(Project project) throws SQLException {
        for (Material material : project.getMaterials()) {
            saveMaterial(material, project.getId());
        }
        for (Labor labor : project.getLaborItems()) {
            saveLabor(labor, project.getId());
        }
    }

    @Override
    public void saveMaterial(Material material, Long projectId) throws SQLException {
        // Validate input values
        if (material.getUnitCost() > 999.99) {
            throw new IllegalArgumentException("Unit cost exceeds the maximum allowed value");
        }
        if (material.getQuantity() > 999.99) {
            throw new IllegalArgumentException("Quantity exceeds the maximum allowed value");
        }
        if (material.getTransportCost() > 999.99) {
            throw new IllegalArgumentException("Transport cost exceeds the maximum allowed value");
        }
        if (material.getQualityCoefficient() > 9.99) {
            throw new IllegalArgumentException("Quality coefficient exceeds the maximum allowed value");
        }

        // Check if the material already exists
        if (material.getId() != null && findMaterialById(material.getId()).isPresent()) {
            updateMaterial(material);
            linkMaterialToProject(material, projectId);
            return;
        }

        // First, insert into the components table
        String componentSql = "INSERT INTO components (name, type, tva_cost) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(componentSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, material.getName());
            pstmt.setString(2, "MATERIAL");
            pstmt.setDouble(3, material.getVatRate());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    material.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating component failed, no ID obtained.");
                }
            }
        }

        // Then, insert into the materials table
        String materialSql = "INSERT INTO materials (id, unit_cost, quantite, transport_cost, coefficient_qualite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(materialSql)) {
            pstmt.setLong(1, material.getId());
            pstmt.setDouble(2, material.getUnitCost());
            pstmt.setDouble(3, material.getQuantity());
            pstmt.setDouble(4, material.getTransportCost());
            pstmt.setDouble(5, material.getQualityCoefficient());
            pstmt.executeUpdate();
        }

        // Finally, link the material to the project in the project_components table
        linkMaterialToProject(material, projectId);
    }

    private void linkMaterialToProject(Material material, Long projectId) throws SQLException {
        String projectComponentSql = "INSERT INTO project_components (project_id, component_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(projectComponentSql)) {
            pstmt.setLong(1, projectId);
            pstmt.setLong(2, material.getId());
            pstmt.setDouble(3, material.getQuantity());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void saveLabor(Labor labor, Long projectId) throws SQLException {
        // Check if the labor already exists
        if (labor.getId() != null && findLaborById(labor.getId()).isPresent()) {
            updateLabor(labor);
            linkLaborToProject(labor, projectId);
            return;
        }

        // First, insert into the components table
        String componentSql = "INSERT INTO components (name, type, tva_cost) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(componentSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, labor.getName());
            pstmt.setString(2, "LABOR");
            pstmt.setDouble(3, labor.getVatRate());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    labor.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating component failed, no ID obtained.");
                }
            }
        }

        // Then, insert into the labor table
        String laborSql = "INSERT INTO labor (id, hourly_rate, work_hours, worker_productivity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(laborSql)) {
            pstmt.setLong(1, labor.getId());
            pstmt.setDouble(2, labor.getHourlyRate());
            pstmt.setDouble(3, labor.getHoursWorked());
            pstmt.setDouble(4, labor.getWorkerProductivity());
            pstmt.executeUpdate();
        }

        // Finally, link the labor to the project in the project_components table
        linkLaborToProject(labor, projectId);
    }

    private void linkLaborToProject(Labor labor, Long projectId) throws SQLException {
        String projectComponentSql = "INSERT INTO project_components (project_id, component_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(projectComponentSql)) {
            pstmt.setLong(1, projectId);
            pstmt.setLong(2, labor.getId());
            pstmt.setDouble(3, labor.getHoursWorked());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Optional<Project> findById(Long id) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding project by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all projects", e);
        }
        return projects;
    }

    @Override
    public Optional<Project> findByName(String name) {
        String sql = "SELECT * FROM projects WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding project by name", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Project> findByStatus(ProjectStatus status) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE status = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding projects by status", e);
        }
        return projects;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project", e);
        }
    }

    @Override
    public Project update(Project project) {
        String sql = "UPDATE projects SET name = ?, profit_margin = ?, total_cost = ?, status = ?, surface = ?, start_date = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, project.getProjectName());
            pstmt.setDouble(2, project.getProfitMargin());
            pstmt.setDouble(3, project.getTotalCost());
            pstmt.setString(4, project.getProjectStatus().name());
            pstmt.setDouble(5, project.getSurface());
            pstmt.setDate(6, Date.valueOf(project.getStartDate()));
            pstmt.setLong(7, project.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating project failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project", e);
        }
        return findById(project.getId()).orElseThrow(() -> new RuntimeException("Updated project not found"));
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project(
                rs.getString("name"),
                rs.getDouble("surface"),
                rs.getDate("start_date").toLocalDate(),
                ProjectStatus.fromDbValue(rs.getString("status")),
                null);
        project.setId(rs.getLong("id"));
        project.setProfitMargin(rs.getDouble("profit_margin"));
        project.setTotalCost(rs.getDouble("total_cost"));

        // Retrieve and set materials
        project.setMaterials(findMaterialsByProjectId(project.getId()));

        // Retrieve and set labor items
        project.setLaborItems(findLaborByProjectId(project.getId()));

        // Retrieve and set client
        project.setClient(findClientById(rs.getLong("client_id")));

        return project;
    }

    private List<Material> findMaterialsByProjectId(Long projectId) throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.tva_cost, m.unit_cost, m.quantite, m.transport_cost, m.coefficient_qualite "
                +
                "FROM components c " +
                "JOIN materials m ON c.id = m.id " +
                "JOIN project_components pc ON c.id = pc.component_id " +
                "WHERE pc.project_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Material material = new Material(
                        rs.getString("name"),
                        rs.getDouble("tva_cost"),
                        rs.getDouble("unit_cost"),
                        rs.getDouble("quantite"),
                        rs.getDouble("transport_cost"),
                        rs.getDouble("coefficient_qualite"));
                material.setId(rs.getLong("id"));
                materials.add(material);
            }
        }
        return materials;
    }

    private List<Labor> findLaborByProjectId(Long projectId) throws SQLException {
        List<Labor> laborItems = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.tva_cost, l.hourly_rate, l.work_hours, l.worker_productivity " +
                "FROM components c " +
                "JOIN labor l ON c.id = l.id " +
                "JOIN project_components pc ON c.id = pc.component_id " +
                "WHERE pc.project_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Labor labor = new Labor(
                        rs.getString("name"),
                        rs.getDouble("tva_cost"),
                        rs.getDouble("hourly_rate"),
                        rs.getDouble("work_hours"),
                        rs.getDouble("worker_productivity"));
                labor.setId(rs.getLong("id"));
                laborItems.add(labor);
            }
        }
        return laborItems;
    }

    private Client findClientById(Long clientId) throws SQLException {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Client(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getBoolean("is_professional"),
                        rs.getDouble("discount_rate"));
            }
        }
        return null;
    }

    private Optional<Material> findMaterialById(Long id) throws SQLException {
        String sql = "SELECT * FROM materials WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMaterial(rs));
            }
        }
        return Optional.empty();
    }

    private Optional<Labor> findLaborById(Long id) throws SQLException {
        String sql = "SELECT * FROM labor WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToLabor(rs));
            }
        }
        return Optional.empty();
    }

    private Material mapResultSetToMaterial(ResultSet rs) throws SQLException {
        Material material = new Material(
            rs.getString("name"),
            rs.getDouble("tva_cost"),
            rs.getDouble("unit_cost"),
            rs.getDouble("quantite"),
            rs.getDouble("transport_cost"),
            rs.getDouble("coefficient_qualite")
        );
        material.setId(rs.getLong("id"));
        return material;
    }

    private Labor mapResultSetToLabor(ResultSet rs) throws SQLException {
        Labor labor = new Labor(
            rs.getString("name"),
            rs.getDouble("tva_cost"),
            rs.getDouble("hourly_rate"),
            rs.getDouble("work_hours"),
            rs.getDouble("worker_productivity")
        );
        labor.setId(rs.getLong("id"));
        return labor;
    }

    private void updateMaterial(Material material) throws SQLException {
        String sql = "UPDATE materials SET unit_cost= ?, quantite = ?, transport_cost = ?, coefficient_qualite = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, material.getUnitCost());
            pstmt.setDouble(2, material.getQuantity());
            pstmt.setDouble(3, material.getTransportCost());
            pstmt.setDouble(4, material.getQualityCoefficient());
            pstmt.setLong(5, material.getId());
            pstmt.executeUpdate();
        }
    }

    private void updateLabor(Labor labor) throws SQLException {
        String sql = "UPDATE labor SET hourly_rate = ?, work_hours = ?, worker_productivity = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, labor.getHourlyRate());
            pstmt.setDouble(2, labor.getHoursWorked());
            pstmt.setDouble(3, labor.getWorkerProductivity());
            pstmt.setLong(4, labor.getId());
            pstmt.executeUpdate();
        }
    }
}