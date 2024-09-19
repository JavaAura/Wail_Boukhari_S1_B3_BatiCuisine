package com.baticuisine.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.baticuisine.model.enums.ProjectStatus;

public class Project {
    private UUID id;
    private String name;
    private double surface;
    private LocalDate startDate;
    private ProjectStatus status;
    private Client client;
    private List<Material> materials;
    private List<Labor> laborItems;


    public Project(String name, double surface, LocalDate startDate, ProjectStatus status) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.surface = surface;
        this.startDate = startDate;
        this.status = status;
        this.materials = new ArrayList<>();
        this.laborItems = new ArrayList<>();
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getSurface() { return surface; }
    public void setSurface(double surface) { this.surface = surface; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public List<Material> getMaterials() { return materials; }
    public void addMaterial(Material material) { this.materials.add(material); }

    public List<Labor> getLaborItems() { return laborItems; }
    public void addLaborItem(Labor labor) { this.laborItems.add(labor); }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surface=" + surface +
                ", startDate=" + startDate +
                ", status=" + status +
                ", client=" + (client != null ? client.getName() : "Not assigned") +
                ", materials=" + materials.size() +
                ", laborItems=" + laborItems.size() +
                '}';
    }
}
