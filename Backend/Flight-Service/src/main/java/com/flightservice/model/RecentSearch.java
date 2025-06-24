package com.flightservice.model;

import java.util.List;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RecentSearch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Embedded
	private RecentFlight recentFlights;
	
	
	public RecentSearch() {
		super();
	}
	public RecentSearch(RecentFlight recentFlights) {
		super();
		this.recentFlights = recentFlights;
	}
	public RecentSearch(Integer id,RecentFlight recentFlights) {
		super();
		this.id = id;
		this.recentFlights = recentFlights;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public RecentFlight getRecentFlights() {
		return recentFlights;
	}
	public void setRecentFlights(RecentFlight recentFlights) {
		this.recentFlights = recentFlights;
	}
	
}
