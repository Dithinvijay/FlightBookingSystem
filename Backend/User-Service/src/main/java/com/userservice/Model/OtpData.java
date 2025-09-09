package com.userservice.Model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OtpData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String otp;
    private User user;
    private LocalDateTime expiryTime;
    
    public OtpData(String otp, User user, LocalDateTime expiryTime) {
        this.otp = otp;
        this.user = user;
        this.expiryTime = expiryTime;
    }
    
    public String getOtp() {
        return otp;
    }
    
    public User getUser() {
        return user;
    }
    
    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}