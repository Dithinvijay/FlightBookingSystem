package com.bookingservice.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.bookingservice.Exception.BookingHandlingException;
import com.bookingservice.Exception.BookingsNotFoundException;
import com.bookingservice.controller.BookingController;
import com.bookingservice.model.Booking;
import com.bookingservice.model.PassengerDetails;
import com.bookingservice.service.BookingService;
import com.bookingservice.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
@WebMvcTest(BookingController.class)
public class BookingControllerTest {
	

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
   

    
    @MockBean	
	private BookingService bookingService;
    @MockBean
    private EmailService emailService;
	
//	@InjectMocks
//	private BookingController bookingController;
//	
	private Booking booking;
	private PassengerDetails passenger;
	
	@BeforeEach
	void setUp() {
		PassengerDetails passenger1 = new PassengerDetails("Pavan", "Male", 22, null);
        PassengerDetails passenger2 = new PassengerDetails("Sai Gopal", "Female", 22, null);
        List<PassengerDetails> passengers = Arrays.asList(passenger1, passenger2);
        
        booking = new Booking("AIR-100", 1, 2, LocalDateTime.now(), "Scheduled", passengers);
	}
	
	@Test
	void testAddBooking() throws JsonProcessingException, Exception {
	    // Arrange: Mock the service method to return 1 (indicating a successful booking)
	    when(bookingService.addBooking(
	            Mockito.eq("AIR-100"),
	            Mockito.eq("Economy"),
	            Mockito.eq(50),
	            Mockito.any(Booking.class) // Use Mockito.any(Booking.class)
	    )).thenReturn(1);

	    // Arrange: Mock calculateTotalPrice to return a sample price
	    when(bookingService.calculateTotalPrice(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
	            .thenReturn(150.0); // Sample price

	    // Act: Call the controller method
	    mockMvc.perform(MockMvcRequestBuilders.post("/BMS/bookTickets/AIR-100/Economy/50")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(booking)))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(content().string("Order created successfully. Please pay amount 150.0 Booking Successfully"));

	    // Verify that the service method was called exactly once with the expected arguments
	    verify(bookingService, times(1)).addBooking(
	            Mockito.eq("AIR-100"),
	            Mockito.eq("Economy"),
	            Mockito.eq(50),
	            Mockito.any(Booking.class) // Use Mockito.any(Booking.class) here too for verification
	    );
	}

	@Test
    void testDeleteBookingById() throws Exception {
        // Arrange: Mock the service method to return 1 (indicating successful deletion)
        int bookingIdToDelete = 2;
        when(bookingService.cancelBookingByBookingId(bookingIdToDelete)).thenReturn(1);

        // Act: Call the controller method via MockMvc
        mockMvc.perform(MockMvcRequestBuilders.delete("/BMS/deleteBookingById/{bookingId}", bookingIdToDelete)
                .contentType(MediaType.APPLICATION_JSON)) // You might not need ContentType for DELETE
                .andExpect(status().isAccepted()) // Assuming your controller returns 200 OK on success
                .andExpect(content().string("Deleted"));

        // Assert: Verify that the service method was called exactly once with the expected argument
        verify(bookingService, times(1)).cancelBookingByBookingId(bookingIdToDelete);
    }

	
	@Test
    void testDeleteBookingByIdWhenFailure() throws Exception {
        // Arrange: Mock the service method to return 1 (indicating successful deletion)
        int bookingIdToDelete = 1;
        when(bookingService.cancelBookingByBookingId(bookingIdToDelete)).thenReturn(0);

        // Act: Call the controller method via MockMvc
        mockMvc.perform(MockMvcRequestBuilders.delete("/BMS/deleteBookingById/{bookingId}", bookingIdToDelete)
                .contentType(MediaType.APPLICATION_JSON)) // You might not need ContentType for DELETE
                .andExpect(status().isBadRequest()) // Assuming your controller returns 200 OK on success
                .andExpect(content().string("Booking Id is not available "));

        // Assert: Verify that the service method was called exactly once with the expected argument
        verify(bookingService, times(1)).cancelBookingByBookingId(bookingIdToDelete);
    }
	
	
	@Test
    void testGetBookingByPassengerId_Success() throws Exception {
        // Arrange
        int passengerBookingId = 12106941;
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingService.getBookingByPassengerBookingId(passengerBookingId)).thenReturn(bookings);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(MockMvcRequestBuilders.get("/BMS/bookingsOfPassenger/" + passengerBookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].flightNumber", is("AIR-100"))); // Add more assertions as needed

        // Verify that the service method was called
        verify(bookingService, times(1)).getBookingByPassengerBookingId(passengerBookingId);
    }
	
	
	@Test
    void testGetBookingBBookingId_Success() throws Exception {
        // Arrange
        int BookingId = 1;
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingService.getBookingsById(BookingId)).thenReturn(bookings);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(MockMvcRequestBuilders.get("/BMS/getByBookingId/" + BookingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].flightNumber", is("AIR-100"))); // Add more assertions as needed

        // Verify that the service method was called
        verify(bookingService, times(1)).getBookingsById(BookingId);
    }
	
	
	
	@Test
    void testGetBookingByFlightNumber_Success() throws Exception {
        // Arrange
        int BookingId = 1;
        String flightNumber = "AIR-100";
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingService.getBookingByFlightNumber(flightNumber)).thenReturn(bookings);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(MockMvcRequestBuilders.get("/BMS/bookings/" + flightNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].flightNumber", is("AIR-100"))); // Add more assertions as needed

        // Verify that the service method was called
        verify(bookingService, times(1)).getBookingByFlightNumber(flightNumber);
    }
	
	@Test
    void testShowAvailableSeatsEndpoint_NoSeatsAvailable() throws Exception {
        // Arrange
        String flightNumber = "AIR-100";
        
        HashMap<String, String> availableSeats = new HashMap<>();
        availableSeats.put("Economy", "50");
        availableSeats.put("Business", "10");
        

        when(bookingService.showAvailableSeats(flightNumber)).thenReturn(availableSeats);

        // Act & Assert using MockMvc for URL handling
        mockMvc.perform(MockMvcRequestBuilders.get("/BMS/showSeats/" + flightNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Economy", is("50")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Business", is("10")));

        // Verify that the service method was called
        verify(bookingService, times(1)).showAvailableSeats(flightNumber);
    }

	
	 @Test
	    void testDeleteBookingByIdEndpoint_Success() throws Exception {
	        // Arrange
	        int bookingIdToDelete = 101;
	        when(bookingService.cancelBookingByBookingId(bookingIdToDelete)).thenReturn(1);

	        // Act & Assert using MockMvc for URL handling
	        mockMvc.perform(MockMvcRequestBuilders.delete("/BMS/deleteBookingById/" + bookingIdToDelete)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andExpect(status().isAccepted()) // Expecting 202 Accepted
	                .andExpect(content().string("Deleted"));

	        // Verify that the service method was called
	        verify(bookingService, times(1)).cancelBookingByBookingId(bookingIdToDelete);
	    }

	 
	 @Test
	    void testDeletePassengerByBookingIdEndpoint_Success() throws Exception {
	        // Arrange
	        int bookingId = 201;
	        int passengerIdToDelete = 301;
	        when(bookingService.deletePassengerIdByBookingId(bookingId, passengerIdToDelete)).thenReturn(1);

	        // Act & Assert using MockMvc for URL handling
	        mockMvc.perform(MockMvcRequestBuilders.delete("/BMS/deletePassenger/" + bookingId + "/" + passengerIdToDelete)
	                .contentType(MediaType.APPLICATION_JSON))
	                .andExpect(status().isOk())
	                .andExpect(content().string("Deleted Passenger Successfully"));

	        // Verify that the service method was called
	        verify(bookingService, times(1)).deletePassengerIdByBookingId(bookingId, passengerIdToDelete);
	    }
	
//	@Test
//	void testgetBookingByPassengerId() throws BookingsNotFoundException {
//		when(bookingService.getBookingByPassengerBookingId(12106941)).thenReturn(Arrays.asList(booking));
//		
//		ResponseEntity<List<Booking>> bookings = bookingController.getBookings(12106941);
//	    assertEquals(HttpStatus.OK, bookings.getStatusCode()); // Verify status is OK
//		assertEquals(bookings, bookings);
//		verify(bookingService, times(1)).getBookingByPassengerBookingId(12106941);
//	}
	
	

}




//@Test
//void testAddBooking() throws JsonProcessingException, Exception {
//    // Arrange: Mock the service method to return 1 (indicating a successful booking)
//	when(bookingService.addBooking(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(Booking.class)))
//    .thenReturn(1);
//
////Arrange: Mock calculateTotalPrice to return a sample price
//when(bookingService.calculateTotalPrice(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
//    .thenReturn(150.0); // Sample price
//
//    // Act: Call the controller method
//    mockMvc.perform(MockMvcRequestBuilders.post("/BMS/bookTickets/AIR-100/Economy/50")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(booking))).
//    andExpect(MockMvcResultMatchers.status().isOk()) // Expecting 200 (OK)
//    .andExpect(content().string("Please pay 150.0 for confirming the booking")); // Corrected expected response body
//
////    ResponseEntity<String> response = bookingController.addBooking("AIR-100", "Economy", 50, booking);
////
////    // Assert: Verify response body and status code
////    assertEquals(HttpStatus.CREATED, response.getStatusCode());  // Check if response status is 200 OK
////    assertNotNull(response.getBody());                      // Ensure the response body is not null
////    assertEquals("Booking Succesful", response.getBody());  // Verify correct success message
//
//    // Verify that the service method was called exactly once with the expected arguments
//    verify(bookingService, times(1)).addBooking("AIR-100", "Economy", 50, booking);
//}
//
//@Test
//void testDeleteBooking() throws BookingHandlingException {
//    // Arrange: Mock the service method to return 1 (indicating successful deletion)
//    when(bookingService.deleteBookingByBookingId(2)).thenReturn(1);
//
//    // Act: Call the controller method
// //   ResponseEntity<String> response = bookingController.deleteBooking(2);
//
//    // Assert: Verify response body
//  //  assertEquals("Deleted", response.getBody());
//    
//    // Verify that the service method was called exactly once with the expected argument
//    verify(bookingService, times(1)).deleteBookingByBookingId(2);
//}

