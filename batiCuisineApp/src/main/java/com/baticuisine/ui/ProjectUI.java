package com.baticuisine.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Project;
import com.baticuisine.model.Quote;
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

    public void manageProjects() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Gestion des projets ===");
            System.out.println("1. Créer un nouveau projet");
            System.out.println("2. Afficher tous les projets");
            System.out.println("3. Mettre à jour un projet");
            System.out.println("4. Supprimer un projet");
            System.out.println("5. Calculer le coût d'un projet");
            System.out.println("6. Générer un devis pour un projet");
            System.out.println("7. Retour au menu principal");

            int choice = inputValidator.getValidIntInput(scanner, "Choisissez une option : ");

            try {
                switch (choice) {
                    case 1: createNewProject(); break;
                    case 2: displayExistingProjects(); break;
                    case 3: updateProject(); break;
                    case 4: deleteProject(); break;
                    case 5: calculateProjectCost(); break;
                    case 6: generateProjectQuote(); break;
                    case 7: running = false; break;
                    default: System.out.println("Option invalide. Veuillez réessayer.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An error occurred while managing projects", e);
                System.out.println("Une erreur est survenue. Veuillez réessayer.");
            }
        }
    }

    private void createNewProject() {
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

    private void displayExistingProjects() {
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
        System.out.println("\n--------------------");
        System.out.println("Nom du projet: " + project.getName());
        System.out.println("Surface: " + project.getSurface() + " m²");
        System.out.println("Date de début: " + project.getStartDate());
        System.out.println("Statut: " + project.getStatus());
        System.out.println("Coût total: " + costCalculator.calculateTotalCost(project) + " €");
        System.out.println("--------------------");
    }

    private void updateProject() {
        LOGGER.info("Starting project update process");
        System.out.println("=== Mise à jour d'un projet ===");
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet à mettre à jour : ");

        Optional<Project> projectOpt = projectService.getProjectByName(projectName);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            double surface = inputValidator.getValidDoubleInput(scanner, "Nouvelle surface de la cuisine (en m²) : ");
            LocalDate startDate = inputValidator.getValidDateInput(scanner, "Nouvelle date de début du projet (jj/mm/aaaa) : ");
            ProjectStatus status = getValidProjectStatus();

            project.setSurface(surface);
            project.setStartDate(startDate);
            project.setStatus(status);

            projectService.updateProject(project);
            LOGGER.info("Project updated: " + projectName);
            System.out.println("Projet mis à jour avec succès !");
        } else {
            System.out.println("Projet non trouvé.");
        }
    }

    private void deleteProject() {
        LOGGER.info("Starting project deletion process");
        System.out.println("=== Suppression d'un projet ===");
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet à supprimer : ");

        Optional<Project> projectOpt = projectService.getProjectByName(projectName);
        if (projectOpt.isPresent()) {
            boolean confirm = inputValidator.getValidBooleanInput(scanner, "Êtes-vous sûr de vouloir supprimer ce projet ? (oui/non) : ");
            if (confirm) {
                projectService.deleteProject(projectName);
                LOGGER.info("Project deleted: " + projectName);
                System.out.println("Projet supprimé avec succès !");
            } else {
                System.out.println("Suppression annulée.");
            }
        } else {
            System.out.println("Projet non trouvé.");
        }
    }

    private void calculateProjectCost() {
        LOGGER.info("Starting project cost calculation");
        System.out.println("=== Calcul du coût d'un projet ===");
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet : ");
        
        Optional<Project> projectOpt = projectService.getProjectByName(projectName);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            double cost = costCalculator.calculateTotalCost(project);
            System.out.println("Le coût total du projet " + projectName + " est : " + cost + " €");
        } else {
            System.out.println("Projet non trouvé.");
        }
    }

    private void generateProjectQuote() {
        LOGGER.info("Starting project quote generation");
        System.out.println("=== Génération de devis pour un projet ===");
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet : ");
        
        Optional<Project> projectOpt = projectService.getProjectByName(projectName);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            Quote quote = quoteGenerator.generateQuote(project);
            System.out.println("Devis généré avec succès !");
            System.out.println(quote.toString());
            System.out.println("\nContenu détaillé du devis :");
            System.out.println(quote.getContent());
        } else {
            System.out.println("Projet non trouvé.");
        }
    }

    private ProjectStatus getValidProjectStatus() {
        while (true) {
            System.out.println("Statuts de projet disponibles :");
            for (ProjectStatus status : ProjectStatus.values()) {
                System.out.println("- " + status.name());
            }
            String statusInput = inputValidator.getValidStringInput(scanner, "Statut du projet : ");
            try {
                return ProjectStatus.valueOf(statusInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Statut de projet invalide. Veuillez réessayer.");
            }
        }
    }
}