package com.flightservice.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightservice.Exception.FlightHandlingException;
import com.flightservice.Exception.FlightNotFoundException;
import com.flightservice.model.Flight;
import com.flightservice.model.RecentSearch;
import com.flightservice.service.FlightServiceImpl;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/FMS")
@Transactional
public class FlightController {
	
	@Autowired
	FlightServiceImpl flightServiceImpl;
	
	
		@PostMapping("/addFlight")
		public ResponseEntity<String> addFlight(@RequestBody @Valid Flight flight) throws FlightHandlingException {
			 
			if(flightServiceImpl.addFlight(flight)) {
				return ResponseEntity.status(HttpStatus.CREATED).body("Added Flight Details Successfully");
			}
			throw new FlightHandlingException("Flight not added");
			
		}
		
		@GetMapping("/flights/{source}/{destination}")
		public ResponseEntity<List<Flight>> getFlightsBySourceDestination(@PathVariable String source,@PathVariable String destination) throws FlightHandlingException{
			List<Flight> flights = flightServiceImpl.findBySourceAndDestination(source, destination);
			if(flights.size() != 0) {
				return ResponseEntity.status(HttpStatus.OK).body(flights);
			}else {
				throw new FlightHandlingException("No Flights available from "+source+" to "+destination);
			}
		}
//		
		@GetMapping("flights/{airline}")
		public ResponseEntity<List<Flight>> showFlightsByAirLine(@PathVariable String airline) throws FlightHandlingException{
			List<Flight> flights = flightServiceImpl.showFlightByAirLine(airline);
			if(flights.size() != 0) {
				return ResponseEntity.status(HttpStatus.OK).body(flights);
			}else {
				throw new FlightHandlingException("Flights not avaiable for this "+airline);
			}
		}
		
		@GetMapping("flightstatus/{flightNumber}")
		public ResponseEntity<String> showFlightStatus(@PathVariable String flightNumber) throws FlightHandlingException, FlightNotFoundException{
			String status = flightServiceImpl.getFlightStatus(flightNumber);
			if(status != null) {
				return ResponseEntity.status(HttpStatus.OK).body(status);
		}
			throw new FlightNotFoundException("Flight Not Found with given Number");
		}
		
		
//		
		@GetMapping("/getByNumber/{flightNumber}")
		public ResponseEntity<Flight> showFlightDetailsByFlightNumber(@PathVariable String flightNumber) throws FlightNotFoundException{
			Flight flight = flightServiceImpl.showFlightByNumber(flightNumber);
			if(flight != null){
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(flight);
			}else {
				
				throw new FlightNotFoundException("No Flight Found By this Number");
			}
		}
		
		@PutMapping("/updatestatus/{flightNumber}/{status}")
		public ResponseEntity<String> updateStatusOfFlightByNumber(@PathVariable String flightNumber,@PathVariable String status) throws FlightNotFoundException{
		if(flightServiceImpl.UpdateFlightStausByNumber(flightNumber, status)){
				return ResponseEntity.status(HttpStatus.OK).body("Flight status updated to "+status);
			}else {
				throw new FlightNotFoundException("No Flight Found By this Number");
			}
		}
//		
		@DeleteMapping("/deleteByNumber/{flightNumber}")
		public ResponseEntity<String> deleteFlightByNumber(@PathVariable String flightNumber) throws FlightHandlingException{
			if( flightServiceImpl.removeFlightByNumber(flightNumber)) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Flight Deleted By Flight Number"+flightNumber);
			}else {
				throw new FlightHandlingException("Flight Not Deleted Successfully");
			}
		}
//		
//		
		@GetMapping("/flights/{source}/{destination}/{date}")
		public ResponseEntity<List<Flight>> findByDateAndLocation(@PathVariable String source,
				@PathVariable String destination
				,@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws FlightHandlingException, FlightNotFoundException{
			
		 List<Flight> flights =	flightServiceImpl.getFlightsByRouteAndDate(source, destination, date);
		 if(flights.size() != 0) {
			 return ResponseEntity.status(HttpStatus.OK).body(flights);
		 }else {
			 throw new FlightNotFoundException("Flights not available currently");
		 }
		}
		
		@GetMapping("/availableSeats/{flightNumber}")
		public ResponseEntity<HashMap<String, Integer>> findAvailableSeats(@PathVariable String flightNumber) throws FlightHandlingException{
			HashMap<String, Integer> mapp = flightServiceImpl.getAvailableSeats(flightNumber);
			if(mapp == null) {
				throw new FlightHandlingException("Seats Not Available");
			}
			return ResponseEntity.status(HttpStatus.OK).body(mapp);
			
		}
//		
		@GetMapping("/availableSeats/{flightNumber}/{seatclass}")
		public ResponseEntity<Integer> findAvailableSeatsByNumber(@PathVariable String flightNumber,@PathVariable String seatclass) throws FlightHandlingException{
			int noOfSeats = flightServiceImpl.getAvailableSeats(flightNumber,seatclass);
		if( noOfSeats == -1) {
			throw new FlightHandlingException("Flight Doesnot Exsits");
		}else if(noOfSeats == -2) {
			throw new FlightHandlingException("Seats Not Available");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(noOfSeats);
		}
		
		@PutMapping("/updateSeats/{flightNumber}/{seatsBooked}/{seatclass}")
		public ResponseEntity<String> updateAvailableSeats(@PathVariable String flightNumber,@PathVariable Integer seatsBooked,@PathVariable String seatclass) throws FlightHandlingException{
		int updatedSeats	= flightServiceImpl.updateAvailableSeats(flightNumber, seatsBooked,seatclass);
		if(updatedSeats == -1) {
			throw new FlightHandlingException("Flight Does Not Exists with Number "+flightNumber);
		}
		else if(updatedSeats == -2) {
			throw new FlightHandlingException(seatsBooked +" Seats are not Available ");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body("Seats Booked Successfully");
		}
		
		@PutMapping("/updateSeatsAfterDeletion/{flightNumber}/{seatsDeleted}/{seatclass}")
		public ResponseEntity<String> updateAvailableSeatsWhenDeleted(@PathVariable String flightNumber,@PathVariable Integer seatsDeleted,@PathVariable String seatclass) throws FlightHandlingException{
		int updatedSeats	= flightServiceImpl.updateAvailableSeatsWhenDeleted(flightNumber, seatsDeleted,seatclass);
		if(updatedSeats == -1) {
			throw new FlightHandlingException("Flight Does Not Exists with Number "+flightNumber);
		}
		
		
		return ResponseEntity.status(HttpStatus.OK).body("Seats Added Successfully");
		}
		@GetMapping("/getFareOfSeatClass/{flightNumber}/{seatclass}")
		public ResponseEntity<Double> getPrice(@PathVariable String flightNumber, @PathVariable String seatclass) throws FlightNotFoundException{
			double price = flightServiceImpl.getFare(flightNumber, seatclass);
			if(price == -1) {
				throw new FlightNotFoundException("Flight does not exists");
			}
			return ResponseEntity.ok(price);
		}
		
		@GetMapping("/getRecentSearchs")
		public ResponseEntity<List<RecentSearch>> getPrice() throws FlightNotFoundException{
			return ResponseEntity.ok(flightServiceImpl.findRecentSearches());
		}
//		
		
		@DeleteMapping("/deleteByFlightId/{Id}")
		public ResponseEntity<String> deleteById(@PathVariable Long Id) throws FlightNotFoundException{
			int x = flightServiceImpl.deleteById(Id);
			if(x == -1) {
				throw new FlightNotFoundException("Flight does not exists");
			}
			return ResponseEntity.ok("Deleted");
		}
}
