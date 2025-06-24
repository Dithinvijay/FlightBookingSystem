package com.bookingservice.service;

import java.util.HashMap;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import com.bookingservice.Exception.CustomErrorDecoder;
import com.bookingservice.model.Flight;

@FeignClient(name = "FLIGHT-SERVICE",configuration = CustomErrorDecoder.class)
public interface FlightClient {
	
	@GetMapping("/FMS/availableSeats/{flightNumber}")
	HashMap<String, Integer> getAvailableSeats(@PathVariable String flightNumber);

	@GetMapping("/FMS/availableSeats/{flightNumber}/{seatclass}")
	Integer getAvailableSeatsByclass(@PathVariable String flightNumber,@PathVariable String seatclass);
	
	@PutMapping("/FMS/updateSeats/{flightNumber}/{seatsBooked}/{seatclass}")
	String updateAvailableSeats(@PathVariable String flightNumber,@PathVariable Integer seatsBooked,@PathVariable String seatclass );
	
	@PutMapping("/FMS/updateSeatsAfterDeletion/{flightNumber}/{seatsDeleted}/{seatclass}")
	String updateAvailableSeatsAfterDeletion(@PathVariable String flightNumber,@PathVariable Integer seatsDeleted,@PathVariable String seatclass);
	
	@GetMapping("/FMS/flightstatus/{flightNumber}")
	String getFlightStatus(@PathVariable String flightNumber);
	
	@GetMapping("/FMS/getByNumber/{flightNumber}")
	Flight showFlightDetailsByNumber(@PathVariable String flightNumber);
	
	@GetMapping("/FMS/getFareOfSeatClass/{flightNumber}/{seatclass}")
	double getPrice(@PathVariable String flightNumber, @PathVariable String seatclass);
	
	
//    @PutMapping("/{flightNumber}/updateSeats")
//    void updateAvailableSeats(@PathVariable String flightNumber, @RequestParam int newSeatCount);

}
