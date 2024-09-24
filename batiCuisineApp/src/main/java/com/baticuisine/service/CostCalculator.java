package com.baticuisine.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Client;
import com.baticuisine.model.Material;
import com.baticuisine.model.Project;

public class CostCalculator {
    private static final Logger LOGGER = Logger.getLogger(CostCalculator.class.getName());
    private final MaterialService materialService;
    private static final double DEFAULT_TVA_RATE = 0.20;

    public CostCalculator(MaterialService materialService) {
        this.materialService = materialService;
    }

    public double calculateTotalCost(Project project) {
        try {
            double materialCost = calculateMaterialCost(project);
            double laborCost = calculateLaborCost(project);
            double subtotal = materialCost + laborCost;
            
            Client client = project.getClient();
            double discountRate = (client != null) ? client.getDiscountRate() : 0.0;
            double discountedSubtotal = subtotal * (1 - discountRate);
            
            double totalWithTVA = discountedSubtotal * (1 + DEFAULT_TVA_RATE);
            
            // Round to two decimal places
            totalWithTVA = Math.round(totalWithTVA * 100.0) / 100.0;
            
            project.setTotalCost(totalWithTVA);
            
            return totalWithTVA;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total cost for project: " + project.getProjectName(), e);
            throw new RuntimeException("Failed to calculate total cost", e);
        }
    }

private double calculateMaterialCost(Project project) {
    return project.getMaterials().stream()
            .mapToDouble(Material::calculateCost)
            .sum();
}

private double calculateLaborCost(Project project) {
    return project.getLaborItems().stream()
            .mapToDouble(labor -> labor.getHoursWorked() * labor.getHourlyRate())
            .sum();
}

    public double calculateCostPerSquareMeter(Project project) {
        try {
            double totalCost = calculateTotalCost(project);
            double surface = project.getSurface();
            if (surface <= 0) {
                LOGGER.warning("Invalid surface area for project: " + project.getProjectName());
                return 0;
            }
            return totalCost / surface;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating cost per square meter for project: " + project.getProjectName(), e);
            throw new RuntimeException("Failed to calculate cost per square meter", e);
        }
    }
}
