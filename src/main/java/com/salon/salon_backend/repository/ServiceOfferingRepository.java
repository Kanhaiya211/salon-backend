package com.salon.salon_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salon.salon_backend.entity.Salon;
import com.salon.salon_backend.entity.ServiceOffering;

public interface ServiceOfferingRepository
        extends JpaRepository<ServiceOffering, Long> {
	List<ServiceOffering> findBySalon(Salon salon);
}