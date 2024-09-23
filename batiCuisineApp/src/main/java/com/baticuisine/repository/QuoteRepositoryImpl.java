package com.baticuisine.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.baticuisine.model.Quote;

public class QuoteRepositoryImpl implements QuoteRepository {
    private final Connection connection;

    public QuoteRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Quote save(Quote quote) {
        String sql = "INSERT INTO quotes (total_cost, issue_date, validity_date, project_id, content) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, quote.getTotalCost());
            pstmt.setDate(2, Date.valueOf(quote.getIssueDate()));
            pstmt.setDate(3, Date.valueOf(quote.getValidityDate()));
            pstmt.setLong(4, quote.getProject().getId());
            pstmt.setString(5, quote.getContent());
    
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating quote failed, no rows affected.");
            }
    
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    quote.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating quote failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving quote", e);
        }
        return quote;
    }

    @Override
    public Optional<Quote> findById(Long id) {
        String sql = "SELECT * FROM quotes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToQuote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quote by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Quote> findAll() {
        List<Quote> quotes = new ArrayList<>();
        String sql = "SELECT * FROM quotes";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                quotes.add(mapResultSetToQuote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all quotes", e);
        }
        return quotes;
    }

    @Override
    public List<Quote> findByProjectId(Long projectId) {
        List<Quote> quotes = new ArrayList<>();
        String sql = "SELECT * FROM quotes WHERE project_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                quotes.add(mapResultSetToQuote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quotes by project id", e);
        }
        return quotes;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM quotes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting quote", e);
        }
    }

    @Override
    public void update(Quote quote) {
        String sql = "UPDATE quotes SET total_cost = ?, issue_date = ?, validity_date = ?, project_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, quote.getTotalCost());
            pstmt.setDate(2, Date.valueOf(quote.getIssueDate()));
            pstmt.setDate(3, Date.valueOf(quote.getValidityDate()));
            pstmt.setLong(4, quote.getProject().getId());
            pstmt.setLong(5, quote.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quote", e);
        }
    }

    private Quote mapResultSetToQuote(ResultSet rs) throws SQLException {
        Quote quote = new Quote(
            rs.getDouble("estimated_amount"),
            rs.getDate("issue_date").toLocalDate(),
            rs.getDate("validity_date").toLocalDate(),
            null
        );
        quote.setId(rs.getLong("id"));
        quote.setAccepted(rs.getBoolean("accepted"));
        return quote;
    }
}