package com.salon.salon_backend.dto;

public class RefundRequest {

    private Long appointmentId;

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(
            Long appointmentId
    ) {
        this.appointmentId = appointmentId;
    }
}