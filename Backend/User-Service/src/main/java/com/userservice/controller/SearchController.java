package com.userservice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.Model.Flight;
import com.userservice.exception.FlightHandlingException;
import com.userservice.service.UserServiceImpl;

import feign.Param;

@RestController
public class SearchController {

		@Autowired
		UserServiceImpl searchServiceImpl;
		
	    @GetMapping("/flights")
		public ResponseEntity<List<Flight>> getFlightsByLocationandDate(@RequestParam String source,
				@RequestParam String destination,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
		List<Flight> flights =	searchServiceImpl.getFlightsByLocationAndDate(source, destination, date);
//		if(flights.size() == 0) {
//			throw new FlightHandlingException("No Flights Available");
//		}
		return ResponseEntity.status(HttpStatus.OK).body(flights);
		}
}
