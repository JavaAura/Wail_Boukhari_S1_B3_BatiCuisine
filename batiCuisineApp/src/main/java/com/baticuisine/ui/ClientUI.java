package com.baticuisine.ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
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

            try {
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
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An error occurred while managing clients", e);
                System.out.println("Une erreur est survenue. Veuillez réessayer.");
            }
        }
    }

    private void addClient() {
        LOGGER.info("Starting new client creation process");
        System.out.println("=== Ajout d'un nouveau client ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du client : ");
        String email = inputValidator.getValidEmailInput(scanner, "Email du client : ");
        String phone = inputValidator.getValidPhoneInput(scanner, "Numéro de téléphone du client : ");
        String address = inputValidator.getValidStringInput(scanner, "Adresse du client : ");
        boolean isProfessional = inputValidator.getValidBooleanInput(scanner, "Le client est-il un professionnel ? (oui/non) : ");
    
        Client newClient = new Client(name, email, phone, address, isProfessional);
        clientService.createClient(newClient);
        LOGGER.info("New client created: " + name);
        System.out.println("Client ajouté avec succès !");
        if (isProfessional) {
            System.out.println("Ce client bénéficie d'une remise de " + (newClient.getDiscountRate() * 100) + "%");
        }
    }

    private void displayAllClients() {
        LOGGER.info("Displaying all clients");
        System.out.println("=== Liste de tous les clients ===");
        List<Client> clients = clientService.getAllClients();
        if (clients.isEmpty()) {
            System.out.println("Aucun client trouvé.");
        } else {
            for (Client client : clients) {
                System.out.println("--------------------");
                System.out.println("Nom: " + client.getName());
                System.out.println("Email: " + client.getEmail());
                System.out.println("Téléphone: " + client.getPhone());
                System.out.println("--------------------");
            }
        }
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

        Optional<Client> clientOpt = clientService.getClientByName(name);
        if (clientOpt.isPresent()) {
            boolean confirm = inputValidator.getValidBooleanInput(scanner, "Êtes-vous sûr de vouloir supprimer ce client ? (oui/non) : ");
            if (confirm) {
                clientService.deleteClient(name);
                LOGGER.info("Client deleted: " + name);
                System.out.println("Client supprimé avec succès !");
            } else {
                System.out.println("Suppression annulée.");
            }
        } else {
            System.out.println("Client non trouvé.");
        }
    }
}