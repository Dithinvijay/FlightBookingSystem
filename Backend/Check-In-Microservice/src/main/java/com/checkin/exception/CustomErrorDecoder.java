package com.checkin.exception;

import org.springframework.http.HttpStatus;



import feign.Response;
import feign.codec.ErrorDecoder;
import feign.codec.ErrorDecoder.Default;

public class CustomErrorDecoder  implements ErrorDecoder{

	private final ErrorDecoder defaultErrorDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		        if (response.status() == HttpStatus.NOT_FOUND.value()) {
		            return new BookingsNotFoundException("No Flights with the flightNumber");  // Convert Feign error to your exception
		        }
		        return defaultErrorDecoder.decode(methodKey, response);
	}
	
}
