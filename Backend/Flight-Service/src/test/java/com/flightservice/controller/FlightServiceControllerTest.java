package com.flightservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightservice.Exception.FlightNotFoundException;
import com.flightservice.Repo.FlightRepository;
import com.flightservice.model.Flight;
import com.flightservice.model.Seat;
import com.flightservice.service.FlightService;
import com.flightservice.service.FlightServiceImpl;
import com.flightservice.service.SeatService;

import jakarta.validation.Valid;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = FlightController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SeatController.class))
public class FlightServiceControllerTest {
	
	
//	@InjectMocks
//	private FlightController flightController;
//	
	@MockBean
	private FlightServiceImpl flightServiceImpl;
	
	@MockBean
    private SeatService seatService; 
	
//	@MockBean
//	private FlightRepository flightRepository;
	
	 @Autowired
	 private MockMvc mockMvc;
	
	private Flight flight;
    private Seat seat;
    
    @Autowired
    private ObjectMapper objectMapper;  
    
    @BeforeEach
    void setUp() {
    	seat = new Seat(3,"Economy", 100,50, 100.0,flight);
    	flight = new Flight("AI101", "Air India", "Delhi", "Mumbai", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Scheduled", Arrays.asList(seat));
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()); 
    }
	
    
    @Test
    void testAddFlightEndpoint_Success() throws Exception {
        // Arrange
        when(flightServiceImpl.addFlight(any(Flight.class))).thenReturn(true);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(post("/FMS/addFlight")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(flight)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Added Flight Details Successfully"));

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).addFlight(any(Flight.class));
    }
    
    @Test
    void testShowFlightsByAirline_Success() throws Exception {
        // Arrange
        List<Flight> flights = Arrays.asList(flight);
        when(flightServiceImpl.showFlightByAirLine("Air India")).thenReturn(flights);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(get("/FMS/flights/Air India")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].airline", is("Air India")));

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).showFlightByAirLine("Air India");
    }

    
    @Test
    void testShowFlightStatus_Success() throws Exception {
        // Arrange
        when(flightServiceImpl.getFlightStatus("AI101")).thenReturn("Scheduled");

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(get("/FMS/flightstatus/AI101")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Scheduled"));

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).getFlightStatus("AI101");
    }
    
    @Test
    void testDeleteFlightByNumber_Success() throws Exception {
        // Arrange
        String flightNumberToDelete = "AI101";
        when(flightServiceImpl.removeFlightByNumber(flightNumberToDelete)).thenReturn(true);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(delete("/FMS/deleteByNumber/" + flightNumberToDelete)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Flight Deleted By Flight NumberAI101"));

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).removeFlightByNumber(flightNumberToDelete);
    }
    
    
    @Test
    void testFindByDateAndLocation_Success() throws Exception {
        // Arrange
        String source = "Delhi";
        String destination = "Mumbai";
        LocalDate date = LocalDate.of(2025, 4, 1);
        List<Flight> flights = Arrays.asList(flight);
        when(flightServiceImpl.getFlightsByRouteAndDate(source, destination, date)).thenReturn(flights);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(get("/FMS/flights/" + source + "/" + destination + "/" + date)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].flightNumber", is("AI101")));

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).getFlightsByRouteAndDate(source, destination, date);
    }
    
    
    @Test
    void testFindAvailableSeats_Success() throws Exception {
        // Arrange
        String flightNumber = "AI101";
        HashMap<String, Integer> availableSeats = new HashMap<>();
        availableSeats.put("Economy", 50);
        availableSeats.put("Business", 10);
        when(flightServiceImpl.getAvailableSeats(flightNumber)).thenReturn(availableSeats);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(get("/FMS/availableSeats/" + flightNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.Economy", is(50)))
                .andExpect(jsonPath("$.Business", is(10)));

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).getAvailableSeats(flightNumber);
    }
    
    @Test
    void testFindAvailableSeatsByNumber_Success() throws Exception {
        // Arrange
        String flightNumber = "AI101";
        String seatClass = "Economy";
        when(flightServiceImpl.getAvailableSeats(flightNumber, seatClass)).thenReturn(50);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(get("/FMS/availableSeats/" + flightNumber + "/" + seatClass)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).getAvailableSeats(flightNumber, seatClass);
    }
    
    @Test
    void testGetPrice_Success() throws Exception {
        // Arrange
        String flightNumber = "AI101";
        String seatClass = "Economy";
        double price = 1500.0;
        when(flightServiceImpl.getFare(flightNumber, seatClass)).thenReturn(price);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(get("/FMS/getFareOfSeatClass/" + flightNumber + "/" + seatClass)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.0"));

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).getFare(flightNumber, seatClass);
    }

    @Test
    void testUpdateAvailableSeats_Success() throws Exception {
        // Arrange
        String flightNumber = "AI101";
        int seatsBooked = 2;
        String seatClass = "Economy";
        when(flightServiceImpl.updateAvailableSeats(flightNumber, seatsBooked, seatClass)).thenReturn(10); // Return value doesn't matter for success, just not -1 or -2

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(put("/FMS/updateSeats/" + flightNumber + "/" + seatsBooked + "/" + seatClass)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Seats Booked Successfully"));

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).updateAvailableSeats(flightNumber, seatsBooked, seatClass);
    }

    @Test
    void testUpdateAvailableSeatsWhenDeleted_Success() throws Exception {
        // Arrange
        String flightNumber = "AI101";
        int seatsDeleted = 1;
        String seatClass = "Economy";
        when(flightServiceImpl.updateAvailableSeatsWhenDeleted(flightNumber, seatsDeleted, seatClass)).thenReturn(11); // Return value doesn't matter for success, just not -1

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(put("/FMS/updateSeatsAfterDeletion/" + flightNumber + "/" + seatsDeleted + "/" + seatClass)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Seats Added Successfully")); // Assuming the success message is the same

        // Verify that the service method was called
        verify(flightServiceImpl, times(1)).updateAvailableSeatsWhenDeleted(flightNumber, seatsDeleted, seatClass);
    }
    
//	@Test
//	public void addFlightTest() throws JsonProcessingException, Exception {
//		
//		
//        when(flightServiceImpl.addFlight(any(Flight.class))).thenReturn(true);
//
//        ResponseEntity<String> response = flightController.addFlight(flight);
//        assertNotNull(response.getBody());
//        assertEquals("Added Flight Details Successfully", response.getBody());
//        
//      //  String requestBody = new ObjectMapper().writeValueAsString(flight);
//        
////        mockMvc.perform(post("/FMS/addFlight")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(requestBody))  // ✅ Pass JSON data
////                .andExpect(status().isCreated())  // ✅ Expect HTTP 201
////                .andExpect(content().string("Added Flight Details Successfully"));
////        verify(flightServiceImpl, times(1)).addFlight(any(Flight.class));
//    }
//	
//	@Test
//    void testShowFlightsByAirline() throws Exception {
//        List<Flight> flights = Arrays.asList(flight);
//        when(flightServiceImpl.showFlightByAirLine("Air India")).thenReturn(flights);
//
//        ResponseEntity<List<Flight>> response = flightController.showFlightsByAirLine("Air India");
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().size());
//        assertEquals("Air India", response.getBody().get(0).getAirline());
//        
//        mockMvc.perform(get("/FMS/flights/Air India")  // Test the URL
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1))
//                .andExpect(jsonPath("$[0].airline").value("Air India"));
//        
//        verify(flightServiceImpl, times(1)).showFlightByAirLine("Air India");
//    }
//	
//	
//	@Test
//    void testShowFlightDetailsByFlightNumber() throws Exception {
//        when(flightServiceImpl.showFlightByNumber("AI101")).thenReturn(flight);
//
////        ResponseEntity<Flight> response = flightController.showFlightDetailsByFlightNumber("AI101");
////        assertNotNull(response.getBody());
////        assertEquals("AI101", response.getBody().getFlightNumber());
//        
////        mockMvc.perform(get("/FMS/getByNumber/AI101")  // ✅ Test using URL
////                .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isNotFound()) ; // ✅ Expect HTTP 202 (ACCEPTED)
//        
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/FMS/getByNumber/AI101")
//        		.contentType(MediaType.APPLICATION_ATOM_XML.APPLICATION_JSON))
//        .andExpect(MockMvcResultMatchers.status().isAccepted())
//        .andExpect(MockMvcResultMatchers.jsonPath("$.flightNumber",Matchers.is("AI101")));
//        
//        verify(flightServiceImpl, times(1)).showFlightByNumber("AI101");
//    }
//	
//	
//	@Test
//    void testUpdateStatusOfFlightByNumber() throws FlightNotFoundException {
//        when(flightServiceImpl.UpdateFlightStausByNumber("AI101", "Delayed")).thenReturn(true);
//
//        ResponseEntity<String> response = flightController.updateStatusOfFlightByNumber("AI101", "Delayed");
//        assertNotNull(response.getBody());
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Flight status updated Delayed", response.getBody());
//        verify(flightServiceImpl, times(1)).UpdateFlightStausByNumber("AI101", "Delayed");
//    }
//	
//	  @Test
//	    void testDeleteFlightByNumber() throws Exception {
//	        when(flightServiceImpl.removeFlightByNumber("AI101")).thenReturn(true);
//
////	        ResponseEntity<String> response = flightController.deleteFlightByNumber("AI101");
////	        assertNotNull(response.getBody());
////	        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
////	        assertEquals("Flight Deleted By Flight NumberAI101", response.getBody());
////	        
//	        
////	        mockMvc.perform(delete("/FMS/deleteByNumber/AI101")
////	                .contentType(MediaType.APPLICATION_JSON)) // Specify content type if needed by your controller
////	                .andExpect(status().isAccepted())
////	                .andExpect(content().string("Flight Deleted By Flight NumberAI101"));
////	        
//	        verify(flightServiceImpl, times(1)).removeFlightByNumber("AI101");
//	    }
	
	
//	@Test
//	public void test() {
//		assertEquals("hello", "hello");
//	}

}

//{
//	  "flightNumber": "AIR-100",
//	  "airline": "Air India",
//	  "departureAirport": "Hyderabad",
//	  "arrivalAirport": "Bengaluru",
//	  "departureTime": "2025-04-02 05:30:00",
//	  "arrivalTime": "2025-04-02 09:15:00",
//	  "status": "Scheduled",
//	  "seats": [
//	    {
//	        "seatId":8,
//	      "seatNumber": 1,
//	      "seatClass": "Business",
//	      "noOfSeats":50,
//	     "availableSeats":50,
//	      "price": 3500.00
//	    },
//	    {
//	        "seatId":9,
//	      "seatNumber": 2,
//	      "seatClass": "Economy",
//	      "noOfSeats":50,
//	      "availableSeats":50,
//	      "price": 1500.00
//	    }
//	  ]
//	}


//
//@PostMapping("/addFlight")
//public ResponseEntity<String> addFlight(@RequestBody @Valid Flight flight) throws FlightHandlingException {
//	
//	if(flightServiceImpl.addFlight(flight)) {
//		return ResponseEntity.status(HttpStatus.CREATED).body("Added Flight Details Successfully");
//	}
//	throw new FlightHandlingException("Flight not added");
//	
//}