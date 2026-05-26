package com.salon.salon_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salon.salon_backend.entity.Payment;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {
}
