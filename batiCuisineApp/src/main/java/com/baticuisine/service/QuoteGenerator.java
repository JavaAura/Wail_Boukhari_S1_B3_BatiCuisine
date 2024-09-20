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
        LOGGER.info("Génération du devis pour le projet : " + project.getName());
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
            LOGGER.info("Devis généré et enregistré pour le projet : " + project.getName());    
            return quote;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la génération du devis pour le projet : " + project.getName(), e);
            throw new RuntimeException("Échec de la génération du devis", e);
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
            .append(": ").append(String.format("%.2f", material.getQuantite()))
            .append(" ").append(material.getTypeComposant())
            .append(" x ").append(String.format("%.2f", material.getCoutUnitaire()))
            .append("€ = ").append(String.format("%.2f", material.calculateCost()))
            .append("€\n");
        }
    }

    private void appendLaborDetails(StringBuilder quote, Project project) {
        quote.append("\nMain d'œuvre:\n");
        for (Labor labor : project.getLaborItems()) {
            quote.append("- ").append(labor.getName())
                 .append(": ").append(String.format("%.2f", labor.getHeuresTravail()))
                 .append("h x ").append(String.format("%.2f", labor.getTauxHoraire()))
                 .append("€/h x ").append(String.format("%.2f", labor.getProductiviteOuvrier()))
                 .append(" = ").append(String.format("%.2f", labor.calculateCost()))
                 .append("€\n");
        }
    }

    private void appendCostSummary(StringBuilder quote, Project project) {
        double totalCost = costCalculator.calculateTotalCost(project);
        double costPerSquareMeter = costCalculator.calculateCostPerSquareMeter(project);
        quote.append("\nCoût total du projet: ").append(String.format("%.2f", totalCost)).append("€\n");
        quote.append("Coût par mètre carré: ").append(String.format("%.2f", costPerSquareMeter)).append("€/m²\n");
    }

    public void saveQuote(Quote quote) {
        try {
            quoteRepository.saveQuote(quote);
            LOGGER.info("Devis enregistré : " + quote.getId());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'enregistrement du devis", e);
            throw new RuntimeException("Échec de l'enregistrement du devis", e);
        }
    }
}
