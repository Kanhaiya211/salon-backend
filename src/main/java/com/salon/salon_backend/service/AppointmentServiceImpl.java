package com.salon.salon_backend.service;

import com.salon.salon_backend.dto.CreateAppointmentRequest;
import com.salon.salon_backend.dto.MonthlyRevenueDTO;
import com.salon.salon_backend.dto.OwnerDashboardResponse;
import com.salon.salon_backend.entity.Appointment;
import com.salon.salon_backend.entity.Salon;
import com.salon.salon_backend.entity.ServiceOffering;
import com.salon.salon_backend.entity.User;

import com.salon.salon_backend.enums.AppointmentStatus;
import java.util.Arrays;
import com.salon.salon_backend.repository.AppointmentRepository;
import com.salon.salon_backend.repository.SalonClosedDateRepository;
import com.salon.salon_backend.repository.SalonRepository;
import com.salon.salon_backend.repository.ServiceOfferingRepository;

//import io.jsonwebtoken.lang.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentServiceImpl
        implements AppointmentService {
	
	@Autowired
	private EmailService emailService;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SalonRepository salonRepository;

    @Autowired
    private ServiceOfferingRepository serviceRepository;
    @Autowired
    private SalonClosedDateRepository
            closedDateRepository;
    @Override
    public Appointment createAppointment(
            CreateAppointmentRequest request,
            User customer
    ) {

        // FIND SALON
        Salon salon = salonRepository.findById(request.getSalonId())
                .orElseThrow(() ->
                        new RuntimeException("Salon Not Found"));

        // FETCH SELECTED SERVICES
        List<ServiceOffering> services =
                serviceRepository.findAllById(request.getServiceIds());
        if (services.size() != request.getServiceIds().size()) {

            throw new RuntimeException(
                    "One Or More Services Are Invalid"
            );
        }

        if (services.isEmpty()) {
            throw new RuntimeException("No Services Selected");
        }

        // VALIDATE SERVICES BELONG TO SAME SALON
        for (ServiceOffering service : services) {

            if (!service.getSalon().getId().equals(salon.getId())) {

                throw new RuntimeException(
                        "Service Does Not Belong To Salon"
                );
            }
        }

        // CALCULATE TOTAL PRICE
        double totalPrice = services.stream()
                .mapToDouble(ServiceOffering::getPrice)
                .sum();

        // CALCULATE TOTAL DURATION
        int totalDuration = services.stream()
                .mapToInt(ServiceOffering::getDuration)
                .sum();

        // CALCULATE END TIME
        LocalTime endTime = request.getStartTime()
                .plusMinutes(totalDuration);

        // CHECK TIME CONFLICTS
        List<Appointment> conflicts =
                appointmentRepository
                        .findBySalonAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThan(
                                salon,
                                request.getAppointmentDate(),
                                endTime,
                                request.getStartTime()
                        );

        boolean hasActiveConflict = false;

        for (Appointment conflict : conflicts) {

            if (
                    conflict.getStatus()
                            != AppointmentStatus.CANCELLED
            ) {

                hasActiveConflict = true;

                break;
            }
        }

        if (hasActiveConflict) {

            throw new RuntimeException(
                    "Selected Time Slot Is Already Booked"
            );
        }

        // CREATE APPOINTMENT
        Appointment appointment = new Appointment();

        appointment.setCustomer(customer);
        appointment.setSalon(salon);

        appointment.setServices(services);

        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );

        appointment.setStartTime(
                request.getStartTime()
        );

        appointment.setEndTime(endTime);

        appointment.setTotalPrice(totalPrice);

        appointment.setTotalDuration(totalDuration);

        appointment.setStatus(AppointmentStatus.PENDING);

        appointment.setNotes(request.getNotes());

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getCustomerAppointments(
            User customer
    ) {

        return appointmentRepository.findByCustomer(customer);
    }
    
    @Override
    public Appointment getAppointmentById(
            Long appointmentId
    ) {

        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Appointment Not Found"
                        ));
    }
    
    @Override
    public List<Appointment> getOwnerAppointments(
            User owner
    ) {

        return appointmentRepository.findBySalonOwner(owner);
    }
    
    @Override
    public List<Appointment> getSalonAppointments(
            Long salonId,
            User owner
    ) {

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() ->
                        new RuntimeException("Salon Not Found"));

        // OWNERSHIP CHECK
        if (!salon.getOwner().getId().equals(owner.getId())) {

            throw new RuntimeException("Unauthorized");
        }

        return appointmentRepository.findBySalon(salon);
    }
    @Override
    public Appointment updateAppointmentStatus(
            Long appointmentId,
            String status,
            User owner
    ) {

        System.out.println("STATUS RECEIVED = " + status);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException("Appointment Not Found"));

        System.out.println("APPOINTMENT FOUND");

        System.out.println(
                "OWNER ID = " + owner.getId()
        );

        System.out.println(
                "SALON OWNER ID = " +
                        appointment.getSalon()
                                .getOwner()
                                .getId()
        );

        // OWNERSHIP CHECK
        if (!appointment.getSalon()
                .getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new RuntimeException("Unauthorized");
        }

        System.out.println("OWNERSHIP VERIFIED");

        appointment.setStatus(
                com.salon.salon_backend.enums.AppointmentStatus
                        .valueOf(status.toUpperCase())
        );
        if (

                appointment.getStatus() == AppointmentStatus.CANCELLED

        ) {

            emailService.sendCancellationEmail(

                    appointment
                            .getCustomer()
                            .getEmail(),

                    appointment
                            .getCustomer()
                            .getName(),

                    appointment
                            .getSalon()
                            .getName()
            );

            emailService.sendOwnerCancellationEmail(

                    appointment
                            .getSalon()
                            .getOwner()
                            .getEmail(),

                    appointment
                            .getCustomer()
                            .getName(),

                    appointment
                            .getSalon()
                            .getName()
            );
        }

        System.out.println("STATUS UPDATED");

        return appointmentRepository.save(appointment);
    }
    @Override
    public List<String> getAvailableSlots(
            Long salonId,
            LocalDate date
    ) {

        // GET SALON

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Salon Not Found"
                        ));

        // SALON WORKING HOURS

        LocalTime openingTime =
                salon.getOpeningTime() != null
                        ? salon.getOpeningTime()
                        : LocalTime.of(10, 0);

        LocalTime closingTime =
                salon.getClosingTime() != null
                        ? salon.getClosingTime()
                        : LocalTime.of(20, 0);

        // WEEKLY OFF VALIDATION

        DayOfWeek day =
                date.getDayOfWeek();

        String workingDaysString =
                salon.getWorkingDays();

        if (
                workingDaysString == null
        ) {

            workingDaysString =
                    "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY";
        }

        List<String> workingDays =
                Arrays.asList(
                        workingDaysString.split(",")
                );

        if (
                !workingDays.contains(
                        day.toString()
                )
        ) {

            return new ArrayList<>();
        }

        // CUSTOM CLOSED DATE VALIDATION

        boolean isClosed =
                closedDateRepository
                        .existsBySalonIdAndClosedDate(
                                salonId,
                                date
                        );

        if (isClosed) {

            return new ArrayList<>();
        }

        // SLOT GAP

        int slotInterval = 30;

        // ALL POSSIBLE SLOTS

        List<LocalTime> allSlots =
                new ArrayList<>();

        LocalTime current = openingTime;

        LocalDate today =
                LocalDate.now();

        LocalTime now =
                LocalTime.now();

        while (
                current.isBefore(
                        closingTime
                )
        ) {

            // BLOCK PAST DATES

            if (
                    date.isBefore(today)
            ) {

                current =
                        current.plusMinutes(
                                slotInterval
                        );

                continue;
            }

            // BLOCK PAST TIME FOR TODAY

            if (
                    date.equals(today)
                            &&
                            current.isBefore(now)
            ) {

                current =
                        current.plusMinutes(
                                slotInterval
                        );

                continue;
            }

            allSlots.add(current);

            current =
                    current.plusMinutes(
                            slotInterval
                    );
        }

        // GET APPOINTMENTS OF DATE

        List<Appointment> appointments =
                appointmentRepository
                        .findBySalonIdAndAppointmentDate(
                                salonId,
                                date
                        );

        // REMOVE BOOKED SLOTS

        List<String> availableSlots =
                new ArrayList<>();

        for (
                LocalTime slot
                        : allSlots
        ) {

            boolean booked = false;

            for (
                    Appointment appointment
                            : appointments
            ) {
            	if (
            	        appointment.getStatus()
            	                == AppointmentStatus.CANCELLED
            	) {

            	    continue;
            	}

                LocalTime start =
                        appointment.getStartTime();

                LocalTime end =
                        appointment.getEndTime();

                // OVERLAP CHECK

                if (
                        !slot.isBefore(start)
                                &&
                                slot.isBefore(end)
                ) {

                    booked = true;

                    break;
                }
            }

            if (!booked) {

                availableSlots.add(
                        slot.toString()
                );
            }
        }

        return availableSlots;
    }
    
    @Override
    public OwnerDashboardResponse getOwnerDashboard(
            User owner
    ) {

        Long totalAppointments =
                appointmentRepository
                        .countBySalonOwnerId(
                                owner.getId()
                        );

        Double totalRevenue =
                appointmentRepository
                        .getTotalRevenue(
                                owner.getId()
                        );

        Long pendingAppointments =
                appointmentRepository
                        .countBySalonOwnerIdAndStatus(
                                owner.getId(),
                                AppointmentStatus.PENDING
                        );

        Long completedAppointments =
                appointmentRepository
                        .countBySalonOwnerIdAndStatus(
                                owner.getId(),
                                AppointmentStatus.COMPLETED
                        );

        Long todayAppointments =
                appointmentRepository
                        .countBySalonOwnerIdAndAppointmentDate(
                                owner.getId(),
                                LocalDate.now()
                        );

        return new OwnerDashboardResponse(
                totalAppointments,
                totalRevenue,
                pendingAppointments,
                completedAppointments,
                todayAppointments
        );
    }
    
//    @Override
//    public List<MonthlyRevenueDTO> getMonthlyRevenue(
//            User owner
//    ) {
//
//        return appointmentRepository
//                .getMonthlyRevenue(owner.getId());
//    }
    
    public List<MonthlyRevenueDTO> getMonthlyRevenue(
            User owner
    ) {

        List<Appointment> appointments =
                appointmentRepository
                        .getCompletedAppointments(
                                owner.getId()
                        );

        Map<String, Double> revenueMap =
                new LinkedHashMap<>();

        for (Appointment appointment : appointments) {

            String month =
                    appointment
                            .getAppointmentDate()
                            .getMonth()
                            .name();

            Double revenue =
                    revenueMap.getOrDefault(
                            month,
                            0.0
                    );

            revenue += appointment.getTotalPrice();

            revenueMap.put(month, revenue);
        }

        List<MonthlyRevenueDTO> result =
                new ArrayList<>();

        for (Map.Entry<String, Double> entry :
                revenueMap.entrySet()) {

            result.add(
                    new MonthlyRevenueDTO(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return result;
    }
    @Override
    public Appointment cancelAppointment(

            Long appointmentId,

            User customer
    ) {

        Appointment appointment =
                appointmentRepository
                        .findById(appointmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment Not Found"
                                ));

        // OWNERSHIP CHECK

        if (
                !appointment.getCustomer()
                        .getId()
                        .equals(customer.getId())
        ) {

            throw new RuntimeException(
                    "Unauthorized"
            );
        }

        // ALREADY COMPLETED

        if (
                appointment.getStatus()
                        == AppointmentStatus.COMPLETED
        ) {

            throw new RuntimeException(
                    "Completed Appointment Cannot Be Cancelled"
            );
        }

        // ALREADY CANCELLED

        if (
                appointment.getStatus()
                        == AppointmentStatus.CANCELLED
        ) {

            throw new RuntimeException(
                    "Appointment Already Cancelled"
            );
        }

        // TIME CHECK

        LocalDateTime appointmentDateTime =
                LocalDateTime.of(
                        appointment.getAppointmentDate(),
                        appointment.getStartTime()
                );

        // LESS THAN 1 HOUR REMAINING

        if (
                appointmentDateTime.isBefore(
                        LocalDateTime.now()
                                .plusHours(1)
                )
        ) {

            throw new RuntimeException(
                    "Cannot Cancel Within 1 Hour"
            );
        }

        appointment.setStatus(
                AppointmentStatus.CANCELLED
        );

        return appointmentRepository
                .save(appointment);
    }
    
}
    