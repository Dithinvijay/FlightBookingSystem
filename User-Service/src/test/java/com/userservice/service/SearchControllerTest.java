package com.userservice.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.userservice.Model.Flight;
import com.userservice.Model.Seat;
import com.userservice.controller.SearchController;
import com.userservice.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {
	
	private MockMvc mockMvc;

    @Mock
    private UserServiceImpl searchServiceImpl;

    @InjectMocks
    private SearchController searchController;
    
    
    private Flight flight1;
    private Flight flight2;
    private Seat seat1;
    private Seat seat2;
    private Seat seat3;
    private Seat seat4;
    
    @BeforeEach
    void setUp() {
        seat1 = new Seat(1, "Economy", 100, 80, 5000.0, flight1);
        seat2 = new Seat(2, "Business", 20, 15, 15000.0, flight1);
        seat3 = new Seat(3, "Economy", 100, 90, 4500.0, flight2);
        seat4 = new Seat(4, "Business", 25, 20, 14000.0, flight2);

        flight1 = new Flight("AI101", "Air India", "Delhi", "Mumbai", 
        		LocalDateTime.of(2025, 4, 1, 10, 0), LocalDateTime.of(2025, 4, 1, 12, 0), "Scheduled", 
                Arrays.asList(seat1, seat2));

        flight2 = new Flight("IG202", "IndiGo", "Mumbai", "Bangalore", 
        		LocalDateTime.of(2025, 4, 2, 14, 0), LocalDateTime.of(2025, 4, 2, 16, 0), "On Time", 
                Arrays.asList(seat3, seat4));
        
    }

    @Test
    void testGetFlightsByLocationAndDate() throws Exception {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();

        // Mock Data

        // Mock Service Call
        LocalDate testDate = LocalDate.of(2025, 4, 1);
        when(searchServiceImpl.getFlightsByLocationAndDate("Delhi", "Mumbai", testDate)).thenReturn(Arrays.asList(flight1));

        // Perform GET request
        mockMvc.perform(get("/flights")
                .param("source", "Delhi")
                .param("destination", "Mumbai")
                .param("date", testDate.toString())  // Convert date to string
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].flightNumber").value("AI101"));

        // Verify service method was called
        verify(searchServiceImpl, times(1)).getFlightsByLocationAndDate("Delhi", "Mumbai", testDate);
    }

}
