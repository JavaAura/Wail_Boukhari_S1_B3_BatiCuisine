package com.baticuisine.repository;

import java.util.List;
import java.util.Optional;

import com.baticuisine.model.Client;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(Long id);
    List<Client> findAll();
    List<Client> findByName(String name);
    void delete(Long id);
    void update(Client client);
    List<Client> findByNameAndPhone(String name, String phone);
}
