package com.bookingservice.service;

import java.util.HashMap;
import java.util.List;

import com.bookingservice.model.Booking;
import com.bookingservice.model.PassengerDetails;

public interface BookingService {

	List<Booking> getBookingByPassengerBookingId(Integer passengerBookingId);
	
	List<Booking> getBookingsById(Integer bookingId);
	
	HashMap<String, String > showAvailableSeats(String flightNumber);
	
	int showAvailableSeatsBySeatClass(String flightNumber,String seatclass);
	
	int addBooking(String flightNumber,String seatclass,int noOfSeats,Booking booking);
	
	int deleteBookingByFlightNumber(String flightNumber);
	
	int cancelBookingByBookingId(Integer bookingId);
	
	List<Booking> getBookingByFlightNumber(String flightNumber);
	
	int deletePassengerIdByBookingId(Integer bookingId,Integer passengerId);
	
	double calculateTotalPrice(Integer noOfSeats,String seatclass, String flightNumber);
	
	PassengerDetails getPassengerDetailsById(Integer passengerId);
}
