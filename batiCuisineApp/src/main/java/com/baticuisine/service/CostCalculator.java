package com.baticuisine.service;

import com.baticuisine.model.Project;
import com.baticuisine.model.Material;
import com.baticuisine.model.Labor;

import java.util.logging.Logger;
import java.util.logging.Level;

public class CostCalculator {
    private static final Logger LOGGER = Logger.getLogger(CostCalculator.class.getName());
    private final MaterialService materialService;

    public CostCalculator(MaterialService materialService) {
        this.materialService = materialService;
    }

    public double calculateTotalCost(Project project) {
        LOGGER.info("Calculating total cost for project: " + project.getName());
        double materialCost = calculateMaterialCost(project);
        double laborCost = calculateLaborCost(project);
        return materialCost + laborCost;
    }

    private double calculateMaterialCost(Project project) {
        return materialService.calculateTotalCost(project.getMaterials());
    }

    private double calculateLaborCost(Project project) {
        return project.getLaborItems().stream()
                .mapToDouble(Labor::getCost)
                .sum();
    }

    public double calculateCostPerSquareMeter(Project project) {
        double totalCost = calculateTotalCost(project);
        double surface = project.getSurface();
        if (surface <= 0) {
            LOGGER.warning("Invalid surface area for project: " + project.getName());
            return 0;
        }
        return totalCost / surface;
    }
}
