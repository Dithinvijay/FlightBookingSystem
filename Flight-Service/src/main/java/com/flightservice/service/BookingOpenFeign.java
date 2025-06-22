package com.flightservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Booking-Service")
public interface BookingOpenFeign {

	@DeleteMapping("/BMS/deleteBooking/{flightNumber}")
	String deleteBooking(@PathVariable String flightNumber);
	
}
