package com.baticuisine.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Component;
import com.baticuisine.model.Project;
import com.baticuisine.model.enums.ProjectStatus;
import com.baticuisine.repository.ComponentRepository;
import com.baticuisine.repository.ProjectRepository;

public class ProjectService {
    private static final Logger LOGGER = Logger.getLogger(ProjectService.class.getName());
    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;

    public ProjectService(ProjectRepository projectRepository, ComponentRepository componentRepository) {
        this.projectRepository = projectRepository;
        this.componentRepository = componentRepository;
    }

    public Optional<Project> createProject(Project project) {
        try {
            Project savedProject = projectRepository.save(project);
            LOGGER.info("Project created: " + savedProject.getProjectName());
            return Optional.of(savedProject);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating project", e);
            return Optional.empty();
        }
    }

    public Optional<Project> getProjectById(Long id) {
        try {
            return projectRepository.findById(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving project by id: " + id, e);
            throw new RuntimeException("Failed to retrieve project", e);
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
            LOGGER.log(Level.SEVERE, "Error retrieving projects by status: " + status, e);
            throw new RuntimeException("Failed to retrieve projects by status", e);
        }
    }

    public void updateProject(Project project) {
        try {
            projectRepository.update(project);
            LOGGER.info("Project updated: " + project.getProjectName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating project", e);
            throw new RuntimeException("Failed to update project", e);
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

    public void addComponentToProject(Long projectId, Component component) {
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                project.addComponent(component);
                componentRepository.save(component);
                projectRepository.update(project);
                LOGGER.info("Component added to project: " + projectId);
            } else {
                throw new IllegalArgumentException("Project not found with id: " + projectId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding component to project: " + projectId, e);
            throw new RuntimeException("Failed to add component to project", e);
        }
    }

    public void calculateTotalCost(Long projectId) {
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (projectOpt.isPresent()) {
                Project project = projectOpt.get();
                project.calculateTotalCost();
                projectRepository.update(project);
                LOGGER.info("Total cost calculated for project: " + projectId);
            } else {
                throw new IllegalArgumentException("Project not found with id: " + projectId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total cost for project: " + projectId, e);
            throw new RuntimeException("Failed to calculate total cost", e);
        }
    }
}