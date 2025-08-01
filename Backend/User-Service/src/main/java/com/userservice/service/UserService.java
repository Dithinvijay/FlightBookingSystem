package com.userservice.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.userservice.Model.Booking;
import com.userservice.Model.Flight;
import com.userservice.Model.User;

@Service
public interface UserService {
	
	List<Flight> getFlightsByLocationAndDate(String source,String destination,LocalDate date);
	
	List<Booking> getBookingsByPassengerId(Integer passengerId);
	
	User updateUserById(Long id,User user);
	
	int bookFlight(String flightNumber, String seatclass, int noOfSeats, Booking booking);
	
	List<Booking> getBookingsByFlightNumber(String flightNumber);

	int addFlight(Flight flight);
}


