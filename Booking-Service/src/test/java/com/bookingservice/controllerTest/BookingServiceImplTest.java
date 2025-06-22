package com.bookingservice.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookingservice.Repo.BookingRepo;
import com.bookingservice.model.Booking;
import com.bookingservice.model.PassengerDetails;
import com.bookingservice.service.BookingService;
import com.bookingservice.service.BookingServiceImpl;
import com.bookingservice.service.FlightClient;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

	@Mock
	private BookingRepo bookingRepo;
	
	@Mock
	private FlightClient flightClient;
	
	@InjectMocks
	private BookingServiceImpl bookingService;
	
    private  List<Booking> bookings;
		
	private PassengerDetails pass1;
	
	private PassengerDetails pass2;
	
	private Booking b1;
	
	
	@BeforeEach
	void setUp() {
		bookings = new ArrayList<>();
		pass1 = new PassengerDetails("Pavan Sai Gopal","male",22,50,"Economy",b1);
		pass2 = new PassengerDetails("Rakshit","male",22,49,"Business",b1);

		List<PassengerDetails> list = new ArrayList<>();
		
		list.add(pass1);
		list.add(pass2);
		
		 b1 = new Booking("VS-102",12106348,2,LocalDateTime.of(2025, 4, 1, 10, 0),"Confirmed", list);
		bookings.add(b1);
		
	}
	
	@Test
	void testGetBookingByPassengerBookingId() {
		
		when(bookingRepo.findByPassengerBookingId(12106348)).thenReturn(bookings);
		
		List<Booking> bookingsById = bookingService.getBookingByPassengerBookingId(12106348);
		assertNotNull(bookings);
		assertEquals(0, bookingsById.size());
		verify(bookingRepo, times(1)).findByPassengerBookingId(12106348);
	}
	
	@Test
	void testShowAvailableSeats() {
		HashMap<String, Integer> mapp = new HashMap<>();
		mapp.put("Economy", 50);
		mapp.put("Business",49);
		when(flightClient.getAvailableSeats("VS-102")).thenReturn(mapp);
		HashMap<String,String> mapp2 = bookingService.showAvailableSeats("VS-102");
		assertNotNull(mapp2);
		verify(flightClient, times(1)).getAvailableSeats("VS-102");
	}
	
	@Test
	void testShowAvailableSeatsBySeatClass() {
		when(flightClient.getAvailableSeatsByclass("VS-102", "Economy")).thenReturn(50);
		int x = bookingService.showAvailableSeatsBySeatClass("VS-102", "Economy");
		assertEquals(50, x);
		verify(flightClient , times(1)).getAvailableSeatsByclass("VS-102","Economy");
	}
	
	@Test
	void testDeleteBookingByFlightNumber() {
		when(bookingRepo.findByFlightNumber("VS-102")).thenReturn(bookings);
		//when(bookingRepo.deleteByFlightNumber("VS-102")).thenReturn(1);
		doNothing().when(bookingRepo).deleteByFlightNumber("VS-102");
		
		 int x = bookingService.deleteBookingByFlightNumber("VS-102");
		 assertEquals(1, x);		
	}
	
	@Test
	void testGetBookingsByFlightNumber() {
		when(bookingRepo.findByFlightNumber("VS-102")).thenReturn(bookings);
		List<Booking> bookings2 = bookingService.getBookingByFlightNumber("VS-102");
		assertNotNull(bookings2);
		assertEquals(bookings, bookings2);
		verify(bookingRepo , times(1)).findByFlightNumber("VS-102");
		
		
	}
	
	
	@Test
	void testDeleteBookingByBookingId() {
		when(bookingRepo.findByBookingId(12106348)).thenReturn(bookings);
		doNothing().when(bookingRepo).deleteByBookingId(12106348);
		
		int x = bookingService.cancelBookingByBookingId(12106348);
		assertEquals(1,x );
		assertNotNull(x);
		verify(bookingRepo , times(1)).findByBookingId(12106348);
		verify(bookingRepo , times(1)).deleteByBookingId(12106348);

	}
	
	@Test
	void testCalculateTotalPrice() {
		when(flightClient.getPrice("VS-102", "Economy")).thenReturn(1500.0);
		
		double price = bookingService.calculateTotalPrice(2, "Economy", "VS-102");
		assertNotNull(price);
		assertEquals(3000, price);
		verify(flightClient, times(1)).getPrice("VS-102", "Economy");
		
	}
	
	
}





