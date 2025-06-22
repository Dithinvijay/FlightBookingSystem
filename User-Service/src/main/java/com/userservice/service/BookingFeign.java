package com.userservice.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.userservice.FeignConfig;
import com.userservice.Model.Booking;
import com.userservice.exception.BookingFeignFalback;
import com.userservice.exception.CustomErrorDecoder;

import jakarta.validation.Valid;

@FeignClient(name = "Booking-Service", configuration = CustomErrorDecoder.class, fallback = BookingFeignFalback.class)
public interface BookingFeign {
	
	@GetMapping("/BMS/bookingsOfPassenger/{passengerBookingId}")
	public  List<Booking> getBookingsByPassengerId(@PathVariable Integer passengerBookingId);
	
	@PostMapping("BMS/bookTickets/{flightNumber}/{seatclass}/{noOfSeats}")
	public String addBooking(@PathVariable String flightNumber,
			@PathVariable String seatclass,
			@PathVariable int noOfSeats
			,@RequestBody  Booking booking);
	
	@GetMapping("/BMS/bookings/{flightNumber}")
	public List<Booking> getBookingsByFlightNumber(@PathVariable String flightNumber);
	

}
