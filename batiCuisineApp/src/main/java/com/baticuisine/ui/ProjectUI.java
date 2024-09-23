package com.baticuisine.ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

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
    private final Scanner scanner;
    private final ProjectService projectService;
    private final CostCalculator costCalculator;
    private final QuoteGenerator quoteGenerator;
    private final InputValidator inputValidator;
    private final ClientService clientService;

    public ProjectUI(ProjectService projectService, CostCalculator costCalculator,
            InputValidator inputValidator, QuoteGenerator quoteGenerator,
            MaterialService materialService, ClientService clientService) {
        this.scanner = new Scanner(System.in);
        this.projectService = projectService;
        this.costCalculator = costCalculator;
        this.quoteGenerator = quoteGenerator;
        this.inputValidator = inputValidator;
        this.clientService = clientService;
    }

    public void manageProjects() {
        while (true) {
            displayMainMenu();
            int choice = inputValidator.getValidIntInput(scanner, "Choose an option: ");
            if (!handleMainMenuChoice(choice)) {
                break;
            }
        }
    }

    public Optional<Project> getProjectByName(String projectName) {
        try {
            return projectService.getProjectByName(projectName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving project by name: " + projectName, e);
            throw new RuntimeException("Failed to retrieve project by name", e);
        }
    }

    private void displayMainMenu() {
        System.out.println("\n=== Project Management ===");
        System.out.println("1. Create a new project");
        System.out.println("2. View and manage existing projects");
        System.out.println("3. Delete a project");
        System.out.println("4. View quotes by project name");
        System.out.println("5. Return to main menu");
    }

    private boolean handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                createNewProject();
                return true;
            case 2:
                viewAndManageProjects();
                return true;
            case 3:
                deleteProject();
                return true;
            case 4:
                viewQuotesByProjectName();
                return true;
            case 5:
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return true;
        }
    }

    private void createNewProject() {
        System.out.println("\n=== Create a New Project ===");
        Project project = createProjectFromInput();
        addComponentsToProject(project);
        saveProject(project);
    }

    private Project createProjectFromInput() {
        String projectName = inputValidator.getValidStringInput(scanner, "Nom du projet: ");
        double surface = inputValidator.getValidDoubleInput(scanner, "Surface (en mètres carrés): ");
        LocalDate startDate = inputValidator.getValidDateInput(scanner, "Date de début (jj/mm/aaaa): ");
        ProjectStatus status = getValidProjectStatus();
        Client client = getOrCreateClient();
        return new Project(projectName, surface, startDate, status, client);
    }

    private ProjectStatus getValidProjectStatus() {
        while (true) {
            String statusInput = inputValidator.getValidStringInput(scanner,
                    "Statut du projet (EN_COURS, TERMINE, ANNULE, EN_ATTENTE): ");
            try {
                return ProjectStatus.fromDbValue(statusInput);
            } catch (IllegalArgumentException e) {
                System.out.println(
                        "Statut invalide. Veuillez entrer l'un des suivants: EN_COURS, TERMINE, ANNULE, EN_ATTENTE.");
            }
        }
    }

    private void addComponentsToProject(Project project) {
        while (true) {
            System.out.println("\nAdd components to the project:");
            System.out.println("1. Add material");
            System.out.println("2. Add labor");
            System.out.println("3. Finish adding components");

            int componentChoice = inputValidator.getValidIntInput(scanner, "Choose an option: ");

            switch (componentChoice) {
                case 1:
                    addMaterialToProject(project);
                    break;
                case 2:
                    addLaborToProject(project);
                    break;
                case 3:
                    saveProject(project);
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void saveProject(Project project) {
        Optional<Project> createdProject = projectService.createProject(project);
        if (createdProject.isPresent()) {
            System.out.println("Project created successfully: " + createdProject.get());
        } else {
            System.out.println("Failed to create project.");
        }
    }

    private Client getOrCreateClient() {
        System.out.println("\n=== Client Information ===");
        boolean clientExists = inputValidator.getValidBooleanInput(scanner,
                "Does the client already exist? (oui/non): ");
        if (clientExists) {
            String clientName = inputValidator.getValidStringInput(scanner, "Client name (e.g., John Doe): ");
            String clientPhone = inputValidator.getValidPhoneInput(scanner,
                    "Client phone number (e.g., +1234567890): ");
            List<Client> clients = clientService.getClientsByNameAndPhone(clientName, clientPhone);
            if (clients.isEmpty()) {
                System.out.println("Client not found. Let's create a new client.");
                return createNewClient();
            } else if (clients.size() == 1) {
                return clients.get(0);
            } else {
                return selectClientFromList(clients);
            }
        } else {
            return createNewClient();
        }
    }

    private Client selectClientFromList(List<Client> clients) {
        System.out.println("\nMultiple clients found with the same name. Please select the correct client:");
        for (int i = 0; i < clients.size(); i++) {
            System.out.println((i + 1) + ". " + clients.get(i));
        }
        int clientIndex = inputValidator.getValidIntInput(scanner, "Select a client (1 to " + clients.size() + "): ")
                - 1;
        if (clientIndex >= 0 && clientIndex < clients.size()) {
            return clients.get(clientIndex);
        } else {
            System.out.println("Invalid selection. Please try again.");
            return selectClientFromList(clients);
        }
    }

    private Client createNewClient() {
        System.out.println("Client not found. Let's create a new client.");
        String name = inputValidator.getValidStringInput(scanner, "Client name (e.g., John Doe): ");
        String email = inputValidator.getValidEmailInput(scanner, "Client email (e.g., john.doe@example.com): ");
        String address = inputValidator.getValidStringInput(scanner, "Client address (e.g., 123 Main St): ");
        String phone = inputValidator.getValidPhoneInput(scanner, "Client phone number (e.g., +1234567890): ");
        boolean isProfessional = inputValidator.getValidBooleanInput(scanner,
                "Is the client professional? (oui/non): ");
        double discountRate = inputValidator.getValidDoubleInput(scanner,
                "Client discount rate (0-1, e.g., 0.1 for 10%): ");
        Client newClient = new Client(name, email, address, phone, isProfessional, discountRate);
        clientService.createClient(newClient);
        System.out.println("New client created successfully!");
        return newClient;
    }

    private void addMaterialToProject(Project project) {
        Material material = createMaterialFromInput();
        project.addMaterial(material);
        try {
            projectService.saveMaterial(material, project.getId());
            System.out.println("Material added to the project and saved to the database.");
        } catch (SQLException e) {
            System.out.println("Failed to save material to the database: " + e.getMessage());
        }
    }

    private Material createMaterialFromInput() {
        String name = inputValidator.getValidStringInput(scanner, "Material name: ");
        double vatRate = inputValidator.getValidDoubleInput(scanner, "VAT rate: ");
        double unitCost = inputValidator.getValidDoubleInput(scanner, "Unit cost: ");
        double quantity = inputValidator.getValidDoubleInput(scanner, "Quantity: ");
        double transportCost = inputValidator.getValidDoubleInput(scanner, "Transport cost: ");
        double qualityCoefficient = inputValidator.getValidDoubleInput(scanner, "Quality coefficient: ");
        return new Material(name, vatRate, unitCost, quantity, transportCost, qualityCoefficient);
    }

    private void addLaborToProject(Project project) {
        Labor labor = createLaborFromInput();
        project.addLabor(labor);
        try {
            projectService.saveLabor(labor, project.getId());
            System.out.println("Labor added to the project and saved to the database.");
        } catch (SQLException e) {
            System.out.println("Failed to save labor to the database: " + e.getMessage());
        }
    }

    private Labor createLaborFromInput() {
        String name = inputValidator.getValidStringInput(scanner, "Labor name: ");
        double hoursWorked = inputValidator.getValidDoubleInput(scanner, "Hours worked: ");
        double hourlyRate = inputValidator.getValidDoubleInput(scanner, "Hourly rate: ");
        double workerProductivity = inputValidator.getValidDoubleInput(scanner, "Worker productivity (0-1): ");
        double vatRate = inputValidator.getValidDoubleInput(scanner, "VAT rate: ");
        return new Labor(name, hoursWorked, hourlyRate, workerProductivity, vatRate);
    }

    private void viewAndManageProjects() {
        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        displayProjects(projects);
        int projectIndex = selectProject(projects);
        if (projectIndex >= 0) {
            manageProject(projects.get(projectIndex));
        }
    }

    private void displayProjects(List<Project> projects) {
        System.out.println("\n=== Existing Projects ===");
        IntStream.range(0, projects.size())
                .forEach(i -> System.out.println((i + 1) + ". " + projects.get(i).getProjectName()));
    }

    private int selectProject(List<Project> projects) {
        int projectIndex = inputValidator.getValidIntInput(scanner, "Select a project to manage (0 to cancel): ") - 1;
        return (projectIndex >= -1 && projectIndex < projects.size()) ? projectIndex : -1;
    }

    private void manageProject(Project project) {
        while (true) {
            displayProjectMenu(project);
            int choice = inputValidator.getValidIntInput(scanner, "Choose an option: ");
            if (!handleProjectMenuChoice(choice, project)) {
                break;
            }
        }
    }

    private void displayProjectMenu(Project project) {
        System.out.println("\n=== Managing Project: " + project.getProjectName() + " ===");
        System.out.println("1. View project details");
        System.out.println("2. Update project status");
        System.out.println("3. Add material");
        System.out.println("4. Add labor");
        System.out.println("5. Calculate total cost");
        System.out.println("6. Generate quote");
        System.out.println("7. Review and accept quote");
        System.out.println("8. Return to project list");
    }

    private boolean handleProjectMenuChoice(int choice, Project project) {
        switch (choice) {
            case 1:
                viewProjectDetails(project);
                return true;
            case 2:
                updateProjectStatus(project);
                return true;
            case 3:
                addMaterialToProject(project);
                return true;
            case 4:
                addLaborToProject(project);
                return true;
            case 5:
                calculateAndDisplayTotalCost(project);
                return true;
            case 6:
                generateAndDisplayQuote(project);
                return true;
            case 7:
                reviewAndAcceptQuote(project);
                return true;
            case 8:
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return true;
        }
    }

    private void viewProjectDetails(Project project) {
        System.out.println("\n=== Project Details ===");
        System.out.println(project.toString());
        System.out.println("\nMaterials:");
        project.getMaterials().forEach(System.out::println);
        System.out.println("\nLabor:");
        project.getLaborItems().forEach(System.out::println);
    }

    private void updateProjectStatus(Project project) {
        System.out.println("\n=== Update Project Status ===");
        ProjectStatus newStatus = getValidProjectStatus();
        project.setProjectStatus(newStatus);
        Project updatedProject = projectService.updateProject(project);
        if (updatedProject != null) {
            System.out.println("Project status updated successfully: " + updatedProject.getProjectStatus());
        } else {
            System.out.println("Failed to update project status.");
        }
    }

    private void calculateAndDisplayTotalCost(Project project) {
        Project updatedProject = projectService.calculateTotalCost(project.getId());
        System.out.println(
                "Total cost for project " + updatedProject.getProjectName() + ": "
                        + String.format("%.2f", updatedProject.getTotalCost()) + " €");
        // Update the project in the current context
        project.setTotalCost(updatedProject.getTotalCost());
    }

    private void generateAndDisplayQuote(Project project) {
        Quote quote = quoteGenerator.generateQuote(project);
        System.out.println("\nQuote generated successfully!");
        System.out.println(quote.toString());
        System.out.println("\nDetailed quote content:");
        System.out.println(quote.getContent());

        if (inputValidator.getValidBooleanInput(scanner, "Do you want to save this quote? (oui/non): ")) {
            saveQuote(quote);
        }
    }

    private void saveQuote(Quote quote) {
        Optional<Quote> savedQuoteOpt = quoteGenerator.saveQuote(quote);
        if (savedQuoteOpt.isPresent()) {
            System.out.println("Quote saved successfully: " + savedQuoteOpt.get());
        } else {
            System.out.println("Failed to save quote.");
        }
    }

    private void deleteProject() {
        String projectName = inputValidator.getValidStringInput(scanner, "Enter the name of the project to delete: ");
        Optional<Project> projectToDelete = projectService.getProjectByName(projectName);
        if (projectToDelete.isPresent()) {
            confirmAndDeleteProject(projectToDelete.get());
        } else {
            System.out.println("Project not found.");
        }
    }

    private void confirmAndDeleteProject(Project project) {
        System.out.println("Are you sure you want to delete this project? " + project.getProjectName());
        if (inputValidator.getValidBooleanInput(scanner, "Confirm deletion (yes/no): ")) {
            try {
                projectService.deleteProject(project.getId());
                System.out.println("Project deleted successfully: " + project.getProjectName());
            } catch (RuntimeException e) {
                System.out.println("Failed to delete project: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    public void reviewAndAcceptQuote(Project project) {
        List<Quote> quotes = quoteGenerator.getQuotesByProjectId(project.getId());

        if (quotes.isEmpty()) {
            System.out.println("No quotes found for the project: " + project.getProjectName());
            return;
        }

        System.out.println("\nQuotes for project: " + project.getProjectName());
        for (int i = 0; i < quotes.size(); i++) {
            System.out.println((i + 1) + ". " + quotes.get(i).toString());
        }

        int quoteIndex = inputValidator.getValidIntInput(scanner,
                "Select a quote to review (1 to " + quotes.size() + "): ") - 1;
        if (quoteIndex >= 0 && quoteIndex < quotes.size()) {
            Quote quote = quotes.get(quoteIndex);
            System.out.println("\nQuote Details:");
            System.out.println(quote.toString());
            System.out.println("\nDetailed quote content:");
            System.out.println(quote.getContent());

            boolean accept = inputValidator.getValidBooleanInput(scanner,
                    "Do you want to accept this quote? (yes/no): ");
            try {
                quote.setAccepted(accept);
                quoteGenerator.updateQuote(quote);
                System.out.println("Quote " + (accept ? "accepted" : "refused") + " successfully.");
            } catch (IllegalStateException e) {
                System.out.println("Failed to accept quote: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid selection. Please try again.");
        }
    }

    public void viewQuotesByProjectName() {
        String projectName = inputValidator.getValidStringInput(scanner, "Enter the name of the project: ");
        Optional<Project> projectOpt = projectService.getProjectByName(projectName);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            List<Quote> quotes = quoteGenerator.getQuotesByProjectId(project.getId());

            if (quotes.isEmpty()) {
                System.out.println("No quotes found for the project: " + projectName);
            } else {
                System.out.println("\nQuotes for project: " + projectName);
                for (Quote quote : quotes) {
                    System.out.println(quote.toString());
                    System.out.println("\nDetailed quote content:");
                    System.out.println(quote.getContent());
                }
            }
        } else {
            System.out.println("Project not found: " + projectName);
        }
    }
}