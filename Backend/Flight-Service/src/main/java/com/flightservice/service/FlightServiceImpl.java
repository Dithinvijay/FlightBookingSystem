package com.flightservice.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.hibernate.ObjectDeletedException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightservice.Repo.FlightRepository;
import com.flightservice.Repo.RecentRepo;
import com.flightservice.model.Flight;
import com.flightservice.model.RecentFlight;
import com.flightservice.model.RecentSearch;
import com.flightservice.model.Seat;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FlightServiceImpl implements FlightService {

	private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(FlightServiceImpl.class);

	@Autowired
	FlightRepository flightRepository;
	
	@Autowired
	RecentRepo recentRepo;

	@Autowired
	BookingOpenFeign bookingOpenFeign;

	public static final Map<String, String> AIRPORTS = new HashMap<>();

	static {
		AIRPORTS.put("Delhi", "Indira Gandhi International Airport");
		AIRPORTS.put("Mumbai", "Chhatrapati Shivaji Maharaj International Airport");
		AIRPORTS.put("Bengaluru", "Kempegowda International Airport");
		AIRPORTS.put("Chennai", "Chennai International Airport");
		AIRPORTS.put("Kolkata", "Netaji Subhas Chandra Bose International Airport");
		AIRPORTS.put("Hyderabad", "Rajiv Gandhi International Airport");
		AIRPORTS.put("Pune", "Pune International Airport");
		AIRPORTS.put("Ahmedabad", "Sardar Vallabhbhai Patel International Airport");
		AIRPORTS.put("Kochi", "Cochin International Airport");
		AIRPORTS.put("Jaipur", "Jaipur International Airport");
	}

	@Override
	public boolean addFlight(Flight flight) {
		LOGGER.info("Adding Flight Details with flight number {}", flight.getFlightNumber());
		// TODO Auto-generated method stub
		if (flight.getSeats() != null) {
			LOGGER.info("Setting Seat details for flight Number {}", flight.getFlightNumber());
			for (Seat seat : flight.getSeats()) {
				seat.setFlight(flight);
			}
			LOGGER.info("Successfully added Seat details with flight number {}", flight.getFlightNumber());

		}
		if (flightRepository.save(flight) != null) {
			LOGGER.info("Successfully added flight details with flight number {}", flight.getFlightNumber());
			return true;
		}
		return false;
	}

	@Override
	public List<Flight> showFlightByAirLine(String Airline) {
		// TODO Auto-generated method stub
		LOGGER.info("Searching Flights By Airline {}", Airline);
		List<Flight> flights = flightRepository.findByAirline(Airline);

		if (flights.size() == 0) {
//			LOGGER.error("No Flights Available for Airline "+Airline);
		} else {
			LOGGER.info(" {} Flights Available for Airline {}", flights.size(), Airline);
		}
		return flights;
	}

	@Override
	public List<Flight> getFlightsByRouteAndDate(String departure, String arrival, LocalDate date) {

		LOGGER.info("Searching for flights from {} to {} on {}", departure, arrival, date);
		List<Flight> flights = flightRepository.findFlightsByRouteAndDate(AIRPORTS.get(departure),
				AIRPORTS.get(arrival), date);
		if (flights.size() != 0) {
			RecentFlight rs = new RecentFlight(departure,arrival,date);
			RecentSearch rc = new RecentSearch(rs);
			List<RecentSearch> list = recentRepo.findBySourceDestinationAndDate(departure,arrival,date);
			if(list.size() == 0) {
			recentRepo.save(rc);
			}
			LOGGER.info("Found {} flights from {} to {} on {}", flights.size(), departure, arrival, date);
		}
		return flights;
	}

	@Override
	public Flight showFlightByNumber(String number) {
		// TODO Auto-generated method stub

		LOGGER.info("Fetching Flight By Flight Number {}", number);
		Flight flight = flightRepository.findByFlightNumber(number);
		if (flight != null) {
			LOGGER.info("Flight Found for Number {}", number);
		}
		return flight;

	}

	@Override
	public List<Flight> findBySourceAndDestination(String source, String destination) {
		// TODO Auto-generated method stub
//		return flightRepository.findByDepartureAirportAndArrivalAirport(AIRPORTS.get(source), AIRPORTS.get(destination));
		LOGGER.info("Fetching Flights from {} to {}", source, destination);
		List<Flight> flights = flightRepository.findByDepartureAirportAndArrivalAirport(source,
				destination);

		if (flights.size() != 0) {
			LOGGER.info("{} flights found from {} to {}", flights.size(), source, destination);
		}
		
		return flights;
	}

	@Override
	@Transactional
	public boolean removeFlightByNumber(String flightNumber) {
		// TODO Auto-generated method stub
		LOGGER.info("Removing Flight By flight number {} ", flightNumber);
		Flight flight = flightRepository.findByFlightNumber(flightNumber);
		if (flight == null) {
			return false;
		}
		String string = bookingOpenFeign.deleteBooking(flightNumber);
		int x = flightRepository.deleteByFlightNumber(flightNumber);

		if (x != 0) {
			LOGGER.info("Successfully deleted flight by flight number {}", flightNumber);
		}
		return x == 0 ? false : true;
	}

//	s

	@Override
	public int getAvailableSeats(String flightNumber, String seatclass) {

		LOGGER.info("Fetching Available Seats for flight number {} with seat class {}", flightNumber, seatclass);
		Flight flight = flightRepository.findByFlightNumber(flightNumber);
		if (flight == null) {
			return -1;
		}
		List<Seat> seats = flight.getSeats();
		for (Seat seat : seats) {
			if (seat.getSeatClass().equals(seatclass)) {
				LOGGER.info(" {} - {}", seatclass, seat.getAvailableSeats());

				return seat.getAvailableSeats();
			}
		}
		return -2;
	}

//
//
	@Override
	public int updateAvailableSeats(String flightNumber, Integer noOfSeatsBooked, String seatclass) {

		LOGGER.info("Updating Available Seats for flight number {}", flightNumber);

		Flight flight = flightRepository.findByFlightNumber(flightNumber);
		if (flight == null) {
			return -1;
		}

//		if(flight.getAvailableSeats() < (int) noOfSeatsBooked) {
//			return -2;
//		}
		int x = 0;

		List<Seat> seats = flight.getSeats();
		for (Seat seat : seats) {
			if (seat.getSeatClass().equals(seatclass)) {
				x = seat.getAvailableSeats() < noOfSeatsBooked ? -2 : seat.getAvailableSeats() - noOfSeatsBooked;
				if (x != -2) {
					LOGGER.info("Successfully Updated Seat class {} with {}", seatclass, x);
					seat.setAvailableSeats(x);
				}

			}
		}

		// flight.setAvailableSeats(flight.getAvailableSeats() - (int) noOfSeatsBooked);
		flightRepository.save(flight);

		return x;

	}
	
	
	@Override
	public int updateAvailableSeatsWhenDeleted(String flightNumber, Integer noOfSeatsDeleted, String seatclass) {

		LOGGER.info("Updating Available Seats for flight number {}", flightNumber);

		Flight flight = flightRepository.findByFlightNumber(flightNumber);
		if (flight == null) {
			return -1;
		}

//		if(flight.getAvailableSeats() < (int) noOfSeatsBooked) {
//			return -2;
//		}
		int x = 0;

		List<Seat> seats = flight.getSeats();
		for (Seat seat : seats) {
			if (seat.getSeatClass().equals(seatclass)) {
				x =  seat.getAvailableSeats() + noOfSeatsDeleted;
					LOGGER.info("Successfully Updated Seat class {} with {}", seatclass, x);
					seat.setAvailableSeats(x);
				

			}
		}

		// flight.setAvailableSeats(flight.getAvailableSeats() - (int) noOfSeatsBooked);
		flightRepository.save(flight);

		return x;

	}

	@Override
	public boolean UpdateFlightStausByNumber(String flightNumber, String status) {
		// TODO Auto-generated method stub
		LOGGER.info("Updating Status for flight Number {}", flightNumber);

		Flight flight = flightRepository.findByFlightNumber(flightNumber);
		if (flight == null) {
			return false;
		}
		flight.setStatus(status);
		LOGGER.info("Successfully Updated status with flight number {} with status {}", flightNumber , status);
		flightRepository.save(flight);
		return true;
	}

	@Override
	public HashMap<String, Integer> getAvailableSeats(String flightNumber) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching Available Seats for flight number {}", flightNumber);

		HashMap<String, Integer> mapp = new HashMap<>();

		Flight flight = flightRepository.findByFlightNumber(flightNumber);
		if (flight == null) {
			return null;
		}
		LOGGER.info("Seats Available For flight Number {}", flightNumber);
		for (Seat seat : flight.getSeats()) {
			LOGGER.info(" {} - {}", seat.getSeatClass(), seat.getAvailableSeats());
			mapp.put(seat.getSeatClass(), seat.getAvailableSeats());

		}

		return mapp;

	}

	@Override
	public String getFlightStatus(String flightNumber) {
		// TODO Auto-generated method stub

		LOGGER.info("Fetching status of flight by flight number {}", flightNumber);

		Flight flight = flightRepository.findByFlightNumber(flightNumber);
		if (flight == null) {
			return null;
		}
		LOGGER.info("Flight status for flight number {} is {}", flightNumber, flight.getStatus());
		return flight.getStatus();
	}

	@Override
	public double getFare(String flightNumber, String seatclass) {
		// TODO Auto-generated method 
		LOGGER.info("Getting Fare for flight Number {} and seat class {}" , flightNumber,seatclass);
		Flight flight = flightRepository.findByFlightNumber(flightNumber);
		if(flight == null) {
			return -1;
		}
		List<Seat> seats = flight.getSeats();
		for(Seat seat:seats) {
			if(seat.getSeatClass().equalsIgnoreCase(seatclass)) {
				LOGGER.info("{} -> {}" , seatclass, seat.getPrice())  ;
				return seat.getPrice();
			}
		}
		return 0;
	}

	@Override
	public int deleteById(Long Id) {
		// TODO Auto-generated method stub
		Flight flights = flightRepository.findById(Id).orElse(null);
		if(flights == null) {
			return -1;
		}
		 flightRepository.deleteById(Id);
		 return 0;
	}
	
	@Override
	public List<RecentSearch> findRecentSearches() {
		// TODO Auto-generated method stub
		List<RecentSearch> list = recentRepo.findAll();
		return list;
	}

}
