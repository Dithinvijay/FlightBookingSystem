package com.userservice.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.userservice.Model.Flight;
import com.userservice.exception.CustomErrorDecoder;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@FeignClient(name = "Flight-Service",configuration = CustomErrorDecoder.class)
public interface FlightOpenFeign {

		@GetMapping("/FMS/flights/{source}/{destination}/{date}")
		List<Flight> getFlightsOnLocationAndDate(@PathVariable String source,
				@PathVariable String destination
				,@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
		
		
		
		  @PostMapping("/FMS/addFlight")
		    String addFlight(@RequestBody @Valid Flight flight);
}
