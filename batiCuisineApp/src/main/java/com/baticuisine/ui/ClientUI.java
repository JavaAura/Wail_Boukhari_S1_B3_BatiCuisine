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
        if (createdClient.isPresent()) {
            Client c = createdClient.get();
            System.out.println("Client added successfully: " + c);
            LOGGER.info("Client added successfully: " + c);
        } else {
            System.out.println("Failed to add client.");
            LOGGER.warning("Failed to add client.");
        }
    }
    private Client createClientFromInput() {
        String name = inputValidator.getValidStringInput(scanner, "Nom du client (ex: John Doe) : ");
        String email = inputValidator.getValidEmailInput(scanner, "Email du client (ex: john.doe@example.com) : ");
        String address = inputValidator.getValidStringInput(scanner, "Adresse du client (ex: 123 Rue Principale) : ");
        String phone = inputValidator.getValidPhoneInput(scanner, "Numéro de téléphone du client (ex: +33123456789) : ");
        boolean isProfessional = inputValidator.getValidBooleanInput(scanner, "Le client est-il professionnel ? (oui/non) : ");
        double discountRate = inputValidator.getValidDoubleInput(scanner, "Taux de remise du client (0-1, ex: 0.1 pour 10%) : ");
        return new Client(name, email, address, phone, isProfessional, discountRate);
    }

    private void viewAllClients() {
        System.out.println("\n=== All Clients ===");
        List<Client> clients = clientService.getAllClients();
        if (clients.isEmpty()) {
            System.out.println("No clients found.");
            LOGGER.info("No clients found.");
        } else {
            clients.forEach(client -> {
                System.out.println(client);
                LOGGER.info("Client: " + client);
            });
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
            LOGGER.warning("Client not found: " + name);
        }
    }

    private void updateClientDetails(Client client) {
        System.out.println("Current client details: " + client);
        updateClientFields(client);
        Optional<Client> updatedClient = clientService.updateClient(client);
        if (updatedClient.isPresent()) {
            Client c = updatedClient.get();
            System.out.println("Client updated successfully: " + c);
            LOGGER.info("Client updated successfully: " + c);
        } else {
            System.out.println("Failed to update client.");
            LOGGER.warning("Failed to update client: " + client);
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
            LOGGER.warning("Client not found: " + name);
        }
    }

    private void confirmAndDeleteClient(Client client) {
        System.out.println("Are you sure you want to delete this client? " + client);
        boolean confirm = inputValidator.getValidBooleanInput(scanner, "Confirm deletion (yes/no): ");

        if (confirm) {
            Optional<Client> deletedClient = clientService.deleteClient(client.getName());
            if (deletedClient.isPresent()) {
                Client c = deletedClient.get();
                System.out.println("Client deleted successfully: " + c);
                LOGGER.info("Client deleted successfully: " + c);
            } else {
                System.out.println("Failed to delete client.");
                LOGGER.warning("Failed to delete client: " + client);
            }
        } else {
            System.out.println("Deletion cancelled.");
            LOGGER.info("Deletion cancelled for client: " + client);
        }
    }
}