package com.checkin.controller;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.checkin.exception.BookingsNotFoundException;



@RestControllerAdvice
public class GlobalExceptionController {
	
	private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GlobalExceptionController.class);


	@ExceptionHandler(BookingsNotFoundException.class)
	public ResponseEntity<String> bookingNotFound(BookingsNotFoundException ex){
		LOGGER.error("Error Occured {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
}
