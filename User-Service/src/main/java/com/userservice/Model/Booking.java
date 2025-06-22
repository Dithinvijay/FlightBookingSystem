package com.userservice.Model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Booking {

	private Integer bookingId;

	private String flightNumber;

	private Integer passengerBookingId;

	private Integer noOfSeats;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime bookingDate;

	private String bookingStatus;

	private List<PassengerDetails> passengers;

	public Booking(String flightNumber, Integer passengerBookingId, Integer noOfSeats, LocalDateTime bookingDate,
			String bookingStatus, List<PassengerDetails> passengers) {
		super();
		this.flightNumber = flightNumber;
		this.passengerBookingId = passengerBookingId;
		this.noOfSeats = noOfSeats;
		this.bookingDate = bookingDate;
		this.bookingStatus = bookingStatus;
		this.passengers = passengers;
	}
	
	public Booking(String flightNumber, Integer passengerBookingId, LocalDateTime bookingDate, String bookingStatus,
			List<PassengerDetails> passengers) {
		super();
		this.flightNumber = flightNumber;
		this.passengerBookingId = passengerBookingId;
		this.bookingDate = bookingDate;
		this.bookingStatus = bookingStatus;
		this.passengers = passengers;
	}

	public Booking(String flightNumber, Integer passengerId, LocalDateTime bookingDate, String bookingStatus) {
		super();
		this.flightNumber = flightNumber;
		this.passengerBookingId = passengerId;
		this.bookingDate = bookingDate;
		this.bookingStatus = bookingStatus;
	}

	public Booking() {
		super();
	}

	
	public Integer getNoOfSeats() {
		return noOfSeats;
	}

	public void setNoOfSeats(Integer noOfSeats) {
		this.noOfSeats = noOfSeats;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public List<PassengerDetails> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<PassengerDetails> passengers) {
		this.passengers = passengers;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public Integer getPassengerBookingId() {
		return passengerBookingId;
	}

	public void setPassengerBookingId(Integer passengerId) {
		this.passengerBookingId = passengerId;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	@Override
	public String toString() {
		return "Booking [bookingId=" + bookingId + ", flightNumber=" + flightNumber + ", passengerBookingId="
				+ passengerBookingId + ", noOfSeats=" + noOfSeats + ", bookingDate=" + bookingDate + ", bookingStatus="
				+ bookingStatus + ", passengers=" + passengers + "]";
	}

}
