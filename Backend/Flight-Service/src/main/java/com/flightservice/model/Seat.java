package com.flightservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Seat {

	@Id
	private Integer seatId;

	@NotBlank(message = "Please enter seat class")
	private String seatClass;

	@NotNull(message = "Please enter total number of seats")
	private int noOfSeats;

	@NotNull(message = "Please enter available number of seats")
	private int availableSeats;

	@NotNull(message = "Please enter price of particular seat class")
	private double price;

	@ManyToOne
	@JoinColumn(name = "id", nullable = false)
	@JsonBackReference
	private Flight flight;

	public Seat(@NotBlank(message = "Please enter seat class") String seatClass,
			@NotNull(message = "Please enter total number of seats") int noOfSeats,
			@NotNull(message = "Please enter available number of seats") int availableSeats,
			@NotNull(message = "Please enter price of particular seat class") double price, Flight flight) {
		super();
		this.seatClass = seatClass;
		this.noOfSeats = noOfSeats;
		this.availableSeats = availableSeats;
		this.price = price;
		this.flight = flight;
	}

	public Seat(Integer seatId, String seatClass, int noOfSeats, int availableSeats, double price, Flight flight) {
		super();
		this.seatId = seatId;
		this.seatClass = seatClass;
		this.noOfSeats = noOfSeats;
		this.price = price;
		this.availableSeats = availableSeats;
		this.flight = flight;
	}

	public Seat(Integer seatId, String seatClass, int noOfSeats, double price) {
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
