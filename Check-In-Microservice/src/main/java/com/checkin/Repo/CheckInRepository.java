package com.checkin.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checkin.model.CheckIn;
import java.util.List;


@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

	CheckIn findByPassengerId(Integer passengerId);
	
	List<CheckIn> findAllByFlightNumber(String flightNumber);
	
	void deleteAllByFlightNumber(String flightNumber);
}
