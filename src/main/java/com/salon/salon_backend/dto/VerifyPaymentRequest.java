package com.salon.salon_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;

@Data
public class VerifyPaymentRequest {

    // PAYMENT DATA

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    // APPOINTMENT DATA

    private Long salonId;

    private List<Long> serviceIds;

    private LocalDate appointmentDate;

    private LocalTime  startTime;

    private String notes;
}