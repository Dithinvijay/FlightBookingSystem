package com.bookingservice.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient( name="PAYMENT-SERVICE")
public interface RazorClient {

	@PostMapping("/create-order")
    ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Integer> requestBody);

    @PostMapping("/capture-payment")
    ResponseEntity<String> capturePayment(@RequestBody Map<String, Object> requestBody);
}
