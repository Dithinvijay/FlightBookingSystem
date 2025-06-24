package com.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.userservice.exception.BookingHandlingException;
import com.userservice.exception.BookingsNotFoundException;
import com.userservice.exception.FlightHandlingException;
import com.userservice.exception.FlightNotFoundException;
import com.userservice.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalHandlingException {

	@ExceptionHandler(FlightHandlingException.class)
	public ResponseEntity<String> flighthandling(FlightHandlingException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	@ExceptionHandler(FlightNotFoundException.class)
	public ResponseEntity<String> flighthandling(FlightNotFoundException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	@ExceptionHandler(BookingHandlingException.class)
	public ResponseEntity<String> bookinghandling(BookingHandlingException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	@ExceptionHandler(BookingsNotFoundException.class)
	public ResponseEntity<String> bookinghandling(BookingsNotFoundException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> userHandlingException(UsernameNotFoundException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
}
