package com.salon.salon_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtpEmail(String toEmail, String otp) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> body = new HashMap<>();

        Map<String, String> sender = new HashMap<>();
        sender.put("email", senderEmail);
        sender.put("name", senderName);

        body.put("sender", sender);

        body.put("to", List.of(
                Map.of("email", toEmail)
        ));

        body.put("subject", "SalonBook OTP Verification");

        body.put("htmlContent",
                """
                <html>
                <body style="font-family:Arial">
                    <h2>OTP Verification</h2>

                    <p>Your OTP is:</p>

                    <h1 style="color:#7c3aed">%s</h1>

                    <p>This OTP is valid for 5 minutes.</p>
                </body>
                </html>
                """.formatted(otp));

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class
                );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send email: " + response.getBody());
        }
    }
}
