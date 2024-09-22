package com.baticuisine.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import com.baticuisine.model.*;
import com.baticuisine.model.enums.ProjectStatus;
import com.baticuisine.service.*;
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
        boolean running = true;
        while (running) {
            System.out.println("\n=== Project Management ===");
            System.out.println("1. Create a new project");
            System.out.println("2. View and manage existing projects");
            System.out.println("3. Delete a project");
            System.out.println("4. Manage quotes");
            System.out.println("5. Return to main menu");

            int choice = inputValidator.getValidIntInput(scanner, "Choose an option: ");

            switch (choice) {
                case 1:
                    createNewProject();
                    break;
                case 2:
                    viewAndManageProjects();
                    break;
                case 3:
                    deleteProject();
                    break;
                case 4:
                    manageQuotes();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createNewProject() {
        System.out.println("\n=== Create a New Project ===");
        String projectName = inputValidator.getValidStringInput(scanner, "Project name: ");
        double surface = inputValidator.getValidDoubleInput(scanner, "Surface area (m²): ");
        LocalDate startDate = inputValidator.getValidDateInput(scanner, "Start date (YYYY-MM-DD): ");
        ProjectStatus status = getValidProjectStatus();

        Client client = getOrCreateClient();

        Project project = new Project(projectName, surface, startDate, status, client);

        boolean addingComponents = true;
        while (addingComponents) {
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
                    addingComponents = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        Optional<Project> createdProject = projectService.createProject(project);
        createdProject.ifPresent(p -> System.out.println("Project created successfully: " + p));
        if (!createdProject.isPresent()) {
            System.out.println("Failed to create project.");
        }
    }

    private Client getOrCreateClient() {
        System.out.println("\n=== Client Information ===");
        String clientName = inputValidator.getValidStringInput(scanner, "Client name: ");
        Optional<Client> existingClient = clientService.getClientByName(clientName);
        return existingClient.orElseGet(() -> {
            System.out.println("Client not found. Let's create a new client.");
            String email = inputValidator.getValidEmailInput(scanner, "Client email: ");
            String address = inputValidator.getValidStringInput(scanner, "Client address: ");
            String phone = inputValidator.getValidPhoneInput(scanner, "Client phone number: ");
            boolean isProfessional = inputValidator.getValidBooleanInput(scanner, "Is the client professional? (yes/no): ");
            double discountRate = inputValidator.getValidDoubleInput(scanner, "Client discount rate (0-1): ");
            Client newClient = new Client(clientName, email, address, phone, isProfessional, discountRate);
            clientService.createClient(newClient);
            System.out.println("New client created successfully!");
            return newClient;
        });
    }

    private void addMaterialToProject(Project project) {
        String materialName = inputValidator.getValidStringInput(scanner, "Material name: ");
        double quantity = inputValidator.getValidDoubleInput(scanner, "Quantity: ");
        String componentType = inputValidator.getValidStringInput(scanner, "Component type: ");
        double unitCost = inputValidator.getValidDoubleInput(scanner, "Unit cost: ");

        Material material = new Material(materialName, quantity, componentType, unitCost);
        project.addMaterial(material);
        System.out.println("Material added to the project.");
    }

    private void addLaborToProject(Project project) {
        String laborName = inputValidator.getValidStringInput(scanner, "Labor name: ");
        double hoursWorked = inputValidator.getValidDoubleInput(scanner, "Hours worked: ");
        double hourlyRate = inputValidator.getValidDoubleInput(scanner, "Hourly rate: ");
        double workerProductivity = inputValidator.getValidDoubleInput(scanner, "Worker productivity (0-1): ");

        Labor labor = new Labor(laborName, hoursWorked, hourlyRate, workerProductivity);
        project.addLabor(labor);
        System.out.println("Labor added to the project.");
    }

    private void viewAndManageProjects() {
        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        System.out.println("\n=== Existing Projects ===");
        IntStream.range(0, projects.size())
                .forEach(i -> System.out.println((i + 1) + ". " + projects.get(i).getName()));

        int projectIndex = inputValidator.getValidIntInput(scanner, "Select a project to manage (0 to cancel): ") - 1;
        if (projectIndex == -1 || projectIndex >= projects.size()) {
            return;
        }

        Project selectedProject = projects.get(projectIndex);
        manageProject(selectedProject);
    }

    private void manageProject(Project project) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== Managing Project: " + project.getName() + " ===");
            System.out.println("1. View project details");
            System.out.println("2. Update project status");
            System.out.println("3. Add material");
            System.out.println("4. Add labor");
            System.out.println("5. Calculate total cost");
            System.out.println("6. Generate quote");
            System.out.println("7. Return to project list");

            int choice = inputValidator.getValidIntInput(scanner, "Choose an option: ");

            switch (choice) {
                case 1:
                    viewProjectDetails(project);
                    break;
                case 2:
                    updateProjectStatus(project);
                    break;
                case 3:
                    addMaterialToProject(project);
                    break;
                case 4:
                    addLaborToProject(project);
                    break;
                case 5:
                    calculateAndDisplayTotalCost(project);
                    break;
                case 6:
                    generateAndDisplayQuote(project);
                    break;
                case 7:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
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
        project.setStatus(newStatus);
        Optional<Project> updatedProject = projectService.updateProject(project);
        updatedProject.ifPresent(p -> System.out.println("Project status updated successfully: " + p.getStatus()));
        if (!updatedProject.isPresent()) {
            System.out.println("Failed to update project status.");
        }
    }

    private void calculateAndDisplayTotalCost(Project project) {
        double totalCost = costCalculator.calculateTotalCost(project);
        System.out.println("Total cost for project " + project.getName() + ": " + String.format("%.2f", totalCost) + " €");
    }

    private void generateAndDisplayQuote(Project project) {
        Quote quote = quoteGenerator.generateQuote(project);
        System.out.println("\nQuote generated successfully!");
        System.out.println(quote.toString());
        System.out.println("\nDetailed quote content:");
        System.out.println(quote.getContent());
        
        boolean saveQuote = inputValidator.getValidBooleanInput(scanner, "Do you want to save this quote? (yes/no): ");
        if (saveQuote) {
            Optional<Quote> savedQuote = quoteGenerator.saveQuote(quote);
            savedQuote.ifPresent(q -> System.out.println("Quote saved successfully: " + q));
            if (!savedQuote.isPresent()) {
                System.out.println("Failed to save quote.");
            }
        }
    }

    private void deleteProject() {
        String projectName = inputValidator.getValidStringInput(scanner, "Enter the name of the project to delete: ");
        Optional<Project> projectToDelete = projectService.getProjectByName(projectName);
        projectToDelete.ifPresent(this::confirmAndDeleteProject);
        if (!projectToDelete.isPresent()) {
            System.out.println("Project not found.");
        }
    }

    private void confirmAndDeleteProject(Project project) {
        System.out.println("Are you sure you want to delete this project? " + project.getName());
        boolean confirm = inputValidator.getValidBooleanInput(scanner, "Confirm deletion (yes/no): ");
        
        if (confirm) {
            Optional<Project> deletedProject = projectService.deleteProject(project.getId());
            deletedProject.ifPresent(p -> System.out.println("Project deleted successfully: " + p.getName()));
            if (!deletedProject.isPresent()) {
                System.out.println("Failed to delete project.");
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
            case 1:
                generateNewQuote();
                break;
            case 2:
                viewExistingQuotes();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void generateNewQuote() {
        String projectName = inputValidator.getValidStringInput(scanner, "Enter the name of the project to generate a quote for: ");
        Optional<Project> projectForQuote = projectService.getProjectByName(projectName);
        projectForQuote.ifPresent(this::generateAndDisplayQuote);
        if (!projectForQuote.isPresent()) {
            System.out.println("Project not found.");
        }
    }

    private void viewExistingQuotes() {
        // This method would need to be implemented if we want to view existing quotes
        System.out.println("Viewing existing quotes is not implemented yet.");
    }
}