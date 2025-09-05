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


  showSignUp() { this.rightPanelActive = true; }
  showSignIn() { this.rightPanelActive = false; }

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
    
    this.registerError = '';
    this.http.post(`${environment.apiUrlLogin}register`, {
      firstName: this.regFirstName,
      lastName: this.regLastName,
      email: this.regEmail,
      phoneNo: this.regPhoneNo,
      username: this.regUsername,
      password: this.regPassword,
      role: this.regRole
    }).subscribe({
      next: () => {
        this.showToastNotification('Registration successful! Please log in.');
        setTimeout(() => {
          this.showSignIn();
          this.loginUsername = this.regUsername;
        }, 1000);
      },
      error: (err: any) => this.registerError = err.error?.message || 'Registration failed.'
    });
  }
}
