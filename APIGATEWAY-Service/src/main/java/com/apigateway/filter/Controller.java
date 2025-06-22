package com.apigateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.apigateway.service.Openfeign;

@RestController
public class Controller {
	
	@Autowired
	private Openfeign openfeign;
	
	@GetMapping("/validateToken/{token}")
	public String Validate(@PathVariable  String token) {
		return openfeign.validateToken(token);
	}

}
