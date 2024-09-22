package com.baticuisine.model;

public class Client {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private boolean isProfessional;
    private double discountRate;

    public Client(String name, String email, String address, String phoneNumber, boolean isProfessional, double discountRate) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isProfessional = isProfessional;
        this.discountRate = discountRate;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public boolean isProfessional() { return isProfessional; }
    public void setProfessional(boolean professional) { isProfessional = professional; }
    public double getDiscountRate() { return discountRate; }
    public void setDiscountRate(double discountRate) { this.discountRate = discountRate; }

    @Override
    public String toString() {
        return String.format("Client: %s, Email: %s, Address: %s, Phone: %s, Professional: %s, Discount Rate: %.2f",
                name, email, address, phoneNumber, isProfessional ? "Yes" : "No", discountRate);
    }
}