package com.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.Model.Booking;
import com.userservice.Model.Flight;
import com.userservice.exception.BookingsNotFoundException;
import com.userservice.exception.FlightHandlingException;
import com.userservice.service.FlightOpenFeign;
import com.userservice.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	FlightOpenFeign flightOpenFeign;
	
	@Autowired
	UserService userService;
	@PostMapping("/addFlight")
	public ResponseEntity<String> addFlight(@RequestBody @Valid Flight flight) throws FlightHandlingException{
		int x = userService.addFlight(flight);
		if(x == 0) {
			return ResponseEntity.ok("Admin added flight successfully");
		}
		throw new FlightHandlingException("Flight not addedd by admin");
	}

	
	@GetMapping("/getBookingsByFlightNumber/{flightNumber}")
	public ResponseEntity<List<Booking>> getBookingsByFlightNumber(@PathVariable String flightNumber) throws BookingsNotFoundException{
		List<Booking> list = userService.getBookingsByFlightNumber(flightNumber);
		if(list.size() == 0) {
			throw new BookingsNotFoundException("No Bookings Found");
		}
		return ResponseEntity.ok(list);
	}
}
