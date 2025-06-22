package com.userservice.controller;

import java.util.List;

import org.hibernate.metamodel.model.domain.IdentifiableDomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.Model.Role;
import com.userservice.Model.User;
import com.userservice.exception.UserNotFoundException;
import com.userservice.service.JwtService;
import com.userservice.service.UserCrudService;

@RestController
public class UserController {
	
	@Autowired
	private UserCrudService userCrudService;
	

	@Autowired
	private JwtService jwtService;
	
	
	@PostMapping("/register")
	public User register(@RequestBody User user) {
	    System.out.println("User object received in controller: " + user);
		return userCrudService.register(user);
	}
	
	@PostMapping("/login")
	public String login(@RequestBody User user) {
		
		return userCrudService.verify(user);
	}
	
	@GetMapping("/validate/{token}")
	public String validateToken(@PathVariable String token) {
		 return userCrudService.validate(token);
	}
	
//	@PostMapping("/validateToken") // New endpoint for token validation
//    public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
//        try {
//            jwtService.validateToken(token); // This will throw an exception if the token is invalid
//            return ResponseEntity.ok(true);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
//        }
//    }
	
	@GetMapping("/getUser/{id}")
	public User getUser(@PathVariable Long id) throws UserNotFoundException {
	
		User user = userCrudService.getUserById(id);
		if(user == null) {
			throw new UserNotFoundException("User Not found");
		}
		return user;
	}
	
	@GetMapping("/getRoles/{id}")
	public List<Role> getRolesById(@PathVariable Long id) throws UserNotFoundException{
		List<Role> roles = userCrudService.getRoleById(id);
		if(roles == null) {
			throw new UserNotFoundException("User Not Found");
		}
		return roles;
	}
	
	@DeleteMapping("/user/deleteById/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) throws UserNotFoundException{
		int x = userCrudService.deleteUserById(id);
		if(x == 1) {
			return ResponseEntity.ok("Deleted User successfully");
		}
		throw new UserNotFoundException("User not found");
	}

}
