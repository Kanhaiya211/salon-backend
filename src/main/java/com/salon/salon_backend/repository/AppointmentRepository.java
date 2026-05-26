package com.salon.salon_backend.repository;

import com.salon.salon_backend.dto.MonthlyRevenueDTO;
import com.salon.salon_backend.entity.Appointment;
import com.salon.salon_backend.entity.Salon;
import com.salon.salon_backend.entity.User;
import com.salon.salon_backend.enums.AppointmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    // CUSTOMER BOOKINGS
    List<Appointment> findByCustomer(User customer);

    // SALON BOOKINGS
    List<Appointment> findBySalon(Salon salon);

    // BOOKINGS OF SALON ON SPECIFIC DATE
    List<Appointment> findBySalonAndAppointmentDate(
            Salon salon,
            LocalDate appointmentDate
    );

    List<Appointment> findBySalonOwner(User owner);
    
    // CONFLICT DETECTION QUERY
    List<Appointment> findBySalonAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Salon salon,
            LocalDate appointmentDate,
            LocalTime endTime,
            LocalTime startTime
    );
    
    List<Appointment> findBySalonIdAndAppointmentDate(
            Long salonId,
            LocalDate appointmentDate
    );
    
    Long countBySalonOwnerId(Long ownerId);

    Long countBySalonOwnerIdAndStatus(
            Long ownerId,
            AppointmentStatus status
    );

    Long countBySalonOwnerIdAndAppointmentDate(
            Long ownerId,
            LocalDate appointmentDate
    );
    List<Appointment>
    findByAppointmentDate(
            LocalDate appointmentDate
    );
    
    @Query("""
    		SELECT COALESCE(SUM(a.totalPrice),0)
    		FROM Appointment a
    		WHERE a.salon.owner.id = :ownerId
    		AND a.status = 'COMPLETED'
    		""")
    		Double getTotalRevenue(Long ownerId);
//    @Query("""
//    	    SELECT new com.salon.salon_backend.dto.MonthlyRevenueDTO(
//    	        FUNCTION('DATE_FORMAT', a.appointmentDate, '%b'),
//    	        SUM(a.totalPrice)
//    	    )
//    	    FROM Appointment a
//    	    WHERE a.salon.owner.id = :ownerId
//    	    AND a.status = com.salon.salon_backend.enums.AppointmentStatus.COMPLETED
//    	    GROUP BY FUNCTION('DATE_FORMAT', a.appointmentDate, '%Y-%m')
//    	    ORDER BY FUNCTION('DATE_FORMAT', a.appointmentDate, '%Y-%m')
//    	""")
//    	List<MonthlyRevenueDTO> getMonthlyRevenue(
//    	        @Param("ownerId") Long ownerId
//    	);
    
    @Query("""
    	    SELECT a
    	    FROM Appointment a
    	    WHERE a.salon.owner.id = :ownerId
    	    AND a.status = com.salon.salon_backend.enums.AppointmentStatus.COMPLETED
    	""")
    	List<Appointment> getCompletedAppointments(
    	        @Param("ownerId") Long ownerId
    	);
}	