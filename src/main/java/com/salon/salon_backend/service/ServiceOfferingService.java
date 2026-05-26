package com.salon.salon_backend.service;

import com.salon.salon_backend.entity.ServiceOffering;
import com.salon.salon_backend.entity.User;

import java.util.List;

public interface ServiceOfferingService {

    ServiceOffering createService(
            Long salonId,
            ServiceOffering service,
            User owner
    );

    List<ServiceOffering> getSalonServices(Long salonId);

    ServiceOffering updateService(
            Long serviceId,
            ServiceOffering updatedService,
            User owner
    );

    void deleteService(
            Long serviceId,
            User owner
    );
}
