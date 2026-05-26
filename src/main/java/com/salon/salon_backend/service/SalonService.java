package com.salon.salon_backend.service;

import com.salon.salon_backend.entity.Salon;
import com.salon.salon_backend.entity.User;

import java.util.List;

public interface SalonService {

    Salon createSalon(Salon salon, User owner);

    List<Salon> getAllSalons();

    List<Salon> getOwnerSalons(User owner);

    Salon getSalonById(Long id);

    Salon updateSalon(Long id, Salon updatedSalon, User owner);

    void deleteSalon(Long id, User owner);

    List<Salon> searchByCity(String city);

    List<Salon> searchByName(String keyword);
}