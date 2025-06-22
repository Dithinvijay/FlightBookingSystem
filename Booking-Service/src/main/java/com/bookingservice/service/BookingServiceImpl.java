package com.bookingservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.bookingservice.Repo.BookingRepo;
import com.bookingservice.Repo.PassengerRepo;
import com.bookingservice.model.Booking;
import com.bookingservice.model.Flight;
import com.bookingservice.model.PassengerDetails;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {
	
	private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BookingServiceImpl.class);


	@Autowired
	private BookingRepo bookingRepo;

	@Autowired
	private PassengerRepo passengerRepo;
	
	@Autowired
	private FlightClient flightClient;

	@Override
	public List<Booking> getBookingByPassengerBookingId(Integer passengerBookingId) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching Bookings By Passenger Booking ID {}",passengerBookingId);

		List<Booking> bookings = bookingRepo.findByPassengerBookingId(passengerBookingId);
		LocalDateTime nowDateTime = LocalDateTime.now();
		
			if(bookings.size() != 0) {
				LOGGER.info(" Fetched {} bookings with passenger booking ID {}", bookings.size(), passengerBookingId);
			}
			for (Booking booking : bookings) {
			List<PassengerDetails> pass = booking.getPassengers();
			for (PassengerDetails passenger : pass) {
				passenger.setBooking(booking);
			}
		}

		return bookings.stream().filter(booking -> booking.getBookingDate().isAfter(nowDateTime))
				.collect(Collectors.toList());

	}

	@Override
	public HashMap<String, String> showAvailableSeats(String flightNumber) {
		LOGGER.info("Fetching Available Seats with flight number {}", flightNumber);

		// TODO Auto-generated method stub
		HashMap<String, Integer> mapp = flightClient.getAvailableSeats(flightNumber);
		HashMap<String, String> updatedMap = new HashMap<String, String>();
		for (Map.Entry<String, Integer> entry : mapp.entrySet()) {
			LOGGER.info("{} -> {}", entry.getKey(), entry.getValue());
			if (entry.getValue() == 0) {
				updatedMap.put(entry.getKey(), "Seats are Not Available");
			} else {
				updatedMap.put(entry.getKey(), entry.getValue() + " seats are Available");
			}
		}
		return updatedMap;
	}

	@Override
	public int showAvailableSeatsBySeatClass(String flightNumber, String seatclass) {
		// TODO Auto-generated method stub
		
		LOGGER.info("Fetching Available Seats with flight number {} and Seat class {}", flightNumber, seatclass);

		Integer noOfSeats = flightClient.getAvailableSeatsByclass(flightNumber, seatclass);
		LOGGER.info("{} -> {} ", seatclass, noOfSeats);

		return noOfSeats;
	}

	@Override
	public int addBooking(String flightNumber, String seatclass, int noOfSeats, Booking booking) {
		LOGGER.info("Fetching Flight if available with number {}", flightNumber);

		Flight flight = flightClient.showFlightDetailsByNumber(flightNumber);
		if(flight != null) {
			LOGGER.info("Flight Found");

		}
		LOGGER.info("Fetching available seats for Flight: {}, Class: {}", flightNumber, seatclass);
		Integer availableSeats = flightClient.getAvailableSeatsByclass(flightNumber, seatclass);
		LOGGER.info("Available seats received: {}", availableSeats);
		// TODO Auto-generated method stub
		if (availableSeats >= noOfSeats) {

			List<PassengerDetails> list = booking.getPassengers();
			if (noOfSeats != list.size()) {
				return -2;
			}
			if (flightClient.getFlightStatus(flightNumber).equals("Cancelled")) {
				return -3;
			}
            booking.setBookingDate(LocalDateTime.now());
			booking.setBookingStatus("Confirmed");
			booking.setNoOfSeats(noOfSeats);
			booking.setFlightNumber(flightNumber);
		
			int seatno = availableSeats;
			for (PassengerDetails pass : list) {
				pass.setSeatNumber(seatno);
				pass.setSeatClass(seatclass);
				seatno--;
				pass.setBooking(booking);
			}
			String s = flightClient.updateAvailableSeats(flightNumber, noOfSeats, seatclass);
			System.out.println(s);
			if (bookingRepo.save(booking) != null) {
				return 1;
			}
		}
		return -1;

	}

	@Override
	public int deleteBookingByFlightNumber(String flightNumber) {
		
		LOGGER.info("Deleting Bookings By Flight number {} ", flightNumber);

		// TODO Auto-generated method stub
		List<Booking> bookings = bookingRepo.findByFlightNumber(flightNumber);
		if (bookings.size() == 0) {
			return -1;
		}else {
			LOGGER.info("deleted Booking successfully of flight number {}", flightNumber);
		}

		bookingRepo.deleteByFlightNumber(flightNumber);

		return 1;
	}

	@Override
	public List<Booking> getBookingByFlightNumber(String flightNumber) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching Bookings with flight number {} ", flightNumber);

		List<Booking> bookings = bookingRepo.findByFlightNumber(flightNumber);
		
		if(bookings.size() != 0) {
			LOGGER.info(" Fetched {} bookings with Flight number {}", bookings.size(), flightNumber);
		}
		return bookings;
	}

//	@Override
//	public int deleteBookingByBookingId(Integer bookingId) {
//		// TODO Auto-generated method stub
//
//		
//		LOGGER.info("Deleting Passenger By Booking Id {} ", bookingId);
//		List<Booking> booking = bookingRepo.findByBookingId(bookingId);
//		LOGGER.info("No Of Bookings for Id {} is {}",bookingId,booking.size());
//		if(booking.size() != 0) {
//		for(Booking bookings:booking) {
//				bookings.setBookingStatus("Cancelled");
//				bookingRepo.save(bookings);
//		}
//		//bookingRepo.deleteByBookingId(bookingId);
//		
//		LOGGER.info("Successfully deleted the booking with booking id {}", bookingId);
//		return 1;
//		}else {
//			LOGGER.error("Not Found Booking with Id {}" , bookingId);
//			return 0;
//		}
//	}

	@Override
	public int cancelBookingByBookingId(Integer bookingId) {
	    LOGGER.info("Deleting Passenger By Booking Id {} ", bookingId);
	    
	    List<Booking> bookingList = bookingRepo.findByBookingId(bookingId);
	    LOGGER.info("No Of Bookings for Id {} is {}", bookingId, bookingList.size());
	    
	    if (!bookingList.isEmpty()) {
	        for (Booking booking : bookingList) {
	            booking.setBookingStatus("Cancelled");
	            bookingRepo.save(booking);
	            
	            List<PassengerDetails> passengerDetailsList = booking.getPassengers(); // Assuming booking has passengers

	            if (passengerDetailsList != null) {
	                for (PassengerDetails pass : passengerDetailsList) {
	                    // Update available seats for each passenger
	                    flightClient.updateAvailableSeatsAfterDeletion(
	                        booking.getFlightNumber(), 
	                        1, 
	                        pass.getSeatClass()
	                    );
	                }
	            } else {
	                LOGGER.warn("No passengers found for booking id {}", bookingId);
	            }
	        }
	        
	        LOGGER.info("Successfully cancelled the booking with booking id {}", bookingId);
	        return 1;
	        
	    } else {
	        LOGGER.error("Not Found Booking with Id {}", bookingId);
	        return 0;
	    }
	}

	
	@Override
	public List<Booking> getBookingsById(Integer bookingId) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching Bookings with Booking Id {} ", bookingId);

		List<Booking> bookings = bookingRepo.findByBookingId(bookingId);
		if(bookings.size() != 0) {
			LOGGER.info("Successfully Fetched booking with booking id {} ", bookingId);
		}
		return bookings;
	}

	@Override
	public int deletePassengerIdByBookingId(Integer bookingId, Integer passengerId) {
		// TODO Auto-generated method stub
		 LOGGER.info("Deleting Passenger By Passenger Id {} ", passengerId);

		    List<Booking> bookings = bookingRepo.findByBookingId(bookingId);
		    
		    for (Booking booking : bookings) {
		        List<PassengerDetails> passengerDetails = booking.getPassengers();
		        
		        Iterator<PassengerDetails> iterator = passengerDetails.iterator();
		        while (iterator.hasNext()) {
		            PassengerDetails pass = iterator.next();
		            if (pass.getPassengerId().equals(passengerId)) {  // Use equals() instead of ==
		                iterator.remove(); // Removes from the list
		                passengerRepo.deleteById(passengerId);  // Deletes from DB
		                flightClient.updateAvailableSeatsAfterDeletion(booking.getFlightNumber(), 1, pass.getSeatClass());
		                
		                LOGGER.info("1 Seat is added successfully");
		                LOGGER.info("Passenger Id {} is deleted successfully ", passengerId);
		                
		                // Save the booking to reflect the deletion
		                booking.setPassengers(passengerDetails);
		                bookingRepo.save(booking);
		                
		                return 1;
		            }
		        }
		    }
		    return -1; 
	}

	@Override
	public double calculateTotalPrice(Integer noOfSeats, String seatclass, String flightNumber) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching Fare of flight number {} with seatclass {} ", flightNumber, seatclass);
		double seatPrice = flightClient.getPrice(flightNumber, seatclass);
		if(seatPrice != 0) {
			LOGGER.info("{} -> {}", seatclass,seatPrice);

		}
		return noOfSeats * seatPrice;
	}

	@Override
	public PassengerDetails getPassengerDetailsById(Integer passengerId) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching passenger Details");
		PassengerDetails pass = passengerRepo.findByPassengerId(passengerId);
		if(pass == null) {
			LOGGER.info("Passenege with {} not found", passengerId);
			return null;
		}
		LOGGER.info("Passenger found with Id {}", passengerId);
		return pass;
	}

}
