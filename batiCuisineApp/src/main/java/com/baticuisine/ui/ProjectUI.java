package com.baticuisine.ui;

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
    private final MaterialService materialService;
    private final ClientService clientService;

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
        return projectService.findByName(projectName);
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
        System.out.println("4. Manage quotes");
        System.out.println("5. Return to main menu");
    }

    private boolean handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1: createNewProject(); return true;
            case 2: viewAndManageProjects(); return true;
            case 3: deleteProject(); return true;
            case 4: manageQuotes(); return true;
            case 5: return false;
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
        String projectName = inputValidator.getValidStringInput(scanner, "Project name: ");
        double surface = inputValidator.getValidDoubleInput(scanner, "Surface area (m²): ");
        LocalDate startDate = inputValidator.getValidDateInput(scanner, "Start date (YYYY-MM-DD): ");
        ProjectStatus status = getValidProjectStatus();
        Client client = getOrCreateClient();
        return new Project(projectName, surface, startDate, status, client);
    }

    private void addComponentsToProject(Project project) {
        while (true) {
            System.out.println("\nAdd components to the project:");
            System.out.println("1. Add material");
            System.out.println("2. Add labor");
            System.out.println("3. Finish adding components");

            int componentChoice = inputValidator.getValidIntInput(scanner, "Choose an option: ");

            switch (componentChoice) {
                case 1: addMaterialToProject(project); break;
                case 2: addLaborToProject(project); break;
                case 3: return;
                default: System.out.println("Invalid option. Please try again.");
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
        String clientName = inputValidator.getValidStringInput(scanner, "Client name: ");
        return clientService.getClientByName(clientName).orElseGet(this::createNewClient);
    }

    private Client createNewClient() {
        System.out.println("Client not found. Let's create a new client.");
        String name = inputValidator.getValidStringInput(scanner, "Client name: ");
        String email = inputValidator.getValidEmailInput(scanner, "Client email: ");
        String address = inputValidator.getValidStringInput(scanner, "Client address: ");
        String phone = inputValidator.getValidPhoneInput(scanner, "Client phone number: ");
        boolean isProfessional = inputValidator.getValidBooleanInput(scanner, "Is the client professional? (yes/no): ");
        double discountRate = inputValidator.getValidDoubleInput(scanner, "Client discount rate (0-1): ");
        Client newClient = new Client(name, email, address, phone, isProfessional, discountRate);
        clientService.createClient(newClient);
        System.out.println("New client created successfully!");
        return newClient;
    }

    private void addMaterialToProject(Project project) {
        Material material = createMaterialFromInput();
        project.addMaterial(material);
        System.out.println("Material added to the project.");
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
        System.out.println("Labor added to the project.");
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
        System.out.println("7. Return to project list");
    }

    private boolean handleProjectMenuChoice(int choice, Project project) {
        switch (choice) {
            case 1: viewProjectDetails(project); return true;
            case 2: updateProjectStatus(project); return true;
            case 3: addMaterialToProject(project); return true;
            case 4: addLaborToProject(project); return true;
            case 5: calculateAndDisplayTotalCost(project); return true;
            case 6: generateAndDisplayQuote(project); return true;
            case 7: return false;
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
        double totalCost = costCalculator.calculateTotalCost(project);
        System.out.println("Total cost for project " + project.getProjectName() + ": " + String.format("%.2f", totalCost) + " €");
    }

    private void generateAndDisplayQuote(Project project) {
        Quote quote = quoteGenerator.generateQuote(project);
        System.out.println("\nQuote generated successfully!");
        System.out.println(quote.toString());
        System.out.println("\nDetailed quote content:");
        System.out.println(quote.getContent());
        
        if (inputValidator.getValidBooleanInput(scanner, "Do you want to save this quote? (yes/no): ")) {
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

    private ProjectStatus getValidProjectStatus() {
        System.out.println("Available project statuses:");
        ProjectStatus[] statuses = ProjectStatus.values();
        IntStream.range(0, statuses.length)
                .forEach(i -> System.out.println((i + 1) + ". " + statuses[i]));

        int statusChoice = inputValidator.getValidIntInput(scanner, "Choose a project status: ");
        return statuses[statusChoice - 1];
    }

    private void manageQuotes() {
        System.out.println("\n=== Quote Management ===");
        System.out.println("1. Generate a new quote");
        System.out.println("2. View existing quotes");
        System.out.println("0. Return");

        int choice = inputValidator.getValidIntInput(scanner, "Choose an option: ");

        switch (choice) {
            case 1: generateNewQuote(); break;
            case 2: viewExistingQuotes(); break;
            case 0: break;
            default: System.out.println("Invalid option.");
        }
    }

    private void generateNewQuote() {
        String projectName = inputValidator.getValidStringInput(scanner, "Enter the name of the project to generate a quote for: ");
        Optional<Project> projectOpt = projectService.getProjectByName(projectName);
        if (projectOpt.isPresent()) {
            generateAndDisplayQuote(projectOpt.get());
        } else {
            System.out.println("Project not found.");
        }
    }

    private void viewExistingQuotes() {
        System.out.println("Viewing existing quotes is not implemented yet.");
    }
}