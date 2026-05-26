package com.salon.salon_backend.service;

import com.salon.salon_backend.entity.Salon;
import com.salon.salon_backend.entity.User;
import com.salon.salon_backend.repository.SalonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalonServiceImpl implements SalonService {

    @Autowired
    private SalonRepository salonRepository;

    @Override
    public Salon createSalon(Salon salon, User owner) {

        salon.setOwner(owner);
        if (
                salon.getWorkingDays() == null
        ) {

            salon.setWorkingDays(
                    "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY"
            );
        }
        return salonRepository.save(salon);
    }

    @Override
    public List<Salon> getAllSalons() {

        return salonRepository.findAll();
    }

    @Override
    public List<Salon> getOwnerSalons(User owner) {

        return salonRepository.findByOwner(owner);
    }

    @Override
    public Salon getSalonById(Long id) {

        return salonRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Salon Not Found"));
    }

    @Override
    public Salon updateSalon(
            Long id,
            Salon updatedSalon,
            User owner
    ) {

        Salon salon = getSalonById(id);

        // OWNERSHIP CHECK
        if (!salon.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        salon.setName(updatedSalon.getName());
        salon.setAddress(updatedSalon.getAddress());
        salon.setCity(updatedSalon.getCity());
        salon.setPhone(updatedSalon.getPhone());
        salon.setDescription(updatedSalon.getDescription());
        salon.setImage(updatedSalon.getImage());

        return salonRepository.save(salon);
    }

    @Override
    public void deleteSalon(Long id, User owner) {

        Salon salon = getSalonById(id);

        // OWNERSHIP CHECK
        if (!salon.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        salonRepository.delete(salon);
    }

	@Override
	public List<Salon> searchByCity(String city) {
		return salonRepository
	            .findByCityContainingIgnoreCase(city);
	}

	@Override
	public List<Salon> searchByName(String keyword) {
		return salonRepository
	            .findByNameContainingIgnoreCase(keyword);
	}
   

    
}