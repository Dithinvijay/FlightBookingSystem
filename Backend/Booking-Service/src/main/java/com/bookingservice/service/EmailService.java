package com.bookingservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
    	
    	 try {
             logger.info("Preparing email to send...");

             SimpleMailMessage message = new SimpleMailMessage();
             message.setFrom("dithin12398@gmail.com");
             message.setTo(to);
             message.setSubject(subject);
             message.setText(body);

             mailSender.send(message);

             logger.info("Email sent successfully to {}", to);
         } catch (Exception e) {
             logger.error("Failed to send email to {}. Error: {}", to, e.getMessage(), e);
         }
    }

}
