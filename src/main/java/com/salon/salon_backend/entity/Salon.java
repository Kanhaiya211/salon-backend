package com.salon.salon_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name = "salons")
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String phone;


    @Column(length = 2000)
    private String description;

    private String image;
    
    private LocalTime openingTime;

    private LocalTime closingTime;
    
    @Column(name = "working_days")
    private String workingDays;
    
    private LocalDateTime createdAt;

    @ManyToOne
    @JsonIgnoreProperties({
        "password",
        "createdAt"
    })
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(
            mappedBy = "salon",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private java.util.List<ServiceOffering> services;


    @OneToMany(
            mappedBy = "salon",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private java.util.List<Appointment> appointments;
    // CONSTRUCTOR
    public Salon() {
        this.createdAt = LocalDateTime.now();
    }

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }
    
    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }
}