package com.bookingservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class PassengerDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer passengerId;

	@NotBlank(message = "Passenger Name is required")
	private String passengerName;

	@NotBlank(message = "Gender is required")
	private String gender;

	@NotNull(message = "Age is required")
	private int age;

	private int seatNumber;

	private String seatClass;

	@ManyToOne
	@JoinColumn(name = "booking_id", nullable = false)
	@JsonBackReference
	private Booking booking;

	public PassengerDetails(@NotBlank(message = "Passenger Name is required") String passengerName,
			@NotBlank(message = "Gender is required") String gender, @NotNull(message = "Age is required") int age,
			int seatNumber, String seatClass, Booking booking) {
		super();
		this.passengerName = passengerName;
		this.gender = gender;
		this.age = age;
		this.seatNumber = seatNumber;
		this.seatClass = seatClass;
		this.booking = booking;
	}

	public PassengerDetails(@NotBlank(message = "Passenger Name is required") String passengerName,
			@NotBlank(message = "Gender is required") String gender, @NotBlank(message = "Age is required") int age,
			Booking booking) {
		super();
		this.passengerName = passengerName;
		this.gender = gender;
		this.age = age;
		this.booking = booking;
	}

	public PassengerDetails(@NotBlank(message = "Passenger Name is required") String passengerName,
			@NotBlank(message = "Gender is required") String gender, @NotNull(message = "Age is required") int age,
			int seatNumber, Booking booking) {
		super();
		this.passengerName = passengerName;
		this.gender = gender;
		this.age = age;
		this.seatNumber = seatNumber;
		this.booking = booking;
	}

	public String getSeatClass() {
		return seatClass;
	}

	public void setSeatClass(String seatClass) {
		this.seatClass = seatClass;
	}

	public Integer getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Integer passengerId) {
		this.passengerId = passengerId;
	}

	public int getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public PassengerDetails() {
		super();
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	@Override
	public String toString() {
		return "PassengerDetails [passengerId=" + passengerId + ", passengerName=" + passengerName + ", gender="
				+ gender + ", age=" + age + "]";
	}

}
