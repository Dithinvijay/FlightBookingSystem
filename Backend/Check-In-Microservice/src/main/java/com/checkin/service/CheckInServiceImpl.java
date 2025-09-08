package com.checkin.service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.checkin.Repo.CheckInRepository;
import com.checkin.model.Booking;
import com.checkin.model.CheckIn;
import com.checkin.model.PassengerDetails;

import jakarta.transaction.Transactional;

@Service
public class CheckInServiceImpl implements CheckInService {
	
	private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CheckInServiceImpl.class);
	
	@Autowired
	private BookingOpenFeign bookingOpenFeign;
	
	@Autowired
	private CheckInRepository checkInRepository;
	
	@Autowired
	private EmailService emailService;
	
	
	@Override
	public boolean isCheckedIn(Integer passengerId) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching if passenger is checked In or not with id {}", passengerId);

		CheckIn checkIn = checkInRepository.findByPassengerId(passengerId);
		if(checkIn != null) {
			LOGGER.info("Already passenger is checked In  with id {}", passengerId);
			return true;
		}
		
		LOGGER.info(" passenger is not checked In  with id {}", passengerId);

		return false;
	}

	@Override
	public List<CheckIn> getAllCheckIns(String flightNumber) {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching All Check Ins in flight with flight number {}", flightNumber);

		return checkInRepository.findAllByFlightNumber(flightNumber);
	}

	@Override
	@Transactional
	public int deleteCheckInByFlightNumber(String flightNumber) {
		// TODO Auto-generated method stusb
		LOGGER.info("Deleting All Check Ins in flight with flight number {}", flightNumber);

		List<CheckIn> checkIns = checkInRepository.findAllByFlightNumber(flightNumber);
		if(checkIns.size() == 0) {
			LOGGER.error("No Check Ins available for flight with flight number {}", flightNumber);

			return -1;
		}
		
		LOGGER.info("Successfully deleted All Check Ins in flight with flight number {}", flightNumber);
		checkInRepository.deleteAllByFlightNumber(flightNumber);
		return 1;
	}



	//main for check-in
	@Override
	public int checkIn(Integer passengerId) {
		LOGGER.info("Fetching passenger details by id {}", passengerId);
		PassengerDetails pass = bookingOpenFeign.getPassengerDetailsById(passengerId);
		if(pass == null) {
			LOGGER.error("Passenger not found with id {}", passengerId);
			return -1;
		}
		if(checkInRepository.findByPassengerId(passengerId) != null) {
			LOGGER.error("Passenger already checked in with id {}", passengerId);
			return -2;
		}
		
		// Find booking that contains this passenger
		Booking passengerBooking = null;
		for(Booking booking : bookingOpenFeign.getBookingsById(pass.getPassengerId())) {
			for(PassengerDetails passenger : booking.getPassengers()) {
				if(passenger.getPassengerId().equals(passengerId)) {
					passengerBooking = booking;
					break;
				}
			}
			if(passengerBooking != null) break;
		}
		
		if(passengerBooking == null) {
			LOGGER.error("Booking not found for passenger with id {}", passengerId);
			return -1;
		}
		
		CheckIn checkIn = new CheckIn();
		checkIn.setFlightNumber(passengerBooking.getFlightNumber());
		checkIn.setPassengerId(passengerId);
		checkIn.setPassengerBookingId(passengerBooking.getPassengerBookingId());
		checkIn.setSeatNumber(pass.getSeatNumber());
		checkIn.setSeatClass(pass.getSeatClass());
		checkIn.setBookingId(passengerBooking.getBookingId());
		checkIn.setPassengerName(pass.getPassengerName());
		checkIn.setCheckInTime(LocalDateTime.now());
		checkIn.setCheckedIn(true);
		checkInRepository.save(checkIn);
		String emailTemplate = "✈️ Check-In Successful! ✅\n"
				+ "\n"
				+ "Dear " + pass.getPassengerName() + ",\n"
				+ "\n"
				+ "We are pleased to inform you that your check-in has been successfully completed.\n"
				+ "\n"
				+ "✈️ Flight Check-In Status: Confirmed\n"
				+ "\n"
				+ "Thank you for choosing VK-Flights. We are committed to making your journey comfortable and enjoyable.\n"
				+ "\n"
				+ "We wish you a safe and pleasant travel experience, " + pass.getPassengerName() + ".\n"
				+ "\n"
				+ "Warm regards,\n"
				+ "Customer Service Team\n"
				+ "VK-Flights\n"
				+ "✈️ Fly Smart. Fly VK.";
		
		emailService.sendEmail(passengerBooking.getEmail(), "✅ Check-In Confirmation – VK-Flights", emailTemplate);
		LOGGER.info("Checking In Successfully done {}", checkIn.getPassengerName());
		return 1;
	}

}
