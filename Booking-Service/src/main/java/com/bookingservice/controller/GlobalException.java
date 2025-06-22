package com.bookingservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bookingservice.Exception.BookingHandlingException;
import com.bookingservice.Exception.BookingsNotFoundException;
import com.bookingservice.Exception.FlightNotFoundException;

@RestControllerAdvice
public class GlobalException {
	
	private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GlobalException.class);

	
	@ExceptionHandler(BookingsNotFoundException.class)
	public ResponseEntity<String> bookingNotFound(BookingsNotFoundException ex){
		LOGGER.error("Error Occured {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(BookingHandlingException.class)
	public ResponseEntity<String> bookingNotFound(BookingHandlingException ex){
		LOGGER.error("Error Occured {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(FlightNotFoundException.class)
	public ResponseEntity<String> bookingNotFound(FlightNotFoundException ex){
		LOGGER.error("Error Occured {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		LOGGER.error("Error Occured {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
}
