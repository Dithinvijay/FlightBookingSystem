package com.userservice.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.userservice.Model.OtpData;
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
    
    @Autowired
    private EmailService emailService;
	


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private static Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private static final String OTP_FILE = "otp_storage.dat";
    private Random random = new Random();

    public String sendOtp(User user) {
        logger.info("Sending OTP for user registration: {}", user.getUsername());
        
        String otp = String.format("%06d", random.nextInt(1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);
        
        otpStorage.put(user.getEmail(), new OtpData(otp, user, expiryTime));
        saveOtpStorage();
        
        emailService.sendOtpEmail(user.getEmail(), user.getFirstName(), otp);
        logger.info("OTP sent successfully to: {}", user.getEmail());
        
        return "OTP sent to your email";
    }
    
    @Transactional
    public User verifyOtpAndRegister(String email, String otp) {
        logger.info("Verifying OTP for email: {}", email);
        loadOtpStorage();
        
        OtpData otpData = otpStorage.get(email);
        if (otpData == null) {
            logger.warn("No OTP found for email: {}", email);
            return null;
        }
        
        if (otpData.isExpired()) {
            otpStorage.remove(email);
            saveOtpStorage();
            logger.warn("OTP expired for email: {}", email);
            return null;
        }
        
        if (!otpData.getOtp().equals(otp)) {
            logger.warn("Invalid OTP for email: {}", email);
            return null;
        }
        
        User user = otpData.getUser();
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        otpStorage.remove(email);
        saveOtpStorage();
        
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
        logger.info("User registered successfully with ID: {}", savedUser.getId());
        
        return savedUser;
    }
    

    public User getUserById(String username) {
        logger.info("Fetching user by ID: {}", username);
        User userOptional = userRepository.findByUsername(username);
        User user = userOptional;
        if (user != null) {
            logger.info("User found with ID: {}", username);
        } else {
            logger.warn("User not found with ID: {}", username);
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
    
    @Transactional
    public int deleteUserById(Long id) {
    	
    	logger.info("Deleting user by id {}", id);
    	User user = userRepository.findById(id).orElse(null);
    	if(user != null) {
    		userRepository.deleteById(id);
    		logger.info("Deleted User successfully");
    		return 1;
    	}
    	logger.warn("User not found with id {}", id);
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
    
    private void saveOtpStorage() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(OTP_FILE))) {
            oos.writeObject(otpStorage);
        } catch (Exception e) {
            logger.error("Error saving OTP storage: {}", e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadOtpStorage() {
        try {
            File file = new File(OTP_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    otpStorage = (Map<String, OtpData>) ois.readObject();
                }
            }
        } catch (Exception e) {
            logger.error("Error loading OTP storage: {}", e.getMessage());
        }
    }
    
    

    

}