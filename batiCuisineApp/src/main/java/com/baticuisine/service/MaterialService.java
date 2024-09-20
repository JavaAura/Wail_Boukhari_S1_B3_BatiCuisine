package com.baticuisine.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.baticuisine.model.Material;
import com.baticuisine.repository.MaterialRepository;

public class MaterialService {
    private static final Logger LOGGER = Logger.getLogger(MaterialService.class.getName());
    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public void createMaterial(Material material) {
        materialRepository.save(material);
        LOGGER.info("Material created: " + material.getName());
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public Optional<Material> getMaterialByName(String name) {
        return materialRepository.findByName(name);
    }

    public void updateMaterial(Material material) {
        materialRepository.update(material);
        LOGGER.info("Material updated: " + material.getName());
    }

    public void deleteMaterial(String materialName) {
        materialRepository.delete(materialName);
        LOGGER.info("Material deleted: " + materialName);
    }

    public double calculateTotalCost(List<Material> materials) {
        return materials.stream()
                .mapToDouble(Material::calculateCost)
                .sum();
    }
}