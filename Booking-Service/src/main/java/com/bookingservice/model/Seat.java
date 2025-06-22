package com.bookingservice.model;

public class Seat {

	private Integer seatId;

	private String seatClass;

	private int noOfSeats;

	private int availableSeats;

	private double price;

	private Flight flight;

	public Seat(Integer seatId, int seatNumber, String seatClass, int noOfSeats, int availableSeats, double price,
			Flight flight) {
		super();
		this.seatId = seatId;
		this.seatClass = seatClass;
		this.noOfSeats = noOfSeats;
		this.price = price;
		this.availableSeats = availableSeats;
		this.flight = flight;
	}

	public Seat(Integer seatId, int seatNumber, String seatClass, int noOfSeats, double price) {
		super();
		this.seatId = seatId;
		this.seatClass = seatClass;
		this.noOfSeats = noOfSeats;
		this.price = price;
		this.availableSeats = noOfSeats;
	}

	public Seat() {
		super();
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Integer getSeatId() {
		return seatId;
	}

	public void setSeatId(Integer id) {
		this.seatId = id;
	}

	public String getSeatClass() {
		return seatClass;
	}

	public void setSeatClass(String seatClass) {
		this.seatClass = seatClass;
	}

	public int getNoOfSeats() {
		return noOfSeats;
	}

	public void setNoOfSeats(int noOfSeats) {
		this.noOfSeats = noOfSeats;
	}

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(int noOfSeats) {
		this.availableSeats = noOfSeats;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	@Override
	public String toString() {
		return "Seat [seatId=" + seatId + ", seatClass=" + seatClass + ", noOfSeats=" + noOfSeats + ", price=" + price
				+ ", availableSeats=" + availableSeats + ", flight=" + flight + "]";
	}

}
