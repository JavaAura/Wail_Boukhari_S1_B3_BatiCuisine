package com.baticuisine.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.baticuisine.model.enums.ProjectStatus;

public class Project {
    private Long id;
    private String projectName;
    private double profitMargin;
    private double totalCost;
    private ProjectStatus projectStatus;
    private Client client;
    private List<Component> components;
    private double surface;
    private LocalDate startDate;

    private List<Material> materials = new ArrayList<>();
    private List<Labor> laborItems = new ArrayList<>();

    public Project(String projectName, double surface, LocalDate startDate, ProjectStatus projectStatus, Client client) {
        this.projectName = projectName;
        this.surface = surface;
        this.startDate = startDate;
        this.projectStatus = projectStatus;
        this.client = client;
        this.components = new ArrayList<>();
        this.profitMargin = 0;
        this.totalCost = 0;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public double getProfitMargin() { return profitMargin; }
    public void setProfitMargin(double profitMargin) { this.profitMargin = profitMargin; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public ProjectStatus getProjectStatus() { return projectStatus; }
    public void setProjectStatus(ProjectStatus projectStatus) { this.projectStatus = projectStatus; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public List<Component> getComponents() { return components; }
    public double getSurface() { return surface; }
    public void setSurface(double surface) { this.surface = surface; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public void addComponent(Component component) {
        this.components.add(component);
    }

    public void calculateTotalCost() {
        double totalCost = components.stream()
                .mapToDouble(Component::calculateCost)
                .sum();
        this.totalCost = totalCost * (1 + profitMargin / 100);
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public List<Labor> getLaborItems() {
        return laborItems;
    }

    public void addMaterial(Material material) {
        this.materials.add(material);
        this.components.add(material);
    }

    public void addLabor(Labor labor) {
        this.laborItems.add(labor);
        this.components.add(labor);
    }

    @Override
    public String toString() {
        return String.format("Project: %s, Client: %s, Surface: %.2f m², Total Cost: %.2f €, Status: %s",
                projectName, client.getName(), surface, totalCost, projectStatus);
    }
}
