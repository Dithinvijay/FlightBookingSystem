package com.userservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.userservice.Model.Role;
import com.userservice.Model.User;
import com.userservice.Repo.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserCrudService {

    private static final Logger logger = LoggerFactory.getLogger(UserCrudService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private JwtService jwtService;
    
    @Autowired
	AuthenticationManager authManager;
	


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public User register(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public User getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElse(null);
        if (user != null) {
            logger.info("User found with ID: {}", id);
        } else {
            logger.warn("User not found with ID: {}", id);
        }
        return user;
    }

    public List<Role> getRoleById(Long id) {
        logger.info("Fetching roles for user with ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            logger.warn("User not found with ID: {}", id);
            return null;
        }
        User user = userOptional.get();
        List<Role> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roles.add(role);
        }
        logger.info(" {} Roles found for user with ID {}",roles.size(), id);
        return roles;
    }
    
    
    public List<User> getAllUsers() {
        logger.info("Fetching All Users");
        List<User> list = userRepository.findAll();
        if(list.size() != 0) {
            logger.info("Fetched {}  users from database", list.size());
    }
        return list;
    }
    
    public int deleteUserById(Long id) {
    	
    	logger.info("Deleting user by id {}", id);
    	User user = userRepository.findById(id).orElse(null);
    	if(user != null) {
    		userRepository.deleteById(id);
    		logger.info("Deleted User successfully");
    		return 1;
    	}
    	return -1;
    	
    }
    
//    public String generateToken(String username) {
//    	return jwtService.generateToken(username);
//    }
    
    
    
    public String verify(User user) {
		// TODO Auto-generated method stub
		Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(user.getUsername());
		}
		return "fail";
	}
    
    
    public String validate(String token) {
    	if(jwtService.validateToken(token))
    		return "token is valid";
    	return "token is either invalid or expired, Kindly login again";
    }
    
    

    

}