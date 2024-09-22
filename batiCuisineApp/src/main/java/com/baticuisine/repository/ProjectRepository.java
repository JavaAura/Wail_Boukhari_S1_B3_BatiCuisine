package com.baticuisine.repository;

import java.util.List;
import java.util.Optional;

import com.baticuisine.model.Project;
import com.baticuisine.model.enums.ProjectStatus;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(Long id);
    List<Project> findAll();
    Optional<Project> findByName(String name);
    List<Project> findByStatus(ProjectStatus status);
    void delete(Long id);
    Project update(Project project);
}
