package com.baticuisine.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

import com.baticuisine.model.Project;
import com.baticuisine.model.enums.ProjectStatus;
import com.baticuisine.service.CostCalculator;
import com.baticuisine.service.ProjectService;
import com.baticuisine.service.QuoteGenerator;
import com.baticuisine.utils.InputValidator;

public class ProjectUI {
    private static final Logger LOGGER = Logger.getLogger(ProjectUI.class.getName());
    private Scanner scanner;
    private ProjectService projectService;
    private CostCalculator costCalculator;
    private QuoteGenerator quoteGenerator;
    private InputValidator inputValidator;

    public ProjectUI(ProjectService projectService, CostCalculator costCalculator, 
                     InputValidator inputValidator, QuoteGenerator quoteGenerator) {
        this.scanner = new Scanner(System.in);
        this.projectService = projectService;
        this.costCalculator = costCalculator;
        this.quoteGenerator = quoteGenerator;
        this.inputValidator = inputValidator;
    }

    public void createNewProject() {
        LOGGER.info("Starting new project creation process");
        System.out.println("=== Création d'un nouveau projet ===");
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet : ");
        double surface = inputValidator.getValidDoubleInput(scanner, "Surface de la cuisine (en m²) : ");
        LocalDate startDate = inputValidator.getValidDateInput(scanner, "Date de début du projet (jj/mm/aaaa) : ");

        Project newProject = new Project(projectName, surface, startDate, ProjectStatus.EN_COURS);
        projectService.createProject(newProject);
        LOGGER.info("New project created: " + projectName);
        System.out.println("Projet créé avec succès !");
    }

    public void displayExistingProjects() {
        LOGGER.info("Displaying existing projects");
        System.out.println("=== Projets existants ===");
        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("Aucun projet existant.");
        } else {
            projects.forEach(this::displayProjectDetails);
        }
    }

    private void displayProjectDetails(Project project) {
        System.out.println("\nNom du projet: " + project.getName());
        System.out.println("Surface: " + project.getSurface() + " m²");
        System.out.println("Date de début: " + project.getStartDate());
        System.out.println("Statut: " + project.getStatus());
        System.out.println("Coût total: " + costCalculator.calculateTotalCost(project) + " €");
        System.out.println("--------------------");
    }

    public void calculateProjectCost() {
        LOGGER.info("Starting project cost calculation");
        System.out.println("=== Calcul du coût d'un projet ===");
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet : ");
        
        Optional<Project> projectOpt = projectService.getProjectByName(projectName);
        
        projectOpt.ifPresent(project -> {
            double cost = costCalculator.calculateTotalCost(project);
            System.out.println("Le coût total du projet " + projectName + " est : " + cost + " €");
        });

        if (!projectOpt.isPresent()) {
            System.out.println("Projet non trouvé.");
        }
    }

    public void generateProjectQuote() {
        LOGGER.info("Starting project quote generation");
        System.out.println("=== Génération de devis pour un projet ===");
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet : ");
        
        Optional<Project> projectOpt = projectService.getProjectByName(projectName);
        
        projectOpt.ifPresent(project -> {
            String quote = quoteGenerator.generateQuote(project);
            System.out.println(quote);
        });

        if (!projectOpt.isPresent()) {
            System.out.println("Projet non trouvé.");
        }
    }
}
