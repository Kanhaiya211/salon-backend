package com.salon.salon_backend.controller;

import com.salon.salon_backend.entity.Salon;
import com.salon.salon_backend.entity.User;
import com.salon.salon_backend.repository.UserRepository;
import com.salon.salon_backend.service.SalonService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salons")
@CrossOrigin(origins = "http://localhost:5173")
public class SalonController {

    @Autowired
    private SalonService salonService;

    @Autowired
    private UserRepository userRepository;

    // CREATE SALON
    @PostMapping
    @PreAuthorize("hasRole('SALON_OWNER')")
    public Salon createSalon(
            @RequestBody Salon salon,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return salonService.createSalon(salon, owner);
    }

    // GET ALL SALONS
    @GetMapping
    public List<Salon> getAllSalons(

            @RequestParam(required = false) String city,

            @RequestParam(required = false) String search
    ) {

        if (city != null && !city.isEmpty()) {

            return salonService.searchByCity(city);
        }

        if (search != null && !search.isEmpty()) {

            return salonService.searchByName(search);
        }

        return salonService.getAllSalons();
    }

    // GET LOGGED-IN OWNER SALONS
    @GetMapping("/my-salons")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public List<Salon> getMySalons(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return salonService.getOwnerSalons(owner);
    }

    // GET SALON BY ID
    @GetMapping("/{id}")
    public Salon getSalonById(
            @PathVariable Long id
    ) {

        return salonService.getSalonById(id);
    }

    // UPDATE SALON
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public Salon updateSalon(
            @PathVariable Long id,
            @RequestBody Salon updatedSalon,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return salonService.updateSalon(
                id,
                updatedSalon,
                owner
        );
    }

    // DELETE SALON
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public String deleteSalon(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        salonService.deleteSalon(id, owner);

        return "Salon Deleted Successfully";
    }
}