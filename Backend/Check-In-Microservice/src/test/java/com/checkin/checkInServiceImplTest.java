package com.checkin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.checkin.Repo.CheckInRepository;
import com.checkin.model.CheckIn;
import com.checkin.service.CheckInServiceImpl;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
public class checkInServiceImplTest {
	
	@Mock
	private CheckInRepository checkInRepository;
	
	@InjectMocks
	private CheckInServiceImpl checkInServiceImpl;
	
	private CheckIn checkIn;
	
	@BeforeEach
	void setUp() {
		checkIn = new CheckIn(1,"Pavan",50,"Economy",1,"VS-102",LocalDateTime.of(2025,4,1,10,0),false);
	}
	
	@Test
	void isCheckedInTest() {
	
		when(checkInRepository.findByPassengerId(1)).thenReturn(checkIn);
		boolean ans = checkInServiceImpl.isCheckedIn(1);
		assertTrue(ans);
		verify(checkInRepository, times(1)).findByPassengerId(1);
	}
	
	@Test
	void getAllCheckInsTest() {
		when(checkInRepository.findAllByFlightNumber("VS-102")).thenReturn(Arrays.asList(checkIn));
		List<CheckIn> list = checkInServiceImpl.getAllCheckIns("VS-102");
		assertEquals(Arrays.asList(checkIn), list);
		verify(checkInRepository, times(1)).findAllByFlightNumber("VS-102");
	}
	
	@Test
	void deleteCheckInByFlightNumber() {
		when(checkInRepository.findAllByFlightNumber("VS-102")).thenReturn(Arrays.asList(checkIn));
		doNothing().when(checkInRepository).deleteAllByFlightNumber("VS-102");
		
		assertEquals(1, checkInServiceImpl.deleteCheckInByFlightNumber("VS-102"));
	}
	

}


//@Override
//@Transactional
//public int deleteCheckInByFlightNumber(String flightNumber) {
//	// TODO Auto-generated method stusb
//	LOGGER.info("Deleting All Check Ins in flight with flight number {}", flightNumber);
//
//	List<CheckIn> checkIns = checkInRepository.findAllByFlightNumber(flightNumber);
//	if(checkIns.size() == 0) {
//		LOGGER.error("No Check Ins available for flight with flight number {}", flightNumber);
//
//		return -1;
//	}
//	
//	LOGGER.info("Successfully deleted All Check Ins in flight with flight number {}", flightNumber);
//	checkInRepository.deleteAllByFlightNumber(flightNumber);
//	return 1;
//}
