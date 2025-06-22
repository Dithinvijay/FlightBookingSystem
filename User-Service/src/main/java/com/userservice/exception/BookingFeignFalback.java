package com.userservice.exception;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.userservice.Model.Booking;
import com.userservice.service.BookingFeign;

@Component
public class BookingFeignFalback implements BookingFeign{

	@Override
	public List<Booking> getBookingsByPassengerId(Integer passengerBookingId) {
		// TODO Auto-generated method stub
		System.out.println("Falback executed");
		return Collections.emptyList();
	}

	@Override
	public String addBooking(String flightNumber, String seatclass, int noOfSeats, Booking booking) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Booking> getBookingsByFlightNumber(String flightNumber) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
