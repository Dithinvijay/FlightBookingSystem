package com.bookingservice.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bookingId;

	private String flightNumber;

	private Integer passengerBookingId;

	private Integer noOfSeats;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime bookingDate;
	
	@Column(nullable = false)
	private String email;

	private String bookingStatus;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<PassengerDetails> passengers;

	public Booking() {
		super();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Booking(@NotBlank(message = "Flight number is required") String flightNumber,
			@NotNull(message = "Passenger Id is required") Integer passengerBookingId,
			@NotNull(message = "No Of Seats is required") Integer noOfSeats,
			@NotNull(message = "Booking Date is required") LocalDateTime bookingDate, String bookingStatus,
			List<PassengerDetails> passengers) {
		super();
		this.flightNumber = flightNumber;
		this.passengerBookingId = passengerBookingId;
		this.noOfSeats = noOfSeats;
		this.bookingDate = bookingDate;
		this.bookingStatus = bookingStatus;
		this.passengers = passengers;
	}

	public Booking(@NotBlank(message = "Flight number is required") String flightNumber,
			@NotBlank(message = "Passenger Id is required") Integer passengerBookingId,
			@NotBlank(message = "Booking Date is required") LocalDateTime bookingDate,
			@NotBlank(message = "Booking Status is required") String bookingStatus, List<PassengerDetails> passengers) {
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

	public Booking(@NotBlank(message = "Flight number is required") String flightNumber,
			@NotNull(message = "Passenger Id is required") Integer passengerBookingId, Integer noOfSeats,
			@NotNull(message = "Booking Date is required") LocalDateTime bookingDate, String email,
			String bookingStatus, List<PassengerDetails> passengers) {
		super();
		this.flightNumber = flightNumber;
		this.passengerBookingId = passengerBookingId;
		this.noOfSeats = noOfSeats;
		this.bookingDate = bookingDate;
		this.email = email;
		this.bookingStatus = bookingStatus;
		this.passengers = passengers;
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
