package com.salon.salon_backend.service;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // =========================
    // OTP EMAIL
    // =========================

    public void sendOtpEmail(

            String toEmail,

            String otp
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "Salon App OTP Verification"
        );

        message.setText(

                "Your OTP is: "
                        + otp
                        +
                        "\n\nValid for 5 minutes."
        );

        mailSender.send(message);
    }

    // =========================
    // CUSTOMER BOOKING EMAIL
    // =========================

    public void sendBookingConfirmation(

            String toEmail,

            String customerName,

            String salonName,

            String date,

            String time
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setTo(toEmail);

            helper.setSubject(
                    "Appointment Confirmed"
            );

            String html = """

            <html>

            <body style="
                font-family: Arial;
                background:#f4f4f4;
                padding:40px;
            ">

                <div style="
                    max-width:600px;
                    margin:auto;
                    background:white;
                    padding:40px;
                    border-radius:20px;
                ">

                    <h1 style="
                        color:#7c3aed;
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

                    <p>
                        <b>Salon:</b> %s
                    </p>

                    <p>
                        <b>Date:</b> %s
                    </p>

                    <p>
                        <b>Time:</b> %s
                    </p>

                    <hr/>

                    <p style="
                        color:gray;
                        margin-top:30px;
                    ">

                        Thank you for booking with SalonBook.

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

            helper.setText(
                    html,
                    true
            );

            mailSender.send(message);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================
    // OWNER BOOKING EMAIL
    // =========================

    public void sendOwnerBookingNotification(

            String ownerEmail,

            String customerName,

            String salonName
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setTo(ownerEmail);

            helper.setSubject(
                    "New Booking Received"
            );

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
                ">

                    <h1 style="
                        color:#16a34a;
                    ">

                        New Appointment Booking

                    </h1>

                    <p>
                        You received a new booking.
                    </p>

                    <p>
                        <b>Customer:</b> %s
                    </p>

                    <p>
                        <b>Salon:</b> %s
                    </p>

                </div>

            </body>

            </html>

            """.formatted(

                    customerName,

                    salonName
            );

            helper.setText(
                    html,
                    true
            );

            mailSender.send(message);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================
    // CUSTOMER CANCELLATION EMAIL
    // =========================

    public void sendCancellationEmail(

            String toEmail,

            String customerName,

            String salonName
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setTo(toEmail);

            helper.setSubject(
                    "Appointment Cancelled"
            );

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
                ">

                    <h1 style="
                        color:#dc2626;
                    ">

                        Appointment Cancelled

                    </h1>

                    <p>
                        Hello <b>%s</b>,
                    </p>

                    <p>
                        Your appointment has been cancelled successfully.
                    </p>

                    <p>
                        <b>Salon:</b> %s
                    </p>

                    <p style="
                        color:gray;
                        margin-top:30px;
                    ">

                        Refund will be processed if applicable.

                    </p>

                </div>

            </body>

            </html>

            """.formatted(

                    customerName,

                    salonName
            );

            helper.setText(
                    html,
                    true
            );

            mailSender.send(message);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================
    // OWNER CANCELLATION EMAIL
    // =========================

    public void sendOwnerCancellationEmail(

            String ownerEmail,

            String customerName,

            String salonName
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setTo(ownerEmail);

            helper.setSubject(
                    "Appointment Cancelled"
            );

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
                ">

                    <h1 style="
                        color:#dc2626;
                    ">

                        Booking Cancelled

                    </h1>

                    <p>
                        A customer cancelled appointment.
                    </p>

                    <p>
                        <b>Customer:</b> %s
                    </p>

                    <p>
                        <b>Salon:</b> %s
                    </p>

                </div>

            </body>

            </html>

            """.formatted(

                    customerName,

                    salonName
            );

            helper.setText(
                    html,
                    true
            );

            mailSender.send(message);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public void sendReminderEmail(

            String toEmail,

            String customerName,

            String salonName,

            String date,

            String time
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setTo(toEmail);

            helper.setSubject(
                    "Appointment Reminder"
            );

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
                ">

                    <h1 style="
                        color:#7c3aed;
                    ">

                        Appointment Reminder

                    </h1>

                    <p>
                        Hello <b>%s</b>,
                    </p>

                    <p>
                        This is a reminder for your appointment tomorrow.
                    </p>

                    <p>
                        <b>Salon:</b> %s
                    </p>

                    <p>
                        <b>Date:</b> %s
                    </p>

                    <p>
                        <b>Time:</b> %s
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

            helper.setText(
                    html,
                    true
            );

            mailSender.send(message);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}