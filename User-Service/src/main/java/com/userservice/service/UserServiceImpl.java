package com.userservice.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userservice.Model.Booking;
import com.userservice.Model.Flight;

@Service
public class UserServiceImpl implements UserService {
	
	private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);


	@Autowired
	FlightOpenFeign flightOpenFeign;
	
	@Autowired
	BookingFeign bookingFeign;
	
	
	@Override
	public List<Flight> getFlightsByLocationAndDate(String source, String destination, LocalDate date) {
		LOGGER.info("Fetching Flights from {} to {} on {}", source, destination, date);
		List<Flight> list = flightOpenFeign.getFlightsOnLocationAndDate(source, destination, date);
		LOGGER.info("Found {} flights from {} to {} on {}", list.size(), source, destination, date);
		return list;
	}
	
	@Override
	public List<Booking> getBookingsByPassengerId(Integer passengerId) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching Bookings By Passenger Id {} ", passengerId);
		List<Booking> booking = bookingFeign.getBookingsByPassengerId(passengerId);
		LOGGER.info(" Found {} bookings by passenger id {}", booking.size(), passengerId);
		return booking;
	}
	
	
	@Override
	public int bookFlight(String flightNumber,String seatclass,int noOfSeats,Booking booking) {
		// TODO Auto-generated method stub
		LOGGER.info("Trying to Book Tickets...");
		bookingFeign.addBooking(flightNumber, seatclass, noOfSeats, booking);
		LOGGER.info("Booked Successfully");
	return 0;
	}

	@Override
	public List<Booking> getBookingsByFlightNumber(String flightNumber) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching Bookings By flight Number {} ", flightNumber);
	List<Booking> list = bookingFeign.getBookingsByFlightNumber(flightNumber);
	if(list.size() != 0) {
		LOGGER.info("{} bookings found for flight number {}", list.size(), flightNumber);
	}
	
		return list;
	}

	@Override
	public int addFlight(Flight flight) {
		// TODO Auto-generated method stub
	String s = 	flightOpenFeign.addFlight(flight);
	if(s.equals("Added Flight Details Successfully")) {
		return 1;
	}
	return 0;

}
	
}
