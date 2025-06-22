package com.flightservice.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.flightservice.model.Flight;
import com.flightservice.model.RecentSearch;

@Service
public interface FlightService {
	boolean addFlight(Flight flight);
	boolean removeFlightByNumber(String flightNumber);
	boolean UpdateFlightStausByNumber(String flightNumber,String status);
	List<Flight> showFlightByAirLine(String Airline);
	public List<Flight> findBySourceAndDestination(String source, String destination);
    public List<Flight> getFlightsByRouteAndDate(String departure, String arrival, LocalDate date) ;
	Flight showFlightByNumber(String number);
	int getAvailableSeats(String flightNumber,String seatclass);
	HashMap<String,Integer> getAvailableSeats(String flightNumber);
    int	updateAvailableSeats(String flightNumber,Integer noOfSeatsBooked,String seatclass);
    String getFlightStatus(String flightNumber);
	int updateAvailableSeatsWhenDeleted(String flightNumber, Integer noOfSeatsDeleted, String seatclass);
	double getFare(String flightNumber, String seatclass);
	int deleteById(Long Id);
	List<RecentSearch> findRecentSearches();
}
