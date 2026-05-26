package com.salon.salon_backend.controller;

import com.salon.salon_backend.dto.CreateAppointmentRequest;
import com.salon.salon_backend.dto.MonthlyRevenueDTO;
import com.salon.salon_backend.dto.OwnerDashboardResponse;
import com.salon.salon_backend.entity.Appointment;
import com.salon.salon_backend.entity.Salon;
import com.salon.salon_backend.entity.SalonClosedDate;
import com.salon.salon_backend.entity.User;
import com.salon.salon_backend.repository.SalonClosedDateRepository;
import com.salon.salon_backend.repository.SalonRepository;
import com.salon.salon_backend.repository.UserRepository;

import com.salon.salon_backend.service.AppointmentService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalonClosedDateRepository
            closedDateRepository;

    @Autowired
    private SalonRepository
            salonRepository;
    // CREATE APPOINTMENT
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public Appointment createAppointment(
            @RequestBody CreateAppointmentRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return appointmentService.createAppointment(
                request,
                customer
        );
    }

    // CUSTOMER BOOKING HISTORY
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Appointment> getMyAppointments(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return appointmentService.getCustomerAppointments(
                customer
        );
    }
    
    @GetMapping("/owner")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public List<Appointment> getOwnerAppointments(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User Not Found"
                        ));

        return appointmentService
                .getOwnerAppointments(owner);
    }
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public OwnerDashboardResponse getOwnerDashboard(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User Not Found"
                        ));

        return appointmentService
                .getOwnerDashboard(owner);
    }
    
    @GetMapping("/{appointmentId}")
    public Appointment getAppointmentById(
            @PathVariable Long appointmentId
    ) {

        return appointmentService
                .getAppointmentById(appointmentId);
    }
    
   
    @PutMapping("/{appointmentId}/status")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public Appointment updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        String status = request.get("status");

        return appointmentService.updateAppointmentStatus(
                appointmentId,
                status,
                owner
        );
    }
    
    @GetMapping("/salon/{salonId}")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public List<Appointment> getSalonAppointments(
            @PathVariable Long salonId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return appointmentService.getSalonAppointments(
                salonId,
                owner
        );
    }
    
    @GetMapping("/available-slots")
    public List<String> getAvailableSlots(

            @RequestParam Long salonId,

            @RequestParam LocalDate date
    ) {

        return appointmentService.getAvailableSlots(
                salonId,
                date
        );
    }
    
    @GetMapping("/monthly-revenue")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public List<MonthlyRevenueDTO> getMonthlyRevenue(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return appointmentService
                .getMonthlyRevenue(owner);
    }
    
    @PostMapping("/closed-date")
    @PreAuthorize("hasRole('SALON_OWNER')")
    public SalonClosedDate addClosedDate(

            @RequestParam Long salonId,

            @RequestParam String date,

            Authentication authentication
    ) {

        String email =
                authentication.getName();

        User owner =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User Not Found"
                                ));

        Salon salon =
                salonRepository.findById(salonId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Salon Not Found"
                                ));

        if (
                !salon.getOwner()
                        .getId()
                        .equals(owner.getId())
        ) {

            throw new RuntimeException(
                    "Unauthorized"
            );
        }

        SalonClosedDate closedDate =
                new SalonClosedDate();

        closedDate.setSalon(salon);

        closedDate.setClosedDate(
                LocalDate.parse(date)
        );

        return closedDateRepository
                .save(closedDate);
    }
    
    @PutMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Appointment cancelAppointment(

            @PathVariable Long appointmentId,

            Authentication authentication
    ) {

        String email =
                authentication.getName();

        User customer =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User Not Found"
                                ));

        return appointmentService
                .cancelAppointment(
                        appointmentId,
                        customer
                );
    }
}
