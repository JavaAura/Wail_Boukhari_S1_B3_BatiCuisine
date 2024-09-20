package com.baticuisine.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

import com.baticuisine.model.Client;
import com.baticuisine.model.Labor;
import com.baticuisine.model.Material;
import com.baticuisine.model.Project;
import com.baticuisine.model.Quote;
import com.baticuisine.model.enums.ProjectStatus;
import com.baticuisine.service.ClientService;
import com.baticuisine.service.CostCalculator;
import com.baticuisine.service.MaterialService;
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
    private MaterialService materialService;
    private ClientService clientService;

    public ProjectUI(ProjectService projectService, CostCalculator costCalculator, 
                     InputValidator inputValidator, QuoteGenerator quoteGenerator,
                     MaterialService materialService, ClientService clientService) {
        this.scanner = new Scanner(System.in);
        this.projectService = projectService;
        this.costCalculator = costCalculator;
        this.quoteGenerator = quoteGenerator;
        this.inputValidator = inputValidator;
        this.materialService = materialService;
        this.clientService = clientService;
    }

    public void manageProjects() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Gestion des projets ===");
            System.out.println("1. Créer un nouveau projet");
            System.out.println("2. Afficher et gérer les projets existants");
            System.out.println("3. Supprimer un projet");
            System.out.println("4. Gérer les devis");
            System.out.println("5. Retour au menu principal");
    
            int choice = inputValidator.getValidIntInput(scanner, "Choisissez une option : ");
    
            switch (choice) {
                case 1: createNewProject(); break;
                case 2: displayAndManageProjects(); break;
                case 3: deleteProject(); break;
                case 4: manageQuotes(); break;
                case 5: running = false; break;
                default: System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void displayAndManageProjects() {
        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("Aucun projet existant.");
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getName());
        }

        int projectChoice = inputValidator.getValidIntInput(scanner, "Choisissez un projet à gérer (0 pour revenir) : ") - 1;
        if (projectChoice >= 0 && projectChoice < projects.size()) {
            Project selectedProject = projects.get(projectChoice);
            manageProject(selectedProject);
        }
    }

    private void manageProject(Project project) {
        System.out.println("\n=== Gestion du projet: " + project.getName() + " ===");
        System.out.println("1. Afficher les détails");
        System.out.println("2. Mettre à jour le projet");
        System.out.println("3. Calculer le coût et générer un devis");

        int choice = inputValidator.getValidIntInput(scanner, "Choisissez une option : ");

        switch (choice) {
            case 1: displayProjectDetails(project); break;
            case 2: updateProject(project); break;
            case 3: calculateCostAndGenerateQuote(project); break;
            default: System.out.println("Option invalide.");
        }
    }

    private void createNewProject() {
        LOGGER.info("Starting new project creation process");
        System.out.println("=== Création d'un nouveau projet ===");
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet : ");
        double surface = inputValidator.getValidDoubleInput(scanner, "Surface de la cuisine (en m²) : ");
        LocalDate startDate = inputValidator.getValidDateInput(scanner, "Date de début du projet (jj/mm/aaaa) : ");

        Project newProject = new Project(projectName, surface, startDate, ProjectStatus.EN_COURS);

        // Add materials
        boolean addMoreMaterials = true;
        while (addMoreMaterials) {
            addMaterialToProject(newProject);
            addMoreMaterials = inputValidator.getValidBooleanInput(scanner, "Voulez-vous ajouter un autre matériau ? (oui/non) : ");
        }

        // Add labor
        boolean addMoreLabor = true;
        while (addMoreLabor) {
            addLaborToProject(newProject);
            addMoreLabor = inputValidator.getValidBooleanInput(scanner, "Voulez-vous ajouter une autre main d'œuvre ? (oui/non) : ");
        }

        // Add client
        addClientToProject(newProject);

        projectService.createProject(newProject);
        LOGGER.info("New project created: " + projectName);
        System.out.println("Projet créé avec succès !");

        // Ask about quote
        boolean seeQuote = inputValidator.getValidBooleanInput(scanner, "Voulez-vous voir le devis ? (oui/non) : ");
        if (seeQuote) {
            Quote quote = quoteGenerator.generateQuote(newProject);
            System.out.println(quote.toString());
            System.out.println(quote.getContent());

            boolean saveQuote = inputValidator.getValidBooleanInput(scanner, "Voulez-vous sauvegarder le devis ? (oui/non) : ");
            if (saveQuote) {
                quoteGenerator.saveQuote(quote);
                System.out.println("Devis sauvegardé avec succès !");
            }
        }
    }

    private void addMaterialToProject(Project project) {
        boolean materialExists = inputValidator.getValidBooleanInput(scanner, "Le matériau existe-t-il déjà ? (oui/non) : ");
        
        if (materialExists) {
            String materialName = inputValidator.getValidStringInput(scanner, "Nom du matériau : ");
            Optional<Material> materialOpt = materialService.getMaterialByName(materialName);
            
            if (materialOpt.isPresent()) {
                Material material = materialOpt.get();
                double quantity = inputValidator.getValidDoubleInput(scanner, "Quantité : ");
                project.addMaterial(material, quantity);
            } else {
                System.out.println("Matériau non trouvé. Création d'un nouveau matériau.");
                createAndAddNewMaterial(project);
            }
        } else {
            createAndAddNewMaterial(project);
        }
    }

    private void createAndAddNewMaterial(Project project) {
        Material newMaterial = createNewMaterial();
        double quantity = inputValidator.getValidDoubleInput(scanner, "Quantité : ");
        project.addMaterial(newMaterial, quantity);
    }

    private Material createNewMaterial() {
        String name = inputValidator.getValidStringInput(scanner, "Nom du matériau : ");
        double coutUnitaire = inputValidator.getValidDoubleInput(scanner, "Coût unitaire : ");
        double quantite = inputValidator.getValidDoubleInput(scanner, "Quantité : ");
        double tauxTVA = inputValidator.getValidDoubleInput(scanner, "Taux de TVA (%) : ");
        double coutTransport = inputValidator.getValidDoubleInput(scanner, "Coût de transport : ");
        double coefficientQualite = inputValidator.getValidDoubleInput(scanner, "Coefficient de qualité : ");
    
        Material newMaterial = new Material(name, coutUnitaire, quantite, tauxTVA, coutTransport, coefficientQualite);
        materialService.createMaterial(newMaterial);
        LOGGER.info("New material created: " + name);
        System.out.println("Nouveau matériau créé avec succès !");
        return newMaterial;
    }

    private void addLaborToProject(Project project) {
        String laborName = inputValidator.getValidStringInput(scanner, "Nom de la main d'œuvre : ");
        double hours = inputValidator.getValidDoubleInput(scanner, "Nombre d'heures : ");
        double hourlyRate = inputValidator.getValidDoubleInput(scanner, "Taux horaire : ");
        double productivityFactor = inputValidator.getValidDoubleInput(scanner, "Facteur de productivité : ");
        double vatRate = inputValidator.getValidDoubleInput(scanner, "Taux de TVA (%) : ");
        
        Labor labor = new Labor(laborName, hourlyRate, hours, productivityFactor, vatRate);
        project.addLaborItem(labor);
    }
    private void addClientToProject(Project project) {
        boolean clientExists = inputValidator.getValidBooleanInput(scanner, "Le client existe-t-il déjà ? (oui/non) : ");
        
        if (clientExists) {
            String clientName = inputValidator.getValidStringInput(scanner, "Nom du client : ");
            Optional<Client> clientOpt = clientService.getClientByName(clientName);
            
            if (clientOpt.isPresent()) {
                project.setClient(clientOpt.get());
            } else {
                System.out.println("Client non trouvé. Création d'un nouveau client.");
                createAndAddNewClient(project);
            }
        } else {
            createAndAddNewClient(project);
        }
    }

    private void createAndAddNewClient(Project project) {
        String name = inputValidator.getValidStringInput(scanner, "Nom du client : ");
        String email = inputValidator.getValidEmailInput(scanner, "Email du client : ");
        String phone = inputValidator.getValidPhoneInput(scanner, "Numéro de téléphone du client : ");
        String address = inputValidator.getValidStringInput(scanner, "Adresse du client : ");
        boolean isProfessional = inputValidator.getValidBooleanInput(scanner, "Le client est-il un professionnel ? (oui/non) : ");
        
        Client newClient = new Client(name, email, phone, address, isProfessional);
        clientService.createClient(newClient);
        project.setClient(newClient);
        
        System.out.println("Client " + (isProfessional ? "professionnel" : "particulier") + " ajouté avec succès !");
        if (isProfessional) {
            System.out.println("Ce client bénéficie d'une remise de " + (newClient.getDiscountRate() * 100) + "%");
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

    private void updateProject(Project project) {
        LOGGER.info("Starting project update process");
        System.out.println("=== Mise à jour du projet: " + project.getName() + " ===");
    
        double surface = inputValidator.getValidDoubleInput(scanner, "Nouvelle surface de la cuisine (en m²) : ");
        LocalDate startDate = inputValidator.getValidDateInput(scanner, "Nouvelle date de début du projet (jj/mm/aaaa) : ");
        ProjectStatus status = getValidProjectStatus();
    
        project.setSurface(surface);
        project.setStartDate(startDate);
        project.setStatus(status);
    
        projectService.updateProject(project);
        LOGGER.info("Project updated: " + project.getName());
        System.out.println("Projet mis à jour avec succès !");
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

    private void calculateCostAndGenerateQuote(Project project) {
        LOGGER.info("Starting project cost calculation and quote generation for project: " + project.getName());
        System.out.println("=== Calcul du coût et génération de devis ===");
        
        double cost = costCalculator.calculateTotalCost(project);
        System.out.println("Le coût total du projet " + project.getName() + " est : " + cost + " €");
        
        Quote quote = quoteGenerator.generateQuote(project);
        System.out.println("\nDevis généré avec succès !");
        System.out.println(quote.toString());
        System.out.println("\nContenu détaillé du devis :");
        System.out.println(quote.getContent());
        
        boolean saveQuote = inputValidator.getValidBooleanInput(scanner, "Voulez-vous sauvegarder le devis ? (oui/non) : ");
        if (saveQuote) {
            quoteGenerator.saveQuote(quote);
            System.out.println("Devis sauvegardé avec succès !");
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


    private void manageQuotes() {
        System.out.println("\n=== Gestion des devis ===");
        System.out.println("1. Générer un nouveau devis");
        System.out.println("0. Retour");

        int choice = inputValidator.getValidIntInput(scanner, "Choisissez une option : ");

        switch (choice) {
            case 1: generateNewQuote(); break;
            case 0: return;
            default: System.out.println("Option invalide.");
        }
    }

    private void generateNewQuote() {
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet pour lequel générer un devis : ");
        Optional<Project> projectOpt = projectService.getProjectByName(projectName);
        
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            Quote quote = quoteGenerator.generateQuote(project);
            System.out.println("Devis généré avec succès :");
            System.out.println(quote.toString());
            System.out.println(quote.getContent());
        } else {
            System.out.println("Projet non trouvé.");
        }
    }
}