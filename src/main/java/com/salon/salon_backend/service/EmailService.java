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

    // ===========================
    // COMMON EMAIL METHOD
    // ===========================

    private void sendEmail(
            String toEmail,
            String subject,
            String htmlContent
    ) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> body = new HashMap<>();

        body.put("sender", Map.of(
                "name", senderName,
                "email", senderEmail
        ));

        body.put("to", List.of(
                Map.of("email", toEmail)
        ));

        body.put("subject", subject);
        body.put("htmlContent", htmlContent);

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
            throw new RuntimeException(
                    "Brevo Error : " + response.getBody()
            );
        }
        // ===========================
// OTP EMAIL
// ===========================

public void sendOtpEmail(
        String toEmail,
        String otp
) {

    String html = """
        <html>

        <body style="
            background:#f4f4f4;
            padding:40px;
            font-family:Arial,sans-serif;
        ">

            <div style="
                max-width:600px;
                margin:auto;
                background:white;
                padding:40px;
                border-radius:20px;
                box-shadow:0 2px 10px rgba(0,0,0,.1);
            ">

                <h1 style="
                    color:#7c3aed;
                    text-align:center;
                ">
                    Salon App
                </h1>

                <h2 style="
                    text-align:center;
                ">
                    OTP Verification
                </h2>

                <p>Hello,</p>

                <p>
                    Use the following OTP to verify your account.
                </p>

                <div style="
                    margin:30px auto;
                    text-align:center;
                ">

                    <span style="
                        display:inline-block;
                        padding:18px 35px;
                        font-size:34px;
                        font-weight:bold;
                        letter-spacing:8px;
                        background:#7c3aed;
                        color:white;
                        border-radius:10px;
                    ">
                        %s
                    </span>

                </div>

                <p>
                    This OTP is valid for
                    <b>5 minutes</b>.
                </p>

                <hr>

                <p style="
                    color:gray;
                    font-size:13px;
                ">
                    If you didn't request this verification,
                    you can safely ignore this email.
                </p>

            </div>

        </body>

        </html>
        """.formatted(otp);

    sendEmail(
            toEmail,
            "Salon App OTP Verification",
            html
    );
}
        // ===========================
// CUSTOMER BOOKING EMAIL
// ===========================

public void sendBookingConfirmation(

        String toEmail,

        String customerName,

        String salonName,

        String date,

        String time
) {

    String html = """

    <html>

    <body style="
        font-family:Arial;
        background:#f4f4f4;
        padding:40px;
    ">

        <div style="
            max-width:600px;
            margin:auto;
            background:white;
            padding:40px;
            border-radius:20px;
            box-shadow:0 2px 10px rgba(0,0,0,.08);
        ">

            <h1 style="
                color:#7c3aed;
                text-align:center;
            ">

                Appointment Confirmed

            </h1>

            <p>
                Hello <b>%s</b>,
            </p>

            <p>
                Your appointment has been successfully booked.
            </p>

            <hr/>

            <h2>
                Appointment Details
            </h2>

            <table style="
                width:100%%;
                border-collapse:collapse;
            ">

                <tr>
                    <td><b>Salon</b></td>
                    <td>%s</td>
                </tr>

                <tr>
                    <td><b>Date</b></td>
                    <td>%s</td>
                </tr>

                <tr>
                    <td><b>Time</b></td>
                    <td>%s</td>
                </tr>

            </table>

            <br>

            <div style="
                background:#ecfdf5;
                padding:15px;
                border-left:5px solid #16a34a;
                border-radius:8px;
            ">
                Please arrive 10 minutes before your appointment.
            </div>

            <br>

            <hr/>

            <p style="
                color:gray;
                text-align:center;
            ">
                Thank you for booking with Salon App ❤️
            </p>

        </div>

    </body>

    </html>

    """.formatted(

            customerName,

            salonName,

            date,

            time
    );

    sendEmail(
            toEmail,
            "Appointment Confirmed",
            html
    );
}
        // ===========================
// OWNER BOOKING NOTIFICATION
// ===========================

public void sendOwnerBookingNotification(

        String ownerEmail,

        String customerName,

        String salonName
) {

    String html = """

    <html>

    <body style="
        font-family:Arial;
        background:#f4f4f4;
        padding:40px;
    ">

        <div style="
            max-width:600px;
            margin:auto;
            background:white;
            padding:40px;
            border-radius:20px;
            box-shadow:0 2px 10px rgba(0,0,0,.08);
        ">

            <div style="
                text-align:center;
            ">

                <h1 style="
                    color:#16a34a;
                    margin-bottom:10px;
                ">
                    🎉 New Appointment Booking
                </h1>

                <p style="
                    color:#666;
                ">
                    Congratulations! A customer has booked an appointment.
                </p>

            </div>

            <hr/>

            <table style="
                width:100%%;
                border-collapse:collapse;
                margin-top:20px;
            ">

                <tr>
                    <td style="padding:10px 0;">
                        <b>Customer</b>
                    </td>

                    <td style="padding:10px 0;">
                        %s
                    </td>
                </tr>

                <tr>
                    <td style="padding:10px 0;">
                        <b>Salon</b>
                    </td>

                    <td style="padding:10px 0;">
                        %s
                    </td>
                </tr>

            </table>

            <br>

            <div style="
                background:#ecfdf5;
                border-left:5px solid #16a34a;
                padding:15px;
                border-radius:8px;
            ">

                Please log in to your dashboard to view complete booking details.

            </div>

            <br>

            <hr/>

            <p style="
                text-align:center;
                color:gray;
                font-size:13px;
            ">

                Salon App Owner Notification

            </p>

        </div>

    </body>

    </html>

    """.formatted(

            customerName,

            salonName
    );

    sendEmail(
            ownerEmail,
            "New Booking Received",
            html
    );
}
        // ===========================
// CUSTOMER CANCELLATION EMAIL
// ===========================

public void sendCancellationEmail(

        String toEmail,

        String customerName,

        String salonName
) {

    String html = """

    <html>

    <body style="
        font-family:Arial,sans-serif;
        background:#f4f4f4;
        padding:40px;
    ">

        <div style="
            max-width:600px;
            margin:auto;
            background:white;
            padding:40px;
            border-radius:20px;
            box-shadow:0 2px 10px rgba(0,0,0,.08);
        ">

            <div style="
                text-align:center;
            ">

                <h1 style="
                    color:#dc2626;
                ">
                    Appointment Cancelled
                </h1>

            </div>

            <p>
                Hello <b>%s</b>,
            </p>

            <p>
                Your appointment has been cancelled successfully.
            </p>

            <br>

            <table style="
                width:100%%;
                border-collapse:collapse;
            ">

                <tr>

                    <td style="padding:10px 0;">
                        <b>Salon</b>
                    </td>

                    <td style="padding:10px 0;">
                        %s
                    </td>

                </tr>

                <tr>

                    <td style="padding:10px 0;">
                        <b>Status</b>
                    </td>

                    <td style="
                        padding:10px 0;
                        color:#dc2626;
                        font-weight:bold;
                    ">
                        Cancelled
                    </td>

                </tr>

            </table>

            <br>

            <div style="
                background:#fef2f2;
                border-left:5px solid #dc2626;
                padding:15px;
                border-radius:8px;
            ">

                If your booking was prepaid, your refund will be processed according to the salon's refund policy.

            </div>

            <br>

            <hr/>

            <p style="
                color:gray;
                text-align:center;
                font-size:13px;
            ">

                Thank you for using Salon App.

            </p>

        </div>

    </body>

    </html>

    """.formatted(

            customerName,

            salonName
    );

    sendEmail(
            toEmail,
            "Appointment Cancelled",
            html
    );
}
        // ===========================
// OWNER CANCELLATION EMAIL
// ===========================

public void sendOwnerCancellationEmail(

        String ownerEmail,

        String customerName,

        String salonName
) {

    String html = """

    <html>

    <body style="
        font-family:Arial,sans-serif;
        background:#f4f4f4;
        padding:40px;
    ">

        <div style="
            max-width:600px;
            margin:auto;
            background:white;
            padding:40px;
            border-radius:20px;
            box-shadow:0 2px 10px rgba(0,0,0,.08);
        ">

            <div style="
                text-align:center;
            ">

                <h1 style="
                    color:#dc2626;
                ">
                    Booking Cancelled
                </h1>

                <p style="
                    color:#666;
                ">
                    A customer has cancelled an appointment.
                </p>

            </div>

            <hr/>

            <table style="
                width:100%%;
                border-collapse:collapse;
                margin-top:20px;
            ">

                <tr>

                    <td style="padding:10px 0;">
                        <b>Customer</b>
                    </td>

                    <td style="padding:10px 0;">
                        %s
                    </td>

                </tr>

                <tr>

                    <td style="padding:10px 0;">
                        <b>Salon</b>
                    </td>

                    <td style="padding:10px 0;">
                        %s
                    </td>

                </tr>

                <tr>

                    <td style="padding:10px 0;">
                        <b>Status</b>
                    </td>

                    <td style="
                        padding:10px 0;
                        color:#dc2626;
                        font-weight:bold;
                    ">
                        Cancelled
                    </td>

                </tr>

            </table>

            <br>

            <div style="
                background:#fef2f2;
                border-left:5px solid #dc2626;
                padding:15px;
                border-radius:8px;
            ">

                The appointment slot is now available for new bookings.

            </div>

            <br>

            <hr/>

            <p style="
                text-align:center;
                color:gray;
                font-size:13px;
            ">

                Salon App Owner Notification

            </p>

        </div>

    </body>

    </html>

    """.formatted(

            customerName,

            salonName
    );

    sendEmail(
            ownerEmail,
            "Appointment Cancelled",
            html
    );
}
        // ===========================
// APPOINTMENT REMINDER EMAIL
// ===========================

public void sendReminderEmail(

        String toEmail,

        String customerName,

        String salonName,

        String date,

        String time
) {

    String html = """

    <html>

    <body style="
        font-family:Arial,sans-serif;
        background:#f4f4f4;
        padding:40px;
    ">

        <div style="
            max-width:600px;
            margin:auto;
            background:white;
            padding:40px;
            border-radius:20px;
            box-shadow:0 2px 10px rgba(0,0,0,.08);
        ">

            <div style="
                text-align:center;
            ">

                <h1 style="
                    color:#7c3aed;
                ">
                    Appointment Reminder
                </h1>

                <p style="
                    color:#666;
                ">
                    Don't forget! Your appointment is scheduled for tomorrow.
                </p>

            </div>

            <hr/>

            <p>
                Hello <b>%s</b>,
            </p>

            <p>
                This is a friendly reminder about your upcoming appointment.
            </p>

            <br>

            <table style="
                width:100%%;
                border-collapse:collapse;
            ">

                <tr>
                    <td style="padding:10px 0;">
                        <b>Salon</b>
                    </td>

                    <td style="padding:10px 0;">
                        %s
                    </td>
                </tr>

                <tr>
                    <td style="padding:10px 0;">
                        <b>Date</b>
                    </td>

                    <td style="padding:10px 0;">
                        %s
                    </td>
                </tr>

                <tr>
                    <td style="padding:10px 0;">
                        <b>Time</b>
                    </td>

                    <td style="padding:10px 0;">
                        %s
                    </td>
                </tr>

            </table>

            <br>

            <div style="
                background:#f3e8ff;
                border-left:5px solid #7c3aed;
                padding:15px;
                border-radius:8px;
            ">

                Please arrive at least
                <b>10 minutes before</b>
                your scheduled appointment.

            </div>

            <br>

            <hr/>

            <p style="
                color:gray;
                text-align:center;
                font-size:13px;
            ">

                Thank you for choosing Salon App ❤️

            </p>

        </div>

    </body>

    </html>

    """.formatted(

            customerName,

            salonName,

            date,

            time
    );

    sendEmail(
            toEmail,
            "Appointment Reminder",
            html
    );
}
    }
