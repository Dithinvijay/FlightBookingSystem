package com.checkin;

import com.checkin.controller.CheckInController;
import com.checkin.model.CheckIn;
import com.checkin.service.CheckInService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CheckInController.class)
public class CheckInControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckInService checkInService;

    @Autowired
    private ObjectMapper objectMapper;

    private CheckIn checkIn;

    @BeforeEach
    void setUp() {
        checkIn = new CheckIn();
        checkIn.setPassengerId(1);
        checkIn.setPassengerName("John Doe");
        checkIn.setSeatNumber(10);
        checkIn.setSeatClass("Economy");
        checkIn.setBookingId(101);
        checkIn.setFlightNumber("AI101");
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setCheckedIn(true);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void testAddCheckIn_Success() throws Exception {
        when(checkInService.addCheckIn(any(CheckIn.class))).thenReturn(1);

        mockMvc.perform(post("/checkIn/addCheckIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkIn)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CheckIn Successfully done John Doe Your seat number is 10 Economyclass")));

        verify(checkInService, times(1)).addCheckIn(any(CheckIn.class));
    }

    @Test
    void testAddCheckIn_AlreadyCheckedIn() throws Exception {
        when(checkInService.addCheckIn(any(CheckIn.class))).thenReturn(-2);

        mockMvc.perform(post("/checkIn/addCheckIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkIn)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Already Checked In"));

        verify(checkInService, times(1)).addCheckIn(any(CheckIn.class));
    }

    @Test
    void testAddCheckIn_NotCheckedInSuccessfully() throws Exception {
        when(checkInService.addCheckIn(any(CheckIn.class))).thenReturn(0); // Assuming 0 or other non-1, non -2 indicates failure

        mockMvc.perform(post("/checkIn/addCheckIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(checkIn)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not Checked In successfully"));

        verify(checkInService, times(1)).addCheckIn(any(CheckIn.class));
    }

    @Test
    void testIsCheckedIn_Success() throws Exception {
        Integer passengerId = 1;
        when(checkInService.isCheckedIn(passengerId)).thenReturn(true);

        mockMvc.perform(get("/checkIn/checkedInStatus/" + passengerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(checkInService, times(1)).isCheckedIn(passengerId);
    }

    @Test
    void testIsCheckedIn_Failure() throws Exception {
        Integer passengerId = 2;
        when(checkInService.isCheckedIn(passengerId)).thenReturn(false);

        mockMvc.perform(get("/checkIn/checkedInStatus/" + passengerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));

        verify(checkInService, times(1)).isCheckedIn(passengerId);
    }

    @Test
    void testGetAllCheckIn_Success() throws Exception {
        String flightNumber = "AI101";
        List<CheckIn> checkIns = Arrays.asList(checkIn);
        when(checkInService.getAllCheckIns(flightNumber)).thenReturn(checkIns);

        mockMvc.perform(get("/checkIn/getAllCheckIns/" + flightNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].passengerName", is("John Doe")));

        verify(checkInService, times(1)).getAllCheckIns(flightNumber);
    }

    @Test
    void testGetAllCheckIn_Failure() throws Exception {
        String flightNumber = "AI202";
        when(checkInService.getAllCheckIns(flightNumber)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/checkIn/getAllCheckIns/" + flightNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(checkInService, times(1)).getAllCheckIns(flightNumber);
    }
}