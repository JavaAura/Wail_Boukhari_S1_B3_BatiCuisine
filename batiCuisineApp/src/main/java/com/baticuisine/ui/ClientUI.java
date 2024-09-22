package com.baticuisine.ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;

import com.baticuisine.model.Client;
import com.baticuisine.service.ClientService;
import com.baticuisine.utils.InputValidator;

public class ClientUI {
    private static final Logger LOGGER = Logger.getLogger(ClientUI.class.getName());
    private final Scanner scanner;
    private final ClientService clientService;
    private final InputValidator inputValidator;

    public ClientUI(ClientService clientService, InputValidator inputValidator) {
        this.scanner = new Scanner(System.in);
        this.clientService = clientService;
        this.inputValidator = inputValidator;
    }

    public void manageClients() {
        while (true) {
            displayMenu();
            int choice = inputValidator.getValidIntInput(scanner, "Choose an option: ");
            if (!handleMenuChoice(choice)) {
                break;
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n=== Client Management ===");
        System.out.println("1. Add a new client");
        System.out.println("2. View all clients");
        System.out.println("3. Update a client");
        System.out.println("4. Delete a client");
        System.out.println("5. Return to main menu");
    }

    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1: addNewClient(); return true;
            case 2: viewAllClients(); return true;
            case 3: updateClient(); return true;
            case 4: deleteClient(); return true;
            case 5: return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return true;
        }
    }

    private void addNewClient() {
        System.out.println("\n=== Add a New Client ===");
        Client newClient = createClientFromInput();
        Optional<Client> createdClient = clientService.createClient(newClient);
        createdClient.ifPresent(c -> System.out.println("Client added successfully: " + c));
        if (!createdClient.isPresent()) {
            System.out.println("Failed to add client.");
        }
    }

    private Client createClientFromInput() {
        String name = inputValidator.getValidStringInput(scanner, "Client name: ");
        String email = inputValidator.getValidEmailInput(scanner, "Client email: ");
        String address = inputValidator.getValidStringInput(scanner, "Client address: ");
        String phone = inputValidator.getValidPhoneInput(scanner, "Client phone number: ");
        boolean isProfessional = inputValidator.getValidBooleanInput(scanner, "Is the client professional? (yes/no): ");
        double discountRate = inputValidator.getValidDoubleInput(scanner, "Client discount rate (0-1): ");
        return new Client(name, email, address, phone, isProfessional, discountRate);
    }

    private void viewAllClients() {
        System.out.println("\n=== All Clients ===");
        List<Client> clients = clientService.getAllClients();
        if (clients.isEmpty()) {
            System.out.println("No clients found.");
        } else {
            clients.forEach(System.out::println);
        }
    }

    private void updateClient() {
        System.out.println("\n=== Update a Client ===");
        String name = inputValidator.getValidStringInput(scanner, "Enter the name of the client to update: ");
        Optional<Client> clientToUpdate = clientService.getClientByName(name);
        if (clientToUpdate.isPresent()) {
            updateClientDetails(clientToUpdate.get());
        } else {
            System.out.println("Client not found.");
        }
    }

    private void updateClientDetails(Client client) {
        System.out.println("Current client details: " + client);
        updateClientFields(client);
        Optional<Client> updatedClient = clientService.updateClient(client);
        updatedClient.ifPresent(c -> System.out.println("Client updated successfully: " + c));
        if (!updatedClient.isPresent()) {
            System.out.println("Failed to update client.");
        }
    }

    private void updateClientFields(Client client) {
        String newEmail = inputValidator.getValidEmailInput(scanner, "New email (press enter to keep current): ");
        String newPhone = inputValidator.getValidPhoneInput(scanner, "New phone number (press enter to keep current): ");
        double newDiscountRate = inputValidator.getValidDoubleInput(scanner, "New discount rate (0-1, press enter to keep current): ");

        if (!newEmail.isEmpty()) {
            client.setEmail(newEmail);
        }
        if (!newPhone.isEmpty()) {
            client.setPhoneNumber(newPhone);
        }
        if (newDiscountRate >= 0) {
            client.setDiscountRate(newDiscountRate);
        }
    }

    private void deleteClient() {
        System.out.println("\n=== Delete a Client ===");
        String name = inputValidator.getValidStringInput(scanner, "Enter the name of the client to delete: ");
        Optional<Client> clientToDelete = clientService.getClientByName(name);
        if (clientToDelete.isPresent()) {
            confirmAndDeleteClient(clientToDelete.get());
        } else {
            System.out.println("Client not found.");
        }
    }

    private void confirmAndDeleteClient(Client client) {
        System.out.println("Are you sure you want to delete this client? " + client);
        boolean confirm = inputValidator.getValidBooleanInput(scanner, "Confirm deletion (yes/no): ");

        if (confirm) {
            Optional<Client> deletedClient = clientService.deleteClient(client.getName());
            deletedClient.ifPresent(c -> System.out.println("Client deleted successfully: " + c));
            if (!deletedClient.isPresent()) {
                System.out.println("Failed to delete client.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
}