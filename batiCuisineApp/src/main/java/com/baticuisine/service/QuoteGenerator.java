package com.baticuisine.service;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Labor;
import com.baticuisine.model.Material;
import com.baticuisine.model.Project;
import com.baticuisine.model.Quote;
import com.baticuisine.repository.QuoteRepository;

public class QuoteGenerator {
    private static final Logger LOGGER = Logger.getLogger(QuoteGenerator.class.getName());
    private final CostCalculator costCalculator;
    private final QuoteRepository quoteRepository;

    public QuoteGenerator(CostCalculator costCalculator, QuoteRepository quoteRepository) {
        this.costCalculator = costCalculator;
        this.quoteRepository = quoteRepository;
    }

    public Quote generateQuote(Project project) {
        LOGGER.info("Generating quote for project: " + project.getName());
        try {
            StringBuilder quoteContent = new StringBuilder();
            quoteContent.append("Devis pour le projet: ").append(project.getName()).append("\n\n");

            appendProjectDetails(quoteContent, project);
            appendMaterialDetails(quoteContent, project);
            appendLaborDetails(quoteContent, project);
            appendCostSummary(quoteContent, project);

            double totalCost = costCalculator.calculateTotalCost(project);
            LocalDate issueDate = LocalDate.now();
            LocalDate validityDate = issueDate.plusMonths(1); // Set validity to 1 month from issue date

            Quote quote = new Quote(project, totalCost, issueDate, validityDate);
            quote.setContent(quoteContent.toString());

            quoteRepository.saveQuote(quote);
            LOGGER.info("Quote generated and saved for project: " + project.getName());

            return quote;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating quote for project: " + project.getName(), e);
            throw new RuntimeException("Failed to generate quote", e);
        }
    }

    private void appendProjectDetails(StringBuilder quote, Project project) {
        quote.append("Détails du projet:\n");
        quote.append("Surface: ").append(String.format("%.2f", project.getSurface())).append(" m²\n");
        quote.append("Date de début: ").append(project.getStartDate()).append("\n");
        quote.append("Statut: ").append(project.getStatus()).append("\n\n");
    }

    private void appendMaterialDetails(StringBuilder quote, Project project) {
        quote.append("Matériaux:\n");
        for (Material material : project.getMaterials()) {
            quote.append("- ").append(material.getName())
                 .append(": ").append(String.format("%.2f", material.getQuantity()))
                 .append(" ").append(material.getUnit())
                 .append(" x ").append(String.format("%.2f", material.getUnitPrice()))
                 .append("€ = ").append(String.format("%.2f", material.getQuantity() * material.getUnitPrice()))
                 .append("€\n");
        }
    }

    private void appendLaborDetails(StringBuilder quote, Project project) {
        quote.append("\nMain d'œuvre:\n");
        for (Labor labor : project.getLaborItems()) {
            quote.append("- ").append(labor.getDescription())
                 .append(": ").append(String.format("%.2f", labor.getHours()))
                 .append("h x ").append(String.format("%.2f", labor.getHourlyRate()))
                 .append("€/h = ").append(String.format("%.2f", labor.getCost()))
                 .append("€\n");
        }
    }

    private void appendCostSummary(StringBuilder quote, Project project) {
        double totalCost = costCalculator.calculateTotalCost(project);
        double costPerSquareMeter = costCalculator.calculateCostPerSquareMeter(project);
        quote.append("\nCoût total du projet: ").append(String.format("%.2f", totalCost)).append("€\n");
        quote.append("Coût par mètre carré: ").append(String.format("%.2f", costPerSquareMeter)).append("€/m²\n");
    }

}
