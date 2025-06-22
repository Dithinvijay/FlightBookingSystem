package com.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.Model.Booking;
import com.userservice.exception.BookingsNotFoundException;
import com.userservice.service.UserService;

@RestController
@RequestMapping("/user")
public class PassengerController {
	@Autowired
	private UserService userService;
	
	@GetMapping("/getBookings/{passengerId}")
	public ResponseEntity<List<Booking>> getBookingsByPassengerId(@PathVariable Integer passengerId){
		List<Booking> bookings = userService.getBookingsByPassengerId(passengerId);
//		if(bookings.size() == 0) {
//		
//		}
		
		return ResponseEntity.status(HttpStatus.OK).body(bookings);
	}
	
	@PostMapping("/book")
    public int bookFlight(@RequestBody Booking booking) {
		return userService.bookFlight(booking.getFlightNumber(),"Economy",booking.getPassengers().size(),booking);
    }

	
	
	
}
