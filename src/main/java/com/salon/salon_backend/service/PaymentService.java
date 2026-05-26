package com.salon.salon_backend.service;

import com.salon.salon_backend.dto.VerifyPaymentRequest;

public interface PaymentService {

    String verifyPayment(

            VerifyPaymentRequest request,

            String email
    );
    String refundPayment(
            Long appointmentId
    );
}