package com.baticuisine.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.baticuisine.model.Client;
import com.baticuisine.repository.ClientRepository;

public class ClientService {
    private static final Logger LOGGER = Logger.getLogger(ClientService.class.getName());
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void createClient(Client client) {
        clientRepository.save(client);
        LOGGER.info("Client created: " + client.getName());
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientByName(String name) {
        return clientRepository.findByName(name);
    }

    public void updateClient(Client client) {
        clientRepository.update(client);
        LOGGER.info("Client updated: " + client.getName());
    }

    public void deleteClient(String clientName) {
        clientRepository.delete(clientName);
        LOGGER.info("Client deleted: " + clientName);
    }
}