package com.baticuisine.repository;

import java.util.List;
import java.util.Optional;

import com.baticuisine.model.Component;

public interface ComponentRepository {
    Component save(Component component);
    Optional<Component> findById(Long id);
    List<Component> findAll();
    List<Component> findByType(String type);
    void delete(Long id);
    void update(Component component);
}