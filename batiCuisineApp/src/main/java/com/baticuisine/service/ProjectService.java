package com.baticuisine.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.baticuisine.model.Project;
import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.utils.DateUtils;

public class ProjectService {
    private static final Logger LOGGER = Logger.getLogger(ProjectService.class.getName());
    private final ProjectRepository projectRepository;
    private final DateUtils dateUtils;

    public ProjectService(ProjectRepository projectRepository, DateUtils dateUtils) {
        this.projectRepository = projectRepository;
        this.dateUtils = dateUtils;
    }

    public void createProject(Project project) {
        try {
            projectRepository.save(project);
            for (Material material : project.getMaterials()) {
                materialRepository.save(material);
                materialRepository.addMaterialToProject(project.getId(), material.getId(), material.getQuantite());
            }
            for (Labor labor : project.getLaborItems()) {
                laborRepository.save(labor);
                laborRepository.addLaborToProject(project.getId(), labor.getId());
            }
            LOGGER.info("Project created: " + project.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating project", e);
            throw new RuntimeException("Failed to create project", e);
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

    public Optional<Project> getProjectByName(String name) {
        try {
            return projectRepository.findByName(name);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving project by name", e);
            throw new RuntimeException("Failed to retrieve project", e);
        }
    }

    public void updateProject(Project project) {
        try {
            if (!dateUtils.isValidDate(project.getStartDate())) {
                throw new IllegalArgumentException("Invalid start date");
            }
            projectRepository.update(project);
            LOGGER.info("Project updated: " + project.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating project", e);
            throw new RuntimeException("Failed to update project", e);
        }
    }

    public void deleteProject(String projectName) {
        try {
            projectRepository.delete(projectName);
            LOGGER.info("Project deleted: " + projectName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting project", e);
            throw new RuntimeException("Failed to delete project", e);
        }
    }

    public List<Project> getProjectsByStatus(String status) {
        try {
            return getAllProjects().stream()
                    .filter(p -> p.getStatus().name().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving projects by status", e);
            throw new RuntimeException("Failed to retrieve projects by status", e);
        }
    }

    public double calculateTotalSurface() {
        try {
            return getAllProjects().stream()
                    .mapToDouble(Project::getSurface)
                    .sum();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total surface", e);
            throw new RuntimeException("Failed to calculate total surface", e);
        }
    }
}