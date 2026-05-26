package com.salon.salon_backend.service;

import com.salon.salon_backend.entity.Salon;
import com.salon.salon_backend.entity.ServiceOffering;
import com.salon.salon_backend.entity.User;

import com.salon.salon_backend.repository.SalonRepository;
import com.salon.salon_backend.repository.ServiceOfferingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceOfferingServiceImpl
        implements ServiceOfferingService {

    @Autowired
    private ServiceOfferingRepository serviceRepository;

    @Autowired
    private SalonRepository salonRepository;

    @Override
    public ServiceOffering createService(
            Long salonId,
            ServiceOffering service,
            User owner
    ) {

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() ->
                        new RuntimeException("Salon Not Found"));

        // OWNERSHIP VERIFICATION
        if (!salon.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException(
                    "Unauthorized To Add Services"
            );
        }

        service.setSalon(salon);

        return serviceRepository.save(service);
    }

    @Override
    public List<ServiceOffering> getSalonServices(Long salonId) {

        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() ->
                        new RuntimeException("Salon Not Found"));

        return serviceRepository.findBySalon(salon);
    }

    @Override
    public ServiceOffering updateService(
            Long serviceId,
            ServiceOffering updatedService,
            User owner
    ) {

        ServiceOffering service = serviceRepository.findById(serviceId)
                .orElseThrow(() ->
                        new RuntimeException("Service Not Found"));

        // OWNERSHIP VERIFICATION
        if (!service.getSalon()
                .getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new RuntimeException("Unauthorized");
        }

        service.setName(updatedService.getName());
        service.setCategory(updatedService.getCategory());
        service.setPrice(updatedService.getPrice());
        service.setDuration(updatedService.getDuration());
        service.setDescription(updatedService.getDescription());
        service.setImage(updatedService.getImage());
        service.setAvailable(updatedService.getAvailable());

        return serviceRepository.save(service);
    }

    @Override
    public void deleteService(
            Long serviceId,
            User owner
    ) {

        ServiceOffering service = serviceRepository.findById(serviceId)
                .orElseThrow(() ->
                        new RuntimeException("Service Not Found"));

        // OWNERSHIP VERIFICATION
        if (!service.getSalon()
                .getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new RuntimeException("Unauthorized");
        }

        serviceRepository.delete(service);
    }
}
