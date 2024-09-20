package com.baticuisine.model;

import java.util.UUID;

public class Client {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private boolean isProfessional;

    public Client(UUID id, String name, String email, String phone, String address, boolean isProfessional) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isProfessional = isProfessional;
    }

    public Client(String name, String email, String phone, String address, boolean isProfessional) {
        this(UUID.randomUUID(), name, email, phone, address, isProfessional);
    }

    // Getters and setters
    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isProfessional() { return isProfessional; }
    public void setProfessional(boolean isProfessional) { this.isProfessional = isProfessional; }

    public double getDiscountRate() {
        return isProfessional ? 0.05 : 0.0;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", isProfessional=" + isProfessional +
                '}';
    }
}