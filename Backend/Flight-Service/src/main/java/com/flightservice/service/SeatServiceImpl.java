package com.flightservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightservice.Repo.SeatRepository;
import com.flightservice.model.Seat;

import jakarta.transaction.Transactional;

@Service
public class SeatServiceImpl implements SeatService {

	@Autowired
	private SeatRepository seatRepository;
	
	@Override
	@Transactional
	public int removeSeatById(Integer id) {
		// TODO Auto-generated method stub
		Seat seat = seatRepository.findById(id).get();
		if(seat == null) {
			return 0;
		}
		 seatRepository.deleteBySeatId(id);
		 return 1;
	}

}
