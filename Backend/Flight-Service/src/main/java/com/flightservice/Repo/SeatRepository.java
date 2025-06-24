package com.flightservice.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightservice.model.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

		void deleteBySeatId(Integer seatId);
}
