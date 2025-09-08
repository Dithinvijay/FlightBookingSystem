package com.userservice.service;

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

    public void sendWelcomeEmail(String to, String firstName) {
    	try {
            logger.info("Preparing welcome email to send...");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dithin12398@gmail.com");
            message.setTo(to);
            message.setSubject("✈️ Welcome to VK Flights – Your Account Is Ready!");
            message.setText(buildWelcomeMessage(firstName));

            mailSender.send(message);

            logger.info("Welcome email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send welcome email to {}. Error: {}", to, e.getMessage(), e);
        }
    }

    private String buildWelcomeMessage(String firstName) {
        return String.format(
        				"Dear %s,\n"
        				+ "\n"
        				+ "Welcome aboard!\n"
        				+ "\n"
        				+ "We’re thrilled to have you join the VK Flights family. Your account has been successfully created, and you're now ready to explore a seamless travel experience.\n"
        				+ "\n"
        				+ "As a registered member, you can now:\n"
        				+ "• 🔍 Search and book flights to your favorite destinations\n"
        				+ "• 🧳 Manage your bookings and check-in online\n"
        				+ "• 🎁 Enjoy exclusive deals, discounts, and special offers\n"
        				+ "\n"
        				+ "Should you have any questions or require assistance, our customer support team is here to help.\n"
        				+ "\n"
        				+ "Thank you for choosing VK Flights. We look forward to making your journeys smooth, memorable, and enjoyable.\n"
        				+ "\n"
        				+ "Warm regards,\n"
        				+ "Customer Experience Team\n"
        				+ "VK Flights\n"
        				+ "✈️ Fly Smart. Fly VK.\n"
        				+ "\n"
        				+ "---\n"
        				+ "This is an automated message. Please do not reply to this email.",firstName
        );
    }
}