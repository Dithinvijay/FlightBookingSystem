package com.bookingservice.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookingservice.model.PassengerDetails;

@Repository
public interface PassengerRepo extends JpaRepository<PassengerDetails, Integer> {
	
	void deleteByPassengerId(Integer passengerId);

	PassengerDetails findByPassengerId(Integer passengerId);
}
