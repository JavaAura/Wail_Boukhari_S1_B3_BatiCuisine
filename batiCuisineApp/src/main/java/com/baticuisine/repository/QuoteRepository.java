package com.baticuisine.repository;

import java.util.List;
import java.util.Optional;

import com.baticuisine.model.Quote;

public interface QuoteRepository {
    Quote save(Quote quote);
    Optional<Quote> findById(Long id);
    List<Quote> findAll();
    List<Quote> findByProjectId(Long projectId);
    void delete(Long id);
    void update(Quote quote);
}