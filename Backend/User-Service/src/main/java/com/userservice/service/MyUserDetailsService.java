package com.userservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.userservice.Model.User;
import com.userservice.Model.UserPrinciple;
import com.userservice.Repo.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user with username: {}", username);
        User user = userRepository.findByUsername(username);

        if (user == null) {
            logger.warn("User with username '{}' not found", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        logger.info("User with username '{}' found successfully", username);
        return new UserPrinciple(user);
    }
}
