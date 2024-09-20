package com.baticuisine.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quote {
    private UUID id;
    private Project project;
    private double estimatedAmount;
    private LocalDate issueDate;
    private LocalDate validityDate;
    private boolean accepted;
    private String content;
    private List<Component> components;

    public Quote(Project project, double estimatedAmount, LocalDate issueDate, LocalDate validityDate) {
        this.id = UUID.randomUUID();
        this.project = project;
        this.estimatedAmount = estimatedAmount;
        this.issueDate = issueDate;
        this.validityDate = validityDate;
        this.accepted = false;
        this.components = new ArrayList<>();
    }

    // Getters and setters
    public UUID getId() { return id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public double getEstimatedAmount() { return estimatedAmount; }
    public void setEstimatedAmount(double estimatedAmount) { this.estimatedAmount = estimatedAmount; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getValidityDate() { return validityDate; }
    public void setValidityDate(LocalDate validityDate) { this.validityDate = validityDate; }

    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public void addComponent(Component component) {
        this.components.add(component);
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", project=" + project.getName() +
                ", estimatedAmount=" + estimatedAmount +
                ", issueDate=" + issueDate +
                ", validityDate=" + validityDate +
                ", accepted=" + accepted +
                ", components=" + components +
                '}';
    }
}