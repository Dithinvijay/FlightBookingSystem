package com.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.userservice.Model.Booking;
import com.userservice.Model.Flight;
import com.userservice.Model.PassengerDetails;
import com.userservice.Model.Seat;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
	
	
	@Mock
	private FlightOpenFeign flightOpenFeign;
	

	@Mock
	private BookingFeign bookingFeign;
	
	@InjectMocks
	private UserServiceImpl userServiceImpl;
	
	 private Flight flight1;
	    private Flight flight2;
	    private Seat seat1;
	    private Seat seat2;
	    private Seat seat3;
	    private Seat seat4;
	    
	    private  List<Booking> bookings;
		
		private PassengerDetails pass1;
		
		private PassengerDetails pass2;
		
		private Booking b1;

	    @BeforeEach
	    void setUp() {
	        seat1 = new Seat(1, "Economy", 100, 80, 5000.0, flight1);
	        seat2 = new Seat(2, "Business", 20, 15, 15000.0, flight1);
	        seat3 = new Seat(3, "Economy", 100, 90, 4500.0, flight2);
	        seat4 = new Seat(4, "Business", 25, 20, 14000.0, flight2);

	        flight1 = new Flight("AI101", "Air India", "Delhi", "Mumbai", 
	        		LocalDateTime.of(2025, 4, 1, 10, 0), LocalDateTime.of(2025, 4, 1, 12, 0), "Scheduled", 
	                Arrays.asList(seat1, seat2));

	        flight2 = new Flight("IG202", "IndiGo", "Mumbai", "Bangalore", 
	        		LocalDateTime.of(2025, 4, 2, 14, 0), LocalDateTime.of(2025, 4, 2, 16, 0), "On Time", 
	                Arrays.asList(seat3, seat4));
	        
	        bookings = new ArrayList<>();
			pass1 = new PassengerDetails("Pavan Sai Gopal","male",22,50,"Economy",b1);
			pass2 = new PassengerDetails("Rakshit","male",22,49,"Economy",b1);

			List<PassengerDetails> list = new ArrayList<>();
			
			list.add(pass1);
			list.add(pass2);
			
			 b1 = new Booking("AI101",12106348,2,LocalDateTime.of(2025, 4, 1, 10, 0),"Confirmed", list);
			bookings.add(b1);
	    }
	    
	    @Test 
	    void testGetFlightsByLocationAndDate() {
	    	when(flightOpenFeign.getFlightsOnLocationAndDate("Delhi", "Mumbai", LocalDate.of(2025, 4, 2))).thenReturn(Arrays.asList(flight1));
	    	
	    	assertEquals(Arrays.asList(flight1), userServiceImpl.getFlightsByLocationAndDate("Delhi", "Mumbai", LocalDate.of(2025, 4, 2)));
	    	
	    	verify(flightOpenFeign, times(1)).getFlightsOnLocationAndDate("Delhi", "Mumbai", LocalDate.of(2025, 4, 2));
	    	
	    }
	    
	    
	    @Test
	    void testBookFlight() {
	        when(bookingFeign.addBooking("AI101", "Economy", 2, b1)).thenReturn("Added Flight Details Successfully");
	        
	        int result = userServiceImpl.bookFlight("AI101", "Economy", 2, b1);
	        
	        assertEquals(0, result);
	        verify(bookingFeign, times(1)).addBooking("AI101", "Economy", 2, b1);
	    }
	    
//	    void testBookFlight() {
//	    	when(bookingFeign.addBooking("AI101", "Economy", 2, b1)).thenReturn(0);
//	    	
//	    	
//	    }
	    
	    @Test
	    void testGetBookingsByPassengerId() {
	    	when(bookingFeign.getBookingsByPassengerId(12106348)).thenReturn(bookings);
	    	List<Booking> list2 = userServiceImpl.getBookingsByPassengerId(12106348);
	    	assertEquals(bookings, list2);
	    	assertNotNull(list2);
	    	verify(bookingFeign, times(1)).getBookingsByPassengerId(12106348);
	    	
	    	
	    			
	    }

}



//@Override
//public List<Booking> getBookingsByPassengerId(Integer passengerId) {
//	// TODO Auto-generated method stub
//	List<Booking> booking = bookingFeign.getBookingsByPassengerId(passengerId);
//	return booking;
//}