package com.salon.salon_backend.repository;

import com.salon.salon_backend.entity.SalonClosedDate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SalonClosedDateRepository
        extends JpaRepository<
        SalonClosedDate,
        Long
        > {

    boolean existsBySalonIdAndClosedDate(
            Long salonId,
            LocalDate closedDate
    );
}