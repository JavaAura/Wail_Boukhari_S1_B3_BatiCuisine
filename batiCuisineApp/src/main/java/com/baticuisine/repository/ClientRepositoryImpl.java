package com.baticuisine.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Client;

public class ClientRepositoryImpl implements ClientRepository {
    private static final Logger LOGGER = Logger.getLogger(ClientRepositoryImpl.class.getName());
    private static ClientRepositoryImpl instance;
    private final Connection connection;

    private ClientRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    public static synchronized ClientRepositoryImpl getInstance(Connection connection) {
        if (instance == null) {
            instance = new ClientRepositoryImpl(connection);
        }
        return instance;
    }
    @Override
    public Client save(Client client) {
        String sql = "INSERT INTO clients (name, email, address, phone_number, is_professional, discount_rate) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getEmail());
            pstmt.setString(3, client.getAddress());
            pstmt.setString(4, client.getPhoneNumber());
            pstmt.setBoolean(5, client.isProfessional());
            pstmt.setDouble(6, client.getDiscountRate());
    
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating client failed, no rows affected.");
            }
    
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating client failed, no ID obtained.");
                }
            }
            LOGGER.info("Client saved: " + client.getName());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving client", e);
            throw new RuntimeException("Error saving client", e);
        }
        return client;
    }

    @Override
    public List<Client> findByNameAndPhone(String name, String phone) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE name LIKE ? AND phone_number LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            pstmt.setString(2, "%" + phone + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding clients by name and phone", e);
        }
        return clients;
    }
    @Override
    public Optional<Client> findById(Long id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                LOGGER.info("Client found with id: " + id);
                return Optional.of(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding client by id", e);
            throw new RuntimeException("Error finding client by id", e);
        }
        LOGGER.warning("Client not found with id: " + id);
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all clients", e);
        }
        return clients;
    }

    @Override
    public List<Client> findByName(String name) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE name LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding clients by name", e);
        }
        return clients;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting client", e);
        }
    }

    @Override
    public void update(Client client) {
        String sql = "UPDATE clients SET name = ?, email = ?, address = ?, phone_number = ?, is_professional = ?, discount_rate = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getEmail());
            pstmt.setString(3, client.getAddress());
            pstmt.setString(4, client.getPhoneNumber());
            pstmt.setBoolean(5, client.isProfessional());
            pstmt.setDouble(6, client.getDiscountRate());
            pstmt.setLong(7, client.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating client", e);
        }
    }

    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        Client client = new Client(
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("address"),
            rs.getString("phone_number"),
            rs.getBoolean("is_professional"),
            rs.getDouble("discount_rate")
        );
        client.setId(rs.getLong("id"));
        return client;
    }
}