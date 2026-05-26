package com.salon.salon_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import jakarta.persistence.OneToOne;
@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy =
            GenerationType.IDENTITY)

    private Long id;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private Integer amount;
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(
            Integer amount
    ) {
        this.amount = amount;
    }
    private String status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        createdAt =
                LocalDateTime.now();
    }
    @JsonBackReference
    @OneToOne(mappedBy = "payment")
    private Appointment appointment;
}