package com.salon.salon_backend.controller;
import org.springframework.security.core.Authentication;
import com.salon.salon_backend.service.PaymentService;
import com.salon.salon_backend.entity.User;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import com.salon.salon_backend.dto.CreateOrderRequest;
import com.salon.salon_backend.dto.RefundRequest;

import javax.crypto.Mac;

import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;

import com.salon.salon_backend.dto.VerifyPaymentRequest;
import com.salon.salon_backend.entity.Appointment;
import com.salon.salon_backend.entity.Payment;

import com.salon.salon_backend.repository.PaymentRepository;
import com.salon.salon_backend.repository.UserRepository;
import com.salon.salon_backend.dto.CreateAppointmentRequest;

import com.salon.salon_backend.service.AppointmentService;
@RestController
@RequestMapping("/payments")
@CrossOrigin(origins =
        "http://localhost:5173")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Autowired
    private PaymentRepository
            paymentRepository;

    @Autowired
    private PaymentService
            paymentService;
    @Autowired
    private AppointmentService
            appointmentService;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    
    @Autowired
    private UserRepository
            userRepository;

    @PostMapping("/create-order")
    public String createOrder(

            @RequestBody
            CreateOrderRequest request
    ) throws Exception {

        RazorpayClient razorpay =
                new RazorpayClient(

                        razorpayKeyId,

                        razorpayKeySecret
                );

        JSONObject orderRequest =
                new JSONObject();

        // RAZORPAY USES PAISE

        orderRequest.put(
                "amount",
                request.getAmount() * 100
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                "txn_123456"
        );

        Order order =
                razorpay.orders
                        .create(orderRequest);

        return order.toString();
    }
    @PostMapping("/verify")

    public String verifyPayment(

            @RequestBody
            VerifyPaymentRequest request,

            Authentication authentication
    ) {

        return paymentService.verifyPayment(

                request,

                authentication.getName()
        );
    }
    @PostMapping("/refund")

    public String refundPayment(

            @RequestBody
            RefundRequest request
    ) {

        return paymentService.refundPayment(

                request.getAppointmentId()
        );
    }
}