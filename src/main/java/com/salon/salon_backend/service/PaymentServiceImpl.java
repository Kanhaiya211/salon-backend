package com.salon.salon_backend.service;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import com.salon.salon_backend.dto.CreateAppointmentRequest;
import com.salon.salon_backend.dto.VerifyPaymentRequest;
import com.salon.salon_backend.entity.Appointment;
import com.salon.salon_backend.entity.Payment;
import com.salon.salon_backend.entity.User;
import com.salon.salon_backend.enums.AppointmentStatus;
import com.salon.salon_backend.repository.AppointmentRepository;
import com.salon.salon_backend.repository.PaymentRepository;
import com.salon.salon_backend.repository.UserRepository;
import com.salon.salon_backend.service.AppointmentService;
import com.salon.salon_backend.service.PaymentService;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl
        implements PaymentService {

	@Value("${razorpay.key.id}")
	private String razorpayKeyId;
	@Autowired
	private AppointmentRepository
	        appointmentRepository;
    @Autowired
    private PaymentRepository
            paymentRepository;

    @Autowired
    private AppointmentService
            appointmentService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository
            userRepository;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public String verifyPayment(

            VerifyPaymentRequest request,

            String email
    ) {

        try {

            String payload =

                    request.getRazorpayOrderId()

                    +

                    "|"

                    +

                    request.getRazorpayPaymentId();

            String generatedSignature =
                    hmacSHA256(

                            payload,

                            razorpayKeySecret
                    );

            if (

                    !generatedSignature.equals(
                            request.getRazorpaySignature()
                    )

            ) {

                return "Invalid Payment";
            }

            User customer =
                    userRepository
                            .findByEmail(email)
                            .orElseThrow(() ->

                                    new RuntimeException(
                                            "User Not Found"
                                    )
                            );

            Payment payment =
                    new Payment();

            payment.setRazorpayOrderId(
                    request.getRazorpayOrderId()
            );

            payment.setRazorpayPaymentId(
                    request.getRazorpayPaymentId()
            );

            payment.setRazorpaySignature(
                    request.getRazorpaySignature()
            );

            payment.setStatus(
                    "SUCCESS"
            );

            payment.setAmount(0);

            payment =
                    paymentRepository.save(
                            payment
                    );

            CreateAppointmentRequest appointmentRequest =
                    new CreateAppointmentRequest();

            appointmentRequest.setSalonId(
                    request.getSalonId()
            );

            appointmentRequest.setServiceIds(
                    request.getServiceIds()
            );

            appointmentRequest.setAppointmentDate(
                    request.getAppointmentDate()
            );

            appointmentRequest.setStartTime(
                    request.getStartTime()
            );

            appointmentRequest.setNotes(
                    request.getNotes()
            );
            

            Appointment appointment =

                    appointmentService.createAppointment(

                            appointmentRequest,

                            customer
                    );
            emailService.sendBookingConfirmation(

                    customer.getEmail(),

                    customer.getName(),

                    appointment.getSalon().getName(),

                    appointment.getAppointmentDate().toString(),

                    appointment.getStartTime().toString()
            );

            emailService.sendOwnerBookingNotification(

                    appointment
                            .getSalon()
                            .getOwner()
                            .getEmail(),

                    customer.getName(),

                    appointment
                            .getSalon()
                            .getName()
            );

            appointment.setPayment(
                    payment
            );

            payment.setAppointment(
                    appointment
            );

            payment.setAmount(
                    appointment.getTotalPrice().intValue()
            );
            
            paymentRepository.save(
                    payment
            );

            return
                    "Payment Verified & Appointment Created";

        } catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();
        }
    }

    private String hmacSHA256(

            String data,

            String secret

    ) throws Exception {

        Mac sha256Hmac =
                Mac.getInstance(
                        "HmacSHA256"
                );

        SecretKeySpec secretKey =
                new SecretKeySpec(

                        secret.getBytes(
                                StandardCharsets.UTF_8
                        ),

                        "HmacSHA256"
                );

        sha256Hmac.init(secretKey);

        byte[] hash =
                sha256Hmac.doFinal(

                        data.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        StringBuilder hexString =
                new StringBuilder();

        for (byte b : hash) {

            String hex =
                    Integer.toHexString(
                            0xff & b
                    );

            if (hex.length() == 1) {

                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }
    @Override
    @Transactional
    public String refundPayment(

            Long appointmentId
    ) {

        try {

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

            Payment payment =
                    appointment.getPayment();

            RazorpayClient razorpay =
                    new RazorpayClient(
                            razorpayKeyId,
                            razorpayKeySecret
                    );

            JSONObject refundRequest =
                    new JSONObject();

            refundRequest.put(
                    "amount",
                    payment.getAmount() * 100
            );

            Refund refund =
                    razorpay.payments
                            .refund(

                                    payment
                                            .getRazorpayPaymentId(),

                                    refundRequest
                            );

            payment.setStatus(
                    "REFUNDED"
            );

            paymentRepository.save(
                    payment
            );

            appointment.setStatus(
                    AppointmentStatus.CANCELLED
            );

            appointmentRepository.save(
                    appointment
            );

            return "Refund Successful";

        } catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();
        }
    }
}