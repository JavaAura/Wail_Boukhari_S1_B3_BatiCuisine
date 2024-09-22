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

import com.baticuisine.model.Project;
import com.baticuisine.model.enums.ProjectStatus;

public class ProjectRepositoryImpl implements ProjectRepository {
    private final Connection connection;

    public ProjectRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Project save(Project project) {
        String sql = "INSERT INTO projects (project_name, profit_margin, total_cost, project_status, surface, start_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, project.getProjectName());
            pstmt.setDouble(2, project.getProfitMargin());
            pstmt.setDouble(3, project.getTotalCost());
            pstmt.setString(4, project.getProjectStatus().name());
            pstmt.setDouble(5, project.getSurface());
            pstmt.setDate(6, Date.valueOf(project.getStartDate()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating project failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating project failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving project", e);
        }
        return project;
    }
    @Override
    public Optional<Project> findByName(String name) {
        String sql = "SELECT * FROM projects WHERE project_name = ?";
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
    public List<Project> findByStatus(ProjectStatus status) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE project_status = ?";
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
        String sql = "UPDATE projects SET project_name = ?, profit_margin = ?, total_cost = ?, project_status = ?, surface = ?, start_date = ? WHERE id = ?";
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
            return project;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project", e);
        }
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project(
            rs.getString("project_name"),
            rs.getDouble("surface"),
            rs.getDate("start_date").toLocalDate(),
            ProjectStatus.valueOf(rs.getString("project_status")),
            null
        );
        project.setId(rs.getLong("id"));
        project.setProfitMargin(rs.getDouble("profit_margin"));
        project.setTotalCost(rs.getDouble("total_cost"));
        return project;
    }
}