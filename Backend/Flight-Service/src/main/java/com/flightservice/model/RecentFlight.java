package com.flightservice.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Embeddable;



@Embeddable
public class RecentFlight {
	
	private String source;
	
	private	String destination;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
	private LocalDate date;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public RecentFlight(String source, String destination, LocalDate date) {
		super();
		this.source = source;
		this.destination = destination;
		this.date = date;
	}

	public RecentFlight() {
		super();
	}
	
	

}
