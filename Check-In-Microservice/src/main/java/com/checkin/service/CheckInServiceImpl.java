package com.checkin.service;

import java.time.LocalDateTime;
import java.util.List;import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForLocalDateTime;
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

	@Override
	public int addCheckIn(CheckIn checkIn) {
		LOGGER.info("Checking In by details");
		
		boolean check = false;
		List<Booking> bookings = bookingOpenFeign.getBookings(checkIn.getFlightNumber());
		
		for(Booking booking:bookings) {
			List<PassengerDetails> passengers = booking.getPassengers();
			for(PassengerDetails pass:passengers) {
				if(pass.getPassengerId() == checkIn.getPassengerId()) {
					if(checkInRepository.findByPassengerId(checkIn.getPassengerId()) != null) {
						LOGGER.error("Passenger already checked in with id {}", pass.getPassengerId());
						return -2;
					}
					
					check = true;
					checkIn.setSeatNumber(pass.getSeatNumber());
					checkIn.setSeatClass(pass.getSeatClass());
					checkIn.setCheckedIn(true);
					LOGGER.info("Checking In Successfully done {}", checkIn.getPassengerName());
					checkInRepository.save(checkIn);
					return 1;
				
				}
			}
		}
		
		return -1;
	}

	
	
	
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




	@Override
	public int checkIn(Integer passengerId) {
		// TODO Auto-generated method stub
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
			
	CheckIn	checkIn = new CheckIn();
//	checkIn.setFlightNumber(pass.getBooking().getFlightNumber()));
	checkIn.setPassengerId(passengerId);
	checkIn.setSeatNumber(pass.getSeatNumber());
	checkIn.setSeatClass(pass.getSeatClass());
//	checkIn.setBookingId(pass.getBooking().getBookingId());
	checkIn.setPassengerName(pass.getPassengerName());
	checkIn.setCheckInTime(LocalDateTime.now());
	checkIn.setCheckedIn(true);
	LOGGER.info("Checking In Successfully done {}", checkIn.getPassengerName());
	checkInRepository.save(checkIn);
	return 0;
	}

}
