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

    public Optional<Client> createClient(Client client) {
        try {
            Client savedClient = clientRepository.save(client);
            LOGGER.info("Client created: " + client.getName());
            return Optional.of(savedClient);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating client", e);
            return Optional.empty();
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
            List<Client> clients = clientRepository.findByName(name);
            return clients.stream().findFirst();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving client by name", e);
            return Optional.empty();
        }
    }

    public Optional<Client> updateClient(Client client) {
        try {
            clientRepository.update(client);
            LOGGER.info("Client updated: " + client.getName());
            return Optional.of(client);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating client", e);
            return Optional.empty();
        }
    }

    public Optional<Client> deleteClient(String clientName) {
        try {
            Optional<Client> clientToDelete = getClientByName(clientName);
            if (clientToDelete.isPresent()) {
                clientRepository.delete(clientToDelete.get().getId());
                LOGGER.info("Client deleted: " + clientName);
                return clientToDelete;
            } else {
                LOGGER.warning("Client not found for deletion: " + clientName);
                return Optional.empty();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting client", e);
            return Optional.empty();
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