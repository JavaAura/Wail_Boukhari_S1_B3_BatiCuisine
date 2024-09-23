package com.baticuisine.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public List<Quote> getQuotesByProjectId(Long projectId) {
        return quoteRepository.findByProjectId(projectId);
    }

    public Quote generateQuote(Project project) {
        double totalCost = costCalculator.calculateTotalCost(project);
        LocalDate issueDate = LocalDate.now();
        LocalDate validityDate = issueDate.plusMonths(1); // Quote valid for 1 month

        Quote quote = new Quote(totalCost, issueDate, validityDate, project);
        quote.setContent(generateQuoteContent(project, totalCost));

        return quote;
    }

    private String generateQuoteContent(Project project, double totalCost) {
        StringBuilder content = new StringBuilder();
        appendProjectDetails(content, project);
        appendMaterialDetails(content, project);
        appendLaborDetails(content, project);
        appendTotalCost(content, totalCost);
        return content.toString();
    }

    private void appendProjectDetails(StringBuilder content, Project project) {
        content.append("Quote for Project: ").append(project.getProjectName()).append("\n");
        content.append("Client: ").append(project.getClient().getName()).append("\n");
        content.append("Surface: ").append(String.format("%.2f", project.getSurface())).append(" m²\n");
        content.append("Start date: ").append(project.getStartDate()).append("\n");
        content.append("Status: ").append(project.getProjectStatus()).append("\n\n");
    }

    private void appendMaterialDetails(StringBuilder content, Project project) {
        content.append("Materials:\n");
        for (Material material : project.getMaterials()) {
            content.append("- ").append(material.getName())
                    .append(": ").append(String.format("%.2f", material.getQuantity()))
                    .append(" ").append(material.getComponentType())
                    .append(" x ").append(String.format("%.2f", material.getUnitCost()))
                    .append("€ = ").append(String.format("%.2f", material.calculateCost()))
                    .append("€\n");
        }
    }

    private void appendLaborDetails(StringBuilder content, Project project) {
        content.append("\nLabor:\n");
        for (Labor labor : project.getLaborItems()) {
            content.append("- ").append(labor.getName())
                    .append(": ").append(String.format("%.2f", labor.getHoursWorked()))
                    .append(" hours x ").append(String.format("%.2f", labor.getHourlyRate()))
                    .append("€/h = ").append(String.format("%.2f", labor.calculateCost()))
                    .append("€\n");
        }
    }

    private void appendTotalCost(StringBuilder content, double totalCost) {
        content.append("\nTotal Cost: ").append(String.format("%.2f", totalCost)).append("€\n");
    }

    public Optional<Quote> saveQuote(Quote quote) {
        try {
            Quote savedQuote = quoteRepository.save(quote);
            LOGGER.info("Quote saved: " + savedQuote.getId());
            return Optional.of(savedQuote);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving quote", e);
            return Optional.empty();
        }
    }

    public Optional<Quote> getQuoteById(Long id) {
        return quoteRepository.findById(id);
    }

    public void updateQuote(Quote quote) {
        quoteRepository.update(quote);
    }
}