package com.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
	
	
    private final ErrorDecoder defaultErrorDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		        if (response.status() == HttpStatus.NOT_FOUND.value()) {
//		            return new FlightHandlingException("No Flights Available Currently");  // Convert Feign error to your exception
		        	if (methodKey.contains("BookingFeign")) {  
		        		
		                return new BookingHandlingException("No Booking Found");
		            } else if (methodKey.contains("FlightOpenFeign")) {  // For Flight Service
		                return new FlightNotFoundException("No Flights Available Currently.");
		            }
		        }
		        
		        if (response.status() == HttpStatus.BAD_REQUEST.value()) {
		            return new FlightHandlingException("Invalid request: Please check the input parameters.");  // Handle 400
		        }
		        
		        return defaultErrorDecoder.decode(methodKey, response);
	}

}
