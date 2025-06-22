package com.checkin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkin.model.CheckIn;
import com.checkin.service.CheckInService;

@RestController
@RequestMapping("/checkIn")
public class CheckInController {
	
	@Autowired
	private CheckInService checkInService;

	@PostMapping("/addCheckIn")
	public ResponseEntity<String> addCheckIn(@RequestBody CheckIn checkIn){
		
		int x= checkInService.addCheckIn(checkIn);
		
		if( x == 1) {
		return ResponseEntity.ok("CheckIn Successfully done "+checkIn.getPassengerName()+ " Your seat number is "+ checkIn.getSeatNumber() + " "+checkIn.getSeatClass()+"class \nWish You a Happy and Safe Journey..");
	}else if(x == -2) {
		return ResponseEntity.badRequest().body("Already Checked In");
	}
		return ResponseEntity.badRequest().body("Not Checked In successfully");
	}
	
	@GetMapping("/checkIn/{passengerId}")
	public ResponseEntity<String> checkIn(@PathVariable Integer passengerId){
		int x = checkInService.checkIn(passengerId);
		if(x == -1 || x == -2) {
			return ResponseEntity.badRequest().body("CheckIn Failed");
		}
	
		return ResponseEntity.ok("Check In success ");
		
	}
	
	@GetMapping("/checkedInStatus/{passengerId}")
	public ResponseEntity<Boolean> isCheckedIn(@PathVariable Integer passengerId){
		
		boolean res = checkInService.isCheckedIn(passengerId);
		if(res == true) {
			return ResponseEntity.ok(true);
		}
		return ResponseEntity.badRequest().body(false);
	}
	
	
	@GetMapping("/getAllCheckIns/{flightNumber}")
	public ResponseEntity<List<CheckIn>> getAllCheckIn(@PathVariable String flightNumber){
		
		List<CheckIn> list = checkInService.getAllCheckIns(flightNumber);
		
		return ResponseEntity.ok(list);
	}
	
	
	@DeleteMapping("/deleteAll/{flightNumber}")
	public ResponseEntity<String> deleteAllCheckInsByFN(@PathVariable String flightNumber){
		int x = checkInService.deleteCheckInByFlightNumber(flightNumber);
		if(x == -1) {
			return ResponseEntity.badRequest().body("No checkIns Available");
		}
		return ResponseEntity.ok("Deleted CheckIns successfully");
	}
}
