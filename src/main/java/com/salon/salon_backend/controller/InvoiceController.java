package com.salon.salon_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.salon.salon_backend.entity.Appointment;

import com.salon.salon_backend.repository.AppointmentRepository;

import com.salon.salon_backend.service.InvoiceService;

@RestController

@RequestMapping("/invoice")

@CrossOrigin(
        origins =
                "http://localhost:5173"
)
public class InvoiceController {

    @Autowired
    private InvoiceService
            invoiceService;

    @Autowired
    private AppointmentRepository
            appointmentRepository;

    @GetMapping("/{appointmentId}")

    public ResponseEntity<byte[]>
    downloadInvoice(

            @PathVariable
            Long appointmentId
    ) {

        Appointment appointment =

                appointmentRepository
                        .findById(
                                appointmentId
                        )
                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Appointment Not Found"
                                )
                        );

        byte[] pdf =

                invoiceService.generateInvoice(
                        appointment
                );

        return ResponseEntity.ok()

                .header(

                        HttpHeaders.CONTENT_DISPOSITION,

                        "attachment; filename=invoice.pdf"
                )

                .contentType(
                        MediaType.APPLICATION_PDF
                )

                .body(pdf);
    }
}