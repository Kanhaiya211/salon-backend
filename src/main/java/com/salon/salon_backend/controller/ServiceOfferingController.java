package com.salon.salon_backend.controller;

import com.salon.salon_backend.entity.ServiceOffering;
import com.salon.salon_backend.entity.User;

import com.salon.salon_backend.repository.UserRepository;

import com.salon.salon_backend.service.ServiceOfferingService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:5173")
public class ServiceOfferingController {

    @Autowired
    private ServiceOfferingService serviceOfferingService;

    @Autowired
    private UserRepository userRepository;

    // CREATE SERVICE
    @PostMapping("/salon/{salonId}")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public ServiceOffering createService(
            @PathVariable Long salonId,
            @RequestBody ServiceOffering service,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return serviceOfferingService.createService(
                salonId,
                service,
                owner
        );
    }

    // GET SERVICES OF SALON
    @GetMapping("/salon/{salonId}")
    public List<ServiceOffering> getSalonServices(
            @PathVariable Long salonId
    ) {

        return serviceOfferingService.getSalonServices(salonId);
    }

    // UPDATE SERVICE
    @PutMapping("/{serviceId}")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public ServiceOffering updateService(
            @PathVariable Long serviceId,
            @RequestBody ServiceOffering updatedService,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return serviceOfferingService.updateService(
                serviceId,
                updatedService,
                owner
        );
    }

    // DELETE SERVICE
    @DeleteMapping("/{serviceId}")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public String deleteService(
            @PathVariable Long serviceId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        serviceOfferingService.deleteService(
                serviceId,
                owner
        );

        return "Service Deleted Successfully";
    }
}