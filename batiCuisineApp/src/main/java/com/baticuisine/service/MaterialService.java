package com.baticuisine.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.baticuisine.model.Component;
import com.baticuisine.model.Material;
import com.baticuisine.repository.ComponentRepository;

public class MaterialService {
    private static final Logger LOGGER = Logger.getLogger(MaterialService.class.getName());
    private final ComponentRepository componentRepository;

    public MaterialService(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    public Material createMaterial(Material material) {
        try {
            Component savedComponent = componentRepository.save(material);
            if (savedComponent instanceof Material) {
                LOGGER.info("Material created: " + savedComponent.getName());
                return (Material) savedComponent;
            } else {
                throw new RuntimeException("Saved component is not a Material");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating material", e);
            throw new RuntimeException("Failed to create material", e);
        }
    }

    public List<Material> getAllMaterials() {
        try {
            return componentRepository.findAll().stream()
                .filter(c -> c instanceof Material)
                .map(c -> (Material) c)
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all materials", e);
            throw new RuntimeException("Failed to retrieve materials", e);
        }
    }

    public Optional<Material> getMaterialByName(String name) {
        try {
            return componentRepository.findAll().stream()
                .filter(c -> c instanceof Material && c.getName().equals(name))
                .map(c -> (Material) c)
                .findFirst();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving material by name: " + name, e);
            throw new RuntimeException("Failed to retrieve material", e);
        }
    }

    public void updateMaterial(Material material) {
        try {
            componentRepository.update(material);
            LOGGER.info("Material updated: " + material.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating material", e);
            throw new RuntimeException("Failed to update material", e);
        }
    }

    public void deleteMaterial(Long id) {
        try {
            componentRepository.delete(id);
            LOGGER.info("Material deleted with id: " + id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting material with id: " + id, e);
            throw new RuntimeException("Failed to delete material", e);
        }
    }

    public double calculateTotalCost(List<Material> materials) {
        return materials.stream()
                .mapToDouble(Material::calculateCost)
                .sum();
    }
}