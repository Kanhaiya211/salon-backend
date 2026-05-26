package com.salon.salon_backend.scheduler;

import java.time.LocalDate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import com.salon.salon_backend.entity.Appointment;

import com.salon.salon_backend.repository.AppointmentRepository;

import com.salon.salon_backend.service.EmailService;

@Component
public class ReminderScheduler {

    @Autowired
    private AppointmentRepository
            appointmentRepository;

    @Autowired
    private EmailService
            emailService;

    @Scheduled(
            cron = "0 0 9 * * *"
    )
    public void sendAppointmentReminders() {

        LocalDate tomorrow =
                LocalDate.now()
                        .plusDays(1);

        List<Appointment> appointments =

                appointmentRepository
                        .findByAppointmentDate(
                                tomorrow
                        );

        for (

                Appointment appointment

                :

                appointments

        ) {

            // SKIP CANCELLED

            if (

                    appointment
                            .getStatus()
                            .name()
                            .equals("CANCELLED")

            ) {

                continue;
            }

            // SKIP IF BOOKED TODAY

            if (

                    appointment
                            .getCreatedAt()
                            .toLocalDate()
                            .equals(
                                    LocalDate.now()
                            )

            ) {

                continue;
            }

            emailService.sendReminderEmail(

                    appointment
                            .getCustomer()
                            .getEmail(),

                    appointment
                            .getCustomer()
                            .getName(),

                    appointment
                            .getSalon()
                            .getName(),

                    appointment
                            .getAppointmentDate()
                            .toString(),

                    appointment
                            .getStartTime()
                            .toString()
            );
        }

        System.out.println(
                "Reminder Scheduler Executed"
        );
    }
}