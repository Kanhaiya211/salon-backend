package com.salon.salon_backend.service;

import com.salon.salon_backend.dto.CreateAppointmentRequest;
import com.salon.salon_backend.dto.MonthlyRevenueDTO;
import com.salon.salon_backend.dto.OwnerDashboardResponse;
import com.salon.salon_backend.entity.Appointment;
import com.salon.salon_backend.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    Appointment createAppointment(
            CreateAppointmentRequest request,
            User customer
    );

    List<Appointment> getCustomerAppointments(User customer);
    
    Appointment getAppointmentById(Long appointmentId);
    
    List<Appointment> getOwnerAppointments(User owner);
    
    List<Appointment> getSalonAppointments(
            Long salonId,
            User owner
    );
    
    Appointment updateAppointmentStatus(
            Long appointmentId,
            String status,
            User owner
    );
    
    List<String> getAvailableSlots(
            Long salonId,
            LocalDate date
    );
    
    OwnerDashboardResponse getOwnerDashboard(
            User owner
    );
    
    List<MonthlyRevenueDTO> getMonthlyRevenue(
            User owner
    );
    
    Appointment cancelAppointment(
            Long appointmentId,
            User customer
    );
}