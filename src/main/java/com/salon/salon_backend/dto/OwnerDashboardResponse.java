package com.salon.salon_backend.dto;

public class OwnerDashboardResponse {

    private Long totalAppointments;

    private Double totalRevenue;

    private Long pendingAppointments;

    private Long completedAppointments;

    private Long todayAppointments;

    public OwnerDashboardResponse(
            Long totalAppointments,
            Double totalRevenue,
            Long pendingAppointments,
            Long completedAppointments,
            Long todayAppointments
    ) {

        this.totalAppointments = totalAppointments;
        this.totalRevenue = totalRevenue;
        this.pendingAppointments = pendingAppointments;
        this.completedAppointments = completedAppointments;
        this.todayAppointments = todayAppointments;
    }

    public Long getTotalAppointments() {
        return totalAppointments;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public Long getPendingAppointments() {
        return pendingAppointments;
    }

    public Long getCompletedAppointments() {
        return completedAppointments;
    }

    public Long getTodayAppointments() {
        return todayAppointments;
    }
}
