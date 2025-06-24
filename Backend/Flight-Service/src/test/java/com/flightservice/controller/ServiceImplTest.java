package com.flightservice.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flightservice.Repo.FlightRepository;
import com.flightservice.model.Flight;
import com.flightservice.model.Seat;
import com.flightservice.service.BookingOpenFeign;
import com.flightservice.service.FlightServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ServiceImplTest {
	
	 @Mock
	    private FlightRepository flightRepository;
	 
	 @Mock
	 private BookingOpenFeign bookingOpenFeign;


	    @InjectMocks
	    private FlightServiceImpl flightService;

	    private Flight flight1;
	    private Flight flight2;
	    private Seat seat1;
	    private Seat seat2;
	    private Seat seat3;
	    private Seat seat4;

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
	    }

	    @Test
	    void testShowFlightByNumber() {
	        when(flightRepository.findByFlightNumber("AI101")).thenReturn(flight1);
	        Flight flight = flightService.showFlightByNumber("AI101");
	        assertNotNull(flight);
	        assertEquals("Air India", flight.getAirline());
	        verify(flightRepository, times(1)).findByFlightNumber("AI101");
	    }

	    @Test
	    void testFindBySourceAndDestination() {
	        when(flightRepository.findByDepartureAirportAndArrivalAirport("Indira Gandhi International Airport", "Chhatrapati Shivaji Maharaj International Airport"))
	                .thenReturn(Arrays.asList(flight1));
	        List<Flight> flights = flightService.findBySourceAndDestination("Delhi", "Mumbai");
	        assertEquals(1, flights.size());
	        verify(flightRepository, times(1)).findByDepartureAirportAndArrivalAirport("Indira Gandhi International Airport", "Chhatrapati Shivaji Maharaj International Airport");
	    }

	    @Test
	    void testRemoveFlightByNumber() {
	        when(flightRepository.findByFlightNumber("AI101")).thenReturn(flight1);
	        when(flightRepository.deleteByFlightNumber("AI101")).thenReturn(1);
	        when(bookingOpenFeign.deleteBooking("AI101")).thenReturn("Deleted");

	        assertEquals(true, flightService.removeFlightByNumber("AI101") );
	        
	        verify(flightRepository, times(1)).deleteByFlightNumber("AI101");
	    }

	    @Test
	    void testUpdateAvailableSeats() {
	        when(flightRepository.findByFlightNumber("AI101")).thenReturn(flight1);
	        int updatedSeats = flightService.updateAvailableSeats("AI101", 5, "Economy");
	        assertEquals(75, updatedSeats);
	    }

	    @Test
	    void testUpdateFlightStatusByNumber() {
	        when(flightRepository.findByFlightNumber("AI101")).thenReturn(flight1);
	        boolean result = flightService.UpdateFlightStausByNumber("AI101", "Delayed");
	        assertTrue(result);
	        assertEquals("Delayed", flight1.getStatus());
	        verify(flightRepository, times(1)).save(flight1);
	    }
	    
	    @Test
	    void testGetFlightsByRouteAndDate() {
	        // Mocking AIRPORTS HashMap
	        when(flightRepository.findFlightsByRouteAndDate("Indira Gandhi International Airport", "Chhatrapati Shivaji Maharaj International Airport", LocalDate.of(2025, 4, 1)))
	                .thenReturn(Arrays.asList(flight1));

	        List<Flight> flights = flightService.getFlightsByRouteAndDate("Delhi", "Mumbai", LocalDate.of(2025, 4, 1));

	        assertEquals(1, flights.size());
	        assertEquals("AI101", flights.get(0).getFlightNumber());

	        verify(flightRepository, times(1)).findFlightsByRouteAndDate("Indira Gandhi International Airport", "Chhatrapati Shivaji Maharaj International Airport", LocalDate.of(2025, 4, 1));
	    }


}
