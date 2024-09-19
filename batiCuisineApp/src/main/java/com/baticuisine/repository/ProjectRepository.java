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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Project;
import com.baticuisine.model.enums.ProjectStatus;

public class ProjectRepository {
    private static final Logger LOGGER = Logger.getLogger(ProjectRepository.class.getName());
    private final Connection connection;

    public ProjectRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Project project) {
        String sql = "INSERT INTO projects (id, name, surface, start_date, status, client_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, project.getId());
            pstmt.setString(2, project.getName());
            pstmt.setDouble(3, project.getSurface());
            pstmt.setDate(4, Date.valueOf(project.getStartDate()));
            pstmt.setString(5, project.getStatus().name());
            pstmt.setObject(6, project.getClient() != null ? project.getClient().getId() : null);
            pstmt.executeUpdate();
            LOGGER.info("Project saved: " + project.getName());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving project", e);
            throw new RuntimeException("Error saving project", e);
        }
    }

    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all projects", e);
            throw new RuntimeException("Error finding all projects", e);
        }
        return projects;
    }

    public Optional<Project> findByName(String name) {
        String sql = "SELECT * FROM projects WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProject(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding project by name", e);
            throw new RuntimeException("Error finding project by name", e);
        }
        return Optional.empty();
    }

    public void update(Project project) {
        String sql = "UPDATE projects SET surface = ?, start_date = ?, status = ?, client_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, project.getSurface());
            pstmt.setDate(2, Date.valueOf(project.getStartDate()));
            pstmt.setString(3, project.getStatus().name());
            pstmt.setObject(4, project.getClient() != null ? project.getClient().getId() : null);
            pstmt.setObject(5, project.getId());
            pstmt.executeUpdate();
            LOGGER.info("Project updated: " + project.getName());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating project", e);
            throw new RuntimeException("Error updating project", e);
        }
    }

    public void delete(String projectName) {
        String sql = "DELETE FROM projects WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, projectName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Project deleted: " + projectName);
            } else {
                LOGGER.warning("Project not found for deletion: " + projectName);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting project", e);
            throw new RuntimeException("Error deleting project", e);
        }
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project(
            rs.getString("name"),
            rs.getDouble("surface"),
            rs.getDate("start_date").toLocalDate(),
            ProjectStatus.valueOf(rs.getString("status"))
        );
        project.setId((UUID) rs.getObject("id"));
        // Note: You'll need to fetch and set the client, materials, and labor items separately
        return project;
    }
}
