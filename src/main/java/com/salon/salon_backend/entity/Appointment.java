package com.salon.salon_backend.entity;

import com.salon.salon_backend.enums.AppointmentStatus;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import jakarta.persistence.OneToOne;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.JoinColumn;
@Entity
@Table(name = "appointments")
@JsonIgnoreProperties({
    "hibernateLazyInitializer",
    "handler"
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CUSTOMER WHO BOOKED
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({
            "password",
            "createdAt"
    })
    private User customer;

    // SALON
    @ManyToOne
    @JoinColumn(name = "salon_id")
    @JsonIgnoreProperties({
            "owner"
    })
    private Salon salon;
    // MULTIPLE SERVICES
    @ManyToMany
    @JoinTable(
            name = "appointment_services",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceOffering> services;
    

    // DATE
    private LocalDate appointmentDate;

    // START TIME
    private LocalTime startTime;

    // END TIME
    private LocalTime endTime;

    // TOTAL PRICE
    private Double totalPrice;

    // TOTAL DURATION IN MINUTES
    private Integer totalDuration;

    // BOOKING STATUS
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    // OPTIONAL CUSTOMER NOTES
    @Column(length = 2000)
    private String notes;

    // CREATED TIME
    private LocalDateTime createdAt;
    
    @JsonManagedReference
    @OneToOne
    @JoinColumn(
            name = "payment_id"
    )
    private Payment payment;

    public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	@PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = AppointmentStatus.PENDING;
        }
    }

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public List<ServiceOffering> getServices() {
        return services;
    }

    public void setServices(List<ServiceOffering> services) {
        this.services = services;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}