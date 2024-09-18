package com.baticuisine.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.baticuisine.model.Client;

public class ClientRepository {
    private static final Logger LOGGER = Logger.getLogger(ClientRepository.class.getName());
    private final Connection connection;

    public ClientRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Client client) {
        String sql = "INSERT INTO clients (id, name, email, phone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, client.getId());
            pstmt.setString(2, client.getName());
            pstmt.setString(3, client.getEmail());
            pstmt.setString(4, client.getPhone());
            pstmt.executeUpdate();
            LOGGER.info("Client saved: " + client.getName());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving client", e);
            throw new RuntimeException("Error saving client", e);
        }
    }

    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all clients", e);
            throw new RuntimeException("Error finding all clients", e);
        }
        return clients;
    }

    public Optional<Client> findByName(String name) {
        String sql = "SELECT * FROM clients WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClient(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding client by name", e);
            throw new RuntimeException("Error finding client by name", e);
        }
        return Optional.empty();
    }

    public void update(Client client) {
        String sql = "UPDATE clients SET email = ?, phone = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.getEmail());
            pstmt.setString(2, client.getPhone());
            pstmt.setObject(3, client.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Client updated: " + client.getName());
            } else {
                LOGGER.warning("Client not found for update: " + client.getName());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating client", e);
            throw new RuntimeException("Error updating client", e);
        }
    }

    public void delete(String clientName) {
        String sql = "DELETE FROM clients WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, clientName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Client deleted: " + clientName);
            } else {
                LOGGER.warning("Client not found for deletion: " + clientName);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting client", e);
            throw new RuntimeException("Error deleting client", e);
        }
    }

    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        return new Client(
            (UUID) rs.getObject("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone")
        );
    }
}
