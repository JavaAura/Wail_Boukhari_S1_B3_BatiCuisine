package com.baticuisine.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.baticuisine.model.Client;
import com.baticuisine.repository.ClientRepository;

public class ClientService {
    private static final Logger LOGGER = Logger.getLogger(ClientService.class.getName());
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void createClient(Client client) {
        try {
            clientRepository.save(client);
            LOGGER.info("Client created: " + client.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating client", e);
            throw new RuntimeException("Failed to create client", e);
        }
    }

    public List<Client> getAllClients() {
        try {
            return clientRepository.findAll();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all clients", e);
            throw new RuntimeException("Failed to retrieve clients", e);
        }
    }

    public Optional<Client> getClientByName(String name) {
        try {
            return clientRepository.findByName(name);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving client by name", e);
            throw new RuntimeException("Failed to retrieve client", e);
        }
    }

    public void updateClient(Client client) {
        try {
            clientRepository.update(client);
            LOGGER.info("Client updated: " + client.getName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating client", e);
            throw new RuntimeException("Failed to update client", e);
        }
    }

    public void deleteClient(String clientName) {
        try {
            clientRepository.delete(clientName);
            LOGGER.info("Client deleted: " + clientName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting client", e);
            throw new RuntimeException("Failed to delete client", e);
        }
    }

    public int getTotalClientCount() {
        return getAllClients().size();
    }

    public List<Client> getProfessionalClients() {
        return getAllClients().stream()
                .filter(Client::isProfessional)
                .collect(Collectors.toList());
    }
}