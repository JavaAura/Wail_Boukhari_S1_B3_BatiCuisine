package com.baticuisine.service;

import java.util.logging.Logger;

import com.baticuisine.model.Labor;
import com.baticuisine.model.Material;
import com.baticuisine.model.Project;

public class QuoteGenerator {
    private static final Logger LOGGER = Logger.getLogger(QuoteGenerator.class.getName());
    private final CostCalculator costCalculator;

    public QuoteGenerator(CostCalculator costCalculator) {
        this.costCalculator = costCalculator;
    }

    public String generateQuote(Project project) {
        LOGGER.info("Generating quote for project: " + project.getName());
        StringBuilder quote = new StringBuilder();
        quote.append("Devis pour le projet: ").append(project.getName()).append("\n\n");

        quote.append("Détails du projet:\n");
        quote.append("Surface: ").append(project.getSurface()).append(" m²\n");
        quote.append("Date de début: ").append(project.getStartDate()).append("\n");
        quote.append("Statut: ").append(project.getStatus()).append("\n\n");

        quote.append("Matériaux:\n");
        for (Material material : project.getMaterials()) {
            quote.append("- ").append(material.getName())
                 .append(": ").append(material.getQuantity())
                 .append(" ").append(material.getUnit())
                 .append(" x ").append(material.getUnitPrice())
                 .append("€ = ").append(material.getQuantity() * material.getUnitPrice())
                 .append("€\n");
        }

        quote.append("\nMain d'œuvre:\n");
        for (Labor labor : project.getLaborItems()) {
            quote.append("- ").append(labor.getDescription())
                 .append(": ").append(labor.getHours())
                 .append("h x ").append(labor.getHourlyRate())
                 .append("€/h = ").append(labor.getCost())
                 .append("€\n");
        }

        double totalCost = costCalculator.calculateTotalCost(project);
        quote.append("\nCoût total du projet: ").append(totalCost).append("€\n");

        double costPerSquareMeter = costCalculator.calculateCostPerSquareMeter(project);
        quote.append("Coût par mètre carré: ").append(costPerSquareMeter).append("€/m²\n");

        return quote.toString();
    }
}
