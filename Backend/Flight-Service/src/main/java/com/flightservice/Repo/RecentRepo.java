package com.flightservice.Repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flightservice.model.Flight;
import com.flightservice.model.RecentFlight;
import com.flightservice.model.RecentSearch;

public interface RecentRepo extends JpaRepository<RecentSearch, Integer> {
	
	@Query("SELECT rs FROM RecentSearch rs WHERE rs.recentFlights.source = :source AND rs.recentFlights.destination = :destination AND rs.recentFlights.date = :date")
    List<RecentSearch> findBySourceDestinationAndDate(String source, String destination, LocalDate date);

	    

}
