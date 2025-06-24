  package com.bookingservice.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookingservice.Exception.BookingHandlingException;
import com.bookingservice.Exception.BookingsNotFoundException;
import com.bookingservice.Exception.FlightNotFoundException;
import com.bookingservice.model.Booking;
import com.bookingservice.model.PassengerDetails;
import com.bookingservice.service.BookingService;
import com.bookingservice.service.EmailService;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/BMS")
@Transactional
public class BookingController {
	
	@Autowired
	private BookingService bookingService;
	
	
	@Autowired
    private EmailService emailService;

	@GetMapping("/bookingsOfPassenger/{passengerBookingId}")
	public ResponseEntity<List<Booking>> getBookings(@PathVariable Integer passengerBookingId) throws BookingsNotFoundException{
		List<Booking> bookings = bookingService.getBookingByPassengerBookingId(passengerBookingId);
		if(bookings.size()== 0) {
			throw new BookingsNotFoundException("No Bookings Available");
		}
		
		
		return ResponseEntity.status(HttpStatus.OK).body(bookings);

	}
	
	@GetMapping("/getPassengerById/{passengerId}")
	public ResponseEntity<PassengerDetails> getPassengerById(@PathVariable Integer passengerId) throws BookingsNotFoundException{
		PassengerDetails pass = bookingService.getPassengerDetailsById(passengerId);
		if(pass == null) {
			throw new BookingsNotFoundException("Passenger Not Found");
		}
		return ResponseEntity.ok(pass);
	}
	@GetMapping("/getByBookingId/{bookingId}")
	public ResponseEntity<List<Booking>> getBookingsById(@PathVariable Integer bookingId) throws BookingsNotFoundException{
		List<Booking> bookings = bookingService.getBookingsById(bookingId);
		if(bookings.size() == 0) {
			throw new BookingsNotFoundException("No Bookings Available for bookingId "+bookingId);
		}
		return ResponseEntity.ok(bookings);
	}
	
	@GetMapping("/bookings/{flightNumber}")
	public ResponseEntity<List<Booking>> getBookings(@PathVariable String flightNumber) throws BookingsNotFoundException{
		List<Booking> bookings = bookingService.getBookingByFlightNumber(flightNumber);
		if(bookings.size()== 0) {
			throw new BookingsNotFoundException("No Bookings Available");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(bookings);

	}
	
	
	
	@GetMapping("/showSeats/{flightNumber}")
	public HashMap<String, String> showAvailableSeats(@PathVariable String flightNumber){
		return bookingService.showAvailableSeats(flightNumber);
	}
	
	@GetMapping("/showSeats/{flightNumber}/{seatclass}")
	public ResponseEntity<String> showAvailableSeatsBySeatClass(@PathVariable String flightNumber,@PathVariable String seatclass) throws BookingsNotFoundException {
		int noOfSeats = bookingService.showAvailableSeatsBySeatClass(flightNumber, seatclass);
		if(noOfSeats == -1) {
			throw new BookingsNotFoundException("No Flight Exists with No "+flightNumber);
		}
		if(noOfSeats == -2) {
			throw new BookingsNotFoundException("Seats Not available");
		}
		return ResponseEntity.status(HttpStatus.OK).body("No Of Seats Available for "+seatclass+" is "+noOfSeats );
	}
	
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@PostMapping("/bookTickets/{flightNumber}/{seatclass}/{noOfSeats}")
	@Transactional
	public ResponseEntity<String> addBooking(@PathVariable String flightNumber,
			@PathVariable String seatclass,
			@PathVariable int noOfSeats
			,@RequestBody @Valid Booking booking) throws BookingsNotFoundException, BookingHandlingException, FlightNotFoundException{
		
		int no = bookingService.addBooking(flightNumber,seatclass,noOfSeats,booking);
		if(no == 1) {
			double price = bookingService.calculateTotalPrice(noOfSeats, seatclass, flightNumber);
//			Map<String, Integer> requestBody = new HashMap<>();
//		    requestBody.put("amount", (int) price); // assuming your Razorpay expects amount in rupees
//		    requestBody.put("bookingId", booking.getPassengerBookingId());
//		    
//		    ResponseEntity<Map<String, Object>> razorpayResponse = razorClient.createOrder(requestBody);
//
//		    Map<String, Object> responseBody = razorpayResponse.getBody();
//
//		    String orderId = (String) responseBody.get("order_id");
				
			emailService.sendEmail(booking.getEmail(), "Tickets Confirmation", "Tickets Booked successfully Amount Paid "+price+"\n Passenger Details : "+booking.getPassengers());
			return ResponseEntity.ok("Order created successfully. Please pay amount "+price + " to book");
			//return ResponseEntity.ok("Please pay "+price+" for confirming the booking");
		//	return ResponseEntity.status(HttpStatus.CREATED).body("Booking Succesful. Your Booking_Id is "+booking.getBookingId());
		}else if(no == -2) {
		throw new BookingHandlingException("Booking Unsuccesful \nPlease add Passengers based on Number of seats mentioned");
		
		}else if(no == -3) {
			throw new BookingHandlingException("Flight is Cancelled\nBooking Not available..");
		}
		throw new BookingHandlingException("Booking Unsuccesful");
	}
	
	@DeleteMapping("/deleteBooking/{flightNumber}")
	public ResponseEntity<String> deleteBooking(@PathVariable String flightNumber) throws BookingHandlingException{
		
		int x = bookingService.deleteBookingByFlightNumber(flightNumber);
		if(x == 1) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Deleted");
		}
		
		throw new BookingHandlingException("Flight is not available ");
		
	}
	@DeleteMapping("/cancelBookingById/{bookingId}")
	public ResponseEntity<String> cancelBooking(@PathVariable Integer bookingId) throws BookingHandlingException{
		
		int x = bookingService.cancelBookingByBookingId(bookingId);
		if(x == 1) {
			emailService.sendEmail("bookingflight60@gmail.com", "Tickets Cancelled", "Your Ticket Has been Cancelled");

			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Deleted");
		}
		
		throw new BookingHandlingException("Booking Id is not available ");
		
	}
	@DeleteMapping("/deletePassenger/{bookingId}/{passengerId}")
	public ResponseEntity<String> deletePassengerByBookingId(@PathVariable Integer bookingId, @PathVariable Integer passengerId) throws BookingHandlingException{
		int x = bookingService.deletePassengerIdByBookingId(bookingId, passengerId);
		if(x == 1) {
			return ResponseEntity.ok("Deleted Passenger Successfully");
		}
		
		throw new BookingHandlingException("Deletion Unsuccessful");
	}
	
	@GetMapping("/getPrice/{noOfSeats}/{seatClass}/{flightNumber}")
	public ResponseEntity<Double> getPrice(@PathVariable Integer noOfSeats,@PathVariable String seatClass, @PathVariable String flightNumber){
		double price = bookingService.calculateTotalPrice(noOfSeats, seatClass, flightNumber);
		return ResponseEntity.ok(price);
	}
}
