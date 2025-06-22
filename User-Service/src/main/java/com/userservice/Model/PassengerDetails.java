package com.userservice.Model;

public class PassengerDetails {

	private Integer passengerId;

	private String passengerName;

	private String gender;

	private int age;

	private int seatNumber;

	private String seatclass;

	private Booking booking;

	public PassengerDetails(String passengerName, String gender, int age, Booking booking) {
		super();
		this.passengerName = passengerName;
		this.gender = gender;
		this.age = age;
		this.booking = booking;
	}

	public PassengerDetails(String passengerName, String gender, int age, int seatNumber, String seatclass,
			Booking booking) {
		super();
		this.passengerName = passengerName;
		this.gender = gender;
		this.age = age;
		this.seatNumber = seatNumber;
		this.seatclass = seatclass;
		this.booking = booking;
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

	public String getSeatclass() {
		return seatclass;
	}

	public void setSeatclass(String seatclass) {
		this.seatclass = seatclass;
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
				+ gender + ", age=" + age + ", booking=" + booking + "]";
	}

}
