package com.baticuisine.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.baticuisine.model.Labor;
import com.baticuisine.model.Material;
import com.baticuisine.model.Project;
import com.baticuisine.model.enums.ProjectStatus;

public interface ProjectRepository {
    Project save(Project project);

    Optional<Project> findById(Long id);

    List<Project> findAll();

    Optional<Project> findByName(String name);

    void delete(Long id);

    List<Project> findByStatus(ProjectStatus status);

    Project update(Project project);

    void saveMaterial(Material material, Long projectId) throws SQLException;

    void saveLabor(Labor labor, Long projectId) throws SQLException;
}
