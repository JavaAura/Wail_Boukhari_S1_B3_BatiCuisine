package com.baticuisine.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Quote;

public class QuoteRepository {
    private static final Logger LOGGER = Logger.getLogger(QuoteRepository.class.getName());
    private final Connection connection;

    public QuoteRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveQuote(Quote quote) {
        String sql = "INSERT INTO quotes (id, project_id, estimated_amount, issue_date, validity_date, accepted, content) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, quote.getId());
            pstmt.setObject(2, quote.getProject().getId());
            pstmt.setDouble(3, quote.getEstimatedAmount());
            pstmt.setDate(4, Date.valueOf(quote.getIssueDate()));
            pstmt.setDate(5, Date.valueOf(quote.getValidityDate()));
            pstmt.setBoolean(6, quote.isAccepted());
            pstmt.setString(7, quote.getContent());
            pstmt.executeUpdate();
            LOGGER.info("Quote saved: " + quote.getId());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving quote", e);
            throw new RuntimeException("Error saving quote", e);
        }
    }
}