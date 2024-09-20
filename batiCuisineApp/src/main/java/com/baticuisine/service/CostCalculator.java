package com.baticuisine.service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Labor;
import com.baticuisine.model.Project;

public class CostCalculator {
    private static final Logger LOGGER = Logger.getLogger(CostCalculator.class.getName());
    private final MaterialService materialService;

    public CostCalculator(MaterialService materialService) {
        this.materialService = materialService;
    }

    public double calculateTotalCost(Project project) {
        LOGGER.info("Calculating total cost for project: " + project.getName());
        try {
            double materialCost = calculateMaterialCost(project);
            double laborCost = calculateLaborCost(project);
            return materialCost + laborCost;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total cost for project: " + project.getName(), e);
            throw new RuntimeException("Failed to calculate total cost", e);
        }
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
        try {
            double totalCost = calculateTotalCost(project);
            double surface = project.getSurface();
            if (surface <= 0) {
                LOGGER.warning("Invalid surface area for project: " + project.getName());
                return 0;
            }
            return totalCost / surface;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating cost per square meter for project: " + project.getName(), e);
            throw new RuntimeException("Failed to calculate cost per square meter", e);
        }
    }

    public Map<String, Double> calculateCostBreakdown(Project project) {
        try {
            double materialCost = calculateMaterialCost(project);
            double laborCost = calculateLaborCost(project);
            Map<String, Double> breakdown = new HashMap<>();
            breakdown.put("Material", materialCost);
            breakdown.put("Labor", laborCost);
            return breakdown;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating cost breakdown for project: " + project.getName(), e);
            throw new RuntimeException("Failed to calculate cost breakdown", e);
        }
    }
}
