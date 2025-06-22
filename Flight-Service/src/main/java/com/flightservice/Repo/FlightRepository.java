package com.flightservice.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flightservice.model.Flight;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
	Flight findByFlightNumber(String flightNumber);
	int deleteByFlightNumber(String flightNumber);
    List<Flight> findByDepartureAirportAndArrivalAirport(String departureAirport, String arrivalAirport);
    List<Flight> findByAirline(String airline);
    
    @Query("SELECT f FROM Flight f WHERE f.departureAirport = :departureAirport AND f.arrivalAirport = :arrivalAirport AND DATE(f.departureTime) = :departureDate")
    List<Flight> findFlightsByRouteAndDate(
        @Param("departureAirport") String departureAirport,
        @Param("arrivalAirport") String arrivalAirport,
        @Param("departureDate") LocalDate departureDate
    );
    
    
}


