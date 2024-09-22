package com.baticuisine.model;

import java.time.LocalDate;

public class Quote {
    private Long id;
    private double estimatedAmount;
    private LocalDate issueDate;
    private LocalDate validityDate;
    private boolean accepted;
    private Project project;
    private String content;

    public Quote(double estimatedAmount, LocalDate issueDate, LocalDate validityDate, Project project) {
        this.estimatedAmount = estimatedAmount;
        this.issueDate = issueDate;
        this.validityDate = validityDate;
        this.accepted = false;
        this.project = project;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public double getEstimatedAmount() { return estimatedAmount; }
    public void setEstimatedAmount(double estimatedAmount) { this.estimatedAmount = estimatedAmount; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getValidityDate() { return validityDate; }
    public void setValidityDate(LocalDate validityDate) { this.validityDate = validityDate; }
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public void setContent(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    public boolean isValid() {
        return LocalDate.now().isBefore(validityDate) || LocalDate.now().isEqual(validityDate);
    }

    @Override
    public String toString() {
        return String.format("Quote for project '%s': %.2f €, Issued: %s, Valid until: %s, Status: %s",
                project.getProjectName(), estimatedAmount, issueDate, validityDate, 
                accepted ? "Accepted" : (isValid() ? "Pending" : "Expired"));
    }
}