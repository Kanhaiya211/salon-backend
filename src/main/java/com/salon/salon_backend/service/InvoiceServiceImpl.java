package com.salon.salon_backend.service;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.salon.salon_backend.service.InvoiceService;
import com.salon.salon_backend.entity.Appointment;
import com.salon.salon_backend.entity.ServiceOffering;



@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Override
    public byte[] generateInvoice( Appointment appointment ) {

        try {

            Document document =
                    new Document();

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            PdfWriter.getInstance(

                    document,

                    out
            );

            document.open();

            document.add(

                    new Paragraph(
                            "SALON INVOICE"
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            document.add(

                    new Paragraph(

                            "Customer: "

                                    +

                                    appointment
                                            .getCustomer()
                                            .getName()
                    )
            );

            document.add(

                    new Paragraph(

                            "Salon: "

                                    +

                                    appointment
                                            .getSalon()
                                            .getName()
                    )
            );

            document.add(

                    new Paragraph(

                            "Date: "

                                    +

                                    appointment
                                            .getAppointmentDate()
                    )
            );

            document.add(

                    new Paragraph(

                            "Time: "

                                    +

                                    appointment
                                            .getStartTime()
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            document.add(
                    new Paragraph("Services:")
            );

            for (

                    ServiceOffering service

                    :

                    appointment.getServices()

            ) {

                document.add(

                        new Paragraph(

                                "- "

                                        +

                                        service.getName()

                                        +

                                        " - ₹"

                                        +

                                        service.getPrice()
                        )
                );
            }

            document.add(
                    new Paragraph(" ")
            );

            document.add(

                    new Paragraph(

                            "Total Amount: ₹"

                                    +

                                    appointment
                                            .getTotalPrice()
                    )
            );

            document.add(

                    new Paragraph(

                            "Payment ID: "

                                    +

                                    appointment
                                            .getPayment()
                                            .getRazorpayPaymentId()
                    )
            );

            document.close();

            return out.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    e.getMessage()
            );
        }
    }
}