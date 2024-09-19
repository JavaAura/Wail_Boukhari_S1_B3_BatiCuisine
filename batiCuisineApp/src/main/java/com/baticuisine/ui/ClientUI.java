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
    private Scanner scanner;
    private ClientService clientService;
    private InputValidator inputValidator;

    public ClientUI(ClientService clientService, InputValidator inputValidator) {
        this.scanner = new Scanner(System.in);
        this.clientService = clientService;
        this.inputValidator = inputValidator;
    }

    public void manageClients() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Gestion des clients ===");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Afficher tous les clients");
            System.out.println("3. Mettre à jour un client");
            System.out.println("4. Supprimer un client");
            System.out.println("5. Retour au menu principal");

            int choice = inputValidator.getValidIntInput(scanner, "Choisissez une option : ");

            switch (choice) {
                case 1:
                    addClient();
                    break;
                case 2:
                    displayAllClients();
                    break;
                case 3:
                    updateClient();
                    break;
                case 4:
                    deleteClient();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void addClient() {
        LOGGER.info("Starting new client creation process");
        System.out.println("=== Ajout d'un nouveau client ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du client : ");
        String email = inputValidator.getValidEmailInput(scanner, "Email du client : ");
        String phone = inputValidator.getValidPhoneInput(scanner, "Numéro de téléphone du client : ");

        Client newClient = new Client(name, email, phone);
        clientService.createClient(newClient);
        LOGGER.info("New client created: " + name);
        System.out.println("Client ajouté avec succès !");
    }

    private void displayAllClients() {
        LOGGER.info("Displaying all clients");
        System.out.println("=== Liste de tous les clients ===");
        List<Client> clients = clientService.getAllClients();
        clients.forEach(System.out::println);
    }

    private void updateClient() {
        LOGGER.info("Starting client update process");
        System.out.println("=== Mise à jour d'un client ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du client à mettre à jour : ");

        Optional<Client> clientOpt = clientService.getClientByName(name);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            String email = inputValidator.getValidEmailInput(scanner, "Nouvel email du client : ");
            String phone = inputValidator.getValidPhoneInput(scanner, "Nouveau numéro de téléphone du client : ");

            client.setEmail(email);
            client.setPhone(phone);

            clientService.updateClient(client);
            LOGGER.info("Client updated: " + name);
            System.out.println("Client mis à jour avec succès !");
        } else {
            System.out.println("Client non trouvé.");
        }
    }

    private void deleteClient() {
        LOGGER.info("Starting client deletion process");
        System.out.println("=== Suppression d'un client ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du client à supprimer : ");

        clientService.deleteClient(name);
        LOGGER.info("Client deleted: " + name);
        System.out.println("Client supprimé avec succès !");
    }
}