package com.salon.salon_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salon.salon_backend.entity.Salon;
import com.salon.salon_backend.entity.User;

public interface SalonRepository
        extends JpaRepository<Salon, Long> {
	List<Salon> findByOwner(User owner);
	
	List<Salon> findByCityContainingIgnoreCase(String city);

	List<Salon> findByNameContainingIgnoreCase(String keyword);
}