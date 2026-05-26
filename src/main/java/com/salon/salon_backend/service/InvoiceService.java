package com.salon.salon_backend.service;

import com.salon.salon_backend.entity.Appointment;

public interface InvoiceService {

    byte[] generateInvoice(

            Appointment appointment
    );
}