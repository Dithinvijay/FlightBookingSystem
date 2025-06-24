package com.apigateway.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "User-Service")
public interface Openfeign {
	
	@GetMapping("/validate/{token}")
	String validateToken(@PathVariable String token);

}
