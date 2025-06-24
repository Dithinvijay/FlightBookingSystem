package com.bookingservice.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookingservice.model.Booking;

@Repository
public interface BookingRepo  extends JpaRepository<Booking, Integer>{

	List<Booking> findByPassengerBookingId(Integer passengerBookingId);
	
	List<Booking> findByFlightNumber(String flightNumber);
	
	 void deleteByFlightNumber(String flightNumber);
	 
	 void deleteByBookingId(Integer bookingId);
	 
	List<Booking> findByBookingId(Integer bookingId);
	
	
}
