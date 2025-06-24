package com.flightservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flightservice.Exception.FlightHandlingException;
import com.flightservice.Exception.FlightNotFoundException;
import com.flightservice.service.FlightServiceImpl;

@RestControllerAdvice
public class GlobalHandlingException {
	
	  private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GlobalHandlingException.class);


	@ExceptionHandler(FlightHandlingException.class)
	public ResponseEntity<String> flighException(FlightHandlingException ex){
		LOGGER.error("Error Occurred {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
	
	
	@ExceptionHandler(FlightNotFoundException.class)
	public ResponseEntity<String> flighException(FlightNotFoundException ex){
		LOGGER.error("Error Occurred {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
}
