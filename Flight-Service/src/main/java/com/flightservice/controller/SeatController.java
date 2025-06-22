package com.flightservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightservice.service.SeatService;

@RestController
public class SeatController {
	@Autowired
	private SeatService seatService;

		@DeleteMapping("/deleteBySeatId/{id}")
		public ResponseEntity<String> deleteBySeatId(@PathVariable Integer id){
			
			int x = seatService.removeSeatById(id);
			if(x != 1) {
				throw new RuntimeException("Not Deleted");
			}
			
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Deleted Record Succesfully");
		}
	
}
