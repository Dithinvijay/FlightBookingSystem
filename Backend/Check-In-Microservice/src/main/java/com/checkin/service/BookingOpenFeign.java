package com.checkin.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.checkin.model.Booking;
import com.checkin.model.PassengerDetails;

@FeignClient(name = "Booking-Service")
public interface BookingOpenFeign {
	
	@GetMapping("/BMS/bookings/{flightNumber}")
	public List<Booking> getBookings(@PathVariable String flightNumber);

	@GetMapping("/BMS/getByBookingId/{bookingId}")
	public List<Booking> getBookingsById(@PathVariable Integer bookingId);
	
	@GetMapping("/BMS/getPassengerById/{passengerId}")
	PassengerDetails getPassengerDetailsById(@PathVariable Integer passengerId);
	
}
