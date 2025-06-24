package com.checkin.model;

public class PassengerDetails {

	private Integer passengerId;

	private String passengerName;

	private int seatNumber;

	private String gender;

	private int age;

	private Booking booking;

	private String seatClass;

	public PassengerDetails(Integer passengerId, String passengerName, int seatNumber, String gender, int age,
			Booking booking, String seatClass) {
		super();
		this.passengerId = passengerId;
		this.passengerName = passengerName;
		this.seatNumber = seatNumber;
		this.gender = gender;
		this.age = age;
		this.booking = booking;
		this.seatClass = seatClass;
	}

	public PassengerDetails(String passengerName, int seatNumber, String gender, int age, Booking booking,
			String seatClass) {
		super();
		this.passengerName = passengerName;
		this.seatNumber = seatNumber;
		this.gender = gender;
		this.age = age;
		this.booking = booking;
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

	public String getSeatClass() {
		return seatClass;
	}

	public void setSeatClass(String seatClass) {
		this.seatClass = seatClass;
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
