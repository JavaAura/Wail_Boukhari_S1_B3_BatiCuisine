package com.baticuisine.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Labor;
import com.baticuisine.model.Material;
import com.baticuisine.model.Project;
import com.baticuisine.model.enums.ProjectStatus;
import com.baticuisine.repository.ComponentRepository;
import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.utils.DateUtils;

public class ProjectService {
    private static final Logger LOGGER = Logger.getLogger(ProjectService.class.getName());
    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;
    private final DateUtils dateUtils;
    private final CostCalculator costCalculator;

    public ProjectService(ProjectRepository projectRepository, DateUtils dateUtils, ComponentRepository componentRepository, CostCalculator costCalculator) {
        this.projectRepository = projectRepository;
        this.dateUtils = dateUtils;
        this.componentRepository = componentRepository;
        this.costCalculator = costCalculator;
    }

    public Optional<Project> createProject(Project project) {
        try {
            if (project.getClient() == null || project.getClient().getId() == null) {
                throw new IllegalArgumentException("Client must be set and have a valid ID before creating a project");
            }
            Project createdProject = projectRepository.save(project);
            return Optional.of(createdProject);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating project", e);
            return Optional.empty();
        }
    }

    public Project updateProject(Project project) {
        try {
            projectRepository.update(project);
            return project;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating project", e);
            throw new RuntimeException("Failed to update project", e);
        }
    }

    public List<Project> getAllProjects() {
        try {
            return projectRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all projects", e);
            throw new RuntimeException("Failed to retrieve projects", e);
        }
    }

    public List<Project> getProjectsByStatus(ProjectStatus status) {
        try {
            return projectRepository.findByStatus(status);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving projects by status", e);
            throw new RuntimeException("Failed to retrieve projects", e);
        }
    }

    public Project calculateTotalCost(Long projectId) {
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                double totalCost = costCalculator.calculateTotalCost(project);
                project.setTotalCost(totalCost);
                Project updatedProject = projectRepository.update(project);
                LOGGER.info("Total cost calculated and saved for project: " + projectId);
                return updatedProject;
            } else {
                throw new IllegalArgumentException("Project not found with id: " + projectId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total cost for project: " + projectId, e);
            throw new RuntimeException("Failed to calculate total cost", e);
        }
    }
    public Optional<Project> getProjectByName(String name) {
        try {
            return projectRepository.findByName(name);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving project by name: " + name, e);
            throw new RuntimeException("Failed to retrieve project by name", e);
        }
    }
    
    public void deleteProject(Long id) {
        try {
            projectRepository.delete(id);
            LOGGER.info("Project deleted with id: " + id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting project with id: " + id, e);
            throw new RuntimeException("Failed to delete project", e);
        }
    }

    public void saveMaterial(Material material, Long projectId) throws SQLException {
        projectRepository.saveMaterial(material, projectId);
    }

    public void saveLabor(Labor labor, Long projectId) throws SQLException {
        projectRepository.saveLabor(labor, projectId);
    }
}