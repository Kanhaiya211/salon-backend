package com.salon.salon_backend.dto;

public class MonthlyRevenueDTO {

    private String month;

    private Double revenue;

    public MonthlyRevenueDTO(
            String month,
            Double revenue
    ) {

        this.month = month;
        this.revenue = revenue;
    }

    public String getMonth() {
        return month;
    }

    public Double getRevenue() {
        return revenue;
    }
}