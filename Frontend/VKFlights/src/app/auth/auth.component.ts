import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class AuthComponent {
  loginUsername = '';
  loginPassword = '';
  loginError = '';

  regFirstName = '';
  regLastName = '';
  regEmail = '';
  regPhoneNo = '';
  regUsername = '';
  regPassword = '';
  regRole='USER';
  registerError = '';
  
  // OTP verification
  showOtpVerification = false;
  otpCode = '';
  otpError = '';
  userEmail = '';
  otpLoading = false;
  registerLoading = false;

  rightPanelActive = false;

  showLoginPassword = false;
  toastMessage = '';
  showToast = false;
  
  // Validation errors
  validationErrors: any = {};

  constructor(private router: Router, private http: HttpClient) {}

  onAdminLogin() {
    this.router.navigate(['/admin-login']);
  }


  showSignUp() { 
    this.rightPanelActive = true;
    this.showOtpVerification = false;
  }
  showSignIn() { 
    this.rightPanelActive = false;
    this.showOtpVerification = false;
  }

  showToastNotification(message: string) {
    this.toastMessage = message;
    setTimeout(() => this.showToast = true, 100);
    setTimeout(() => {
      this.showToast = false;
      setTimeout(() => this.toastMessage = '', 500);
    }, 3000);
  }

  onLogin() {
    if (!this.loginUsername || !this.loginPassword) {
      this.loginError = 'Please fill in all fields.';
      return;
    }
    if (this.loginUsername === 'dithinvijay') {
      this.loginError = 'User is not registered.';
      return;
    }
    this.loginError = '';
    this.http.post(`${environment.apiUrlLogin}login`, {
      username: this.loginUsername,
      password: this.loginPassword
    }, { responseType: 'text' }).subscribe({
      next: (token: string) => {
        if (token && token.length > 0) {
          localStorage.setItem('jwt', token);
          this.showToastNotification('Login successful! Welcome back.');
          setTimeout(() => this.router.navigate(['/dashboard']), 1000);
        } else {
          this.loginError = 'Login failed: No token received.';
        }
      },
      error: (err: any) => this.loginError = err.error?.message || 'Login failed.'
    });
  }

  validateSignUpForm(): boolean {
    this.validationErrors = {};
    let isValid = true;

    // Email validation
    if (!this.regEmail.includes('@gmail.com')) {
      this.validationErrors.email = 'Enter valid email';
      isValid = false;
    }

    // Phone validation
    if (this.regPhoneNo.length !== 10 || !/^[6-9]/.test(this.regPhoneNo)) {
      this.validationErrors.phone = 'Phone number must be 10 digits starting with 6-9';
      isValid = false;
    }

    // Username validation
    if (this.regUsername.length < 6) {
      this.validationErrors.username = 'Username must be at least 6 characters';
      isValid = false;
    }

    // Password validation
    const passwordRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/;
    if (!passwordRegex.test(this.regPassword)) {
      this.validationErrors.password = 'length must be 8 including numbers and symbols';
      isValid = false;
    }

    return isValid;
  }

  onRegister() {
    if (!this.regFirstName || !this.regLastName || !this.regEmail || !this.regPhoneNo || !this.regUsername || !this.regPassword) {
      this.registerError = 'Please fill in all fields.';
      return;
    }
    
    if (!this.validateSignUpForm()) {
      return;
    }
    
    if (this.registerLoading) {
      return;
    }
    
    this.registerError = '';
    this.registerLoading = true;
    
    this.http.post(`${environment.apiUrlLogin}register`, {
      firstName: this.regFirstName,
      lastName: this.regLastName,
      email: this.regEmail,
      phoneNo: this.regPhoneNo,
      username: this.regUsername,
      password: this.regPassword,
      role: this.regRole
    }, { responseType: 'text' }).subscribe({
      next: (response: string) => {
        this.registerLoading = false;
        this.userEmail = this.regEmail;
        this.showOtpVerification = true;
        this.showToastNotification('OTP sent to your email. Please verify to complete registration.');
      },
      error: (err: any) => {
        this.registerLoading = false;
        this.registerError = err.error?.message || 'Registration failed.';
      }
    });
  }
  
  onVerifyOtp() {
    if (!this.otpCode || this.otpCode.length !== 6) {
      this.otpError = 'Please enter a valid 6-digit OTP.';
      return;
    }
    
    if (this.otpLoading) {
      return;
    }
    
    this.otpError = '';
    this.otpLoading = true;
    
    this.http.post(`${environment.apiUrlLogin}verify-otp?email=${this.userEmail}&otp=${this.otpCode}`, {}, { responseType: 'text' }).subscribe({
      next: (response: string) => {
        this.otpLoading = false;
        this.showToastNotification('Registration successful! Please log in.');
        this.showOtpVerification = false;
        this.resetRegistrationForm();
        setTimeout(() => {
          this.showSignIn();
          this.loginUsername = this.regUsername;
        }, 1000);
      },
      error: (err: any) => {
        this.otpLoading = false;
        this.otpError = err.error || 'Invalid or expired OTP.';
      }
    });
  }
  
  resetRegistrationForm() {
    this.regFirstName = '';
    this.regLastName = '';
    this.regEmail = '';
    this.regPhoneNo = '';
    this.regUsername = '';
    this.regPassword = '';
    this.otpCode = '';
    this.validationErrors = {};
    this.otpLoading = false;
    this.registerLoading = false;
  }
}
