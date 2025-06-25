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
  // Login fields
  loginUsername = '';
  loginPassword = '';
  loginError = '';

  // Register fields
  regFirstName = '';
  regLastName = '';
  regEmail = '';
  regPhoneNo = '';
  regUsername = '';
  regPassword = '';
  registerError = '';

  // UI state
  rightPanelActive = false;

  constructor(private router: Router, private http: HttpClient) {}

  // Toggle panels
  showSignUp() { this.rightPanelActive = true; }
  showSignIn() { this.rightPanelActive = false; }

  // Login logic
  onLogin() {
    if (!this.loginUsername || !this.loginPassword) {
      this.loginError = 'Please fill in all fields.';
      return;
    }
    this.loginError = '';
    this.http.post(`${environment.apiUrl}/login`, {
      username: this.loginUsername,
      password: this.loginPassword
    }, { responseType: 'text' }).subscribe({
      next: (token: string) => {
        if (token && token.length > 0) {
          localStorage.setItem('jwt', token);
          this.router.navigate(['/dashboard']);
        } else {
          this.loginError = 'Login failed: No token received.';
        }
      },
      error: (err: any) => this.loginError = err.error?.message || 'Login failed.'
    });
  }

  // Register logic
  onRegister() {
    if (!this.regFirstName || !this.regLastName || !this.regEmail || !this.regPhoneNo || !this.regUsername || !this.regPassword) {
      this.registerError = 'Please fill in all fields.';
      return;
    }
    this.registerError = '';
    this.http.post(`${environment.apiUrl}/register`, {
      firstName: this.regFirstName,
      lastName: this.regLastName,
      email: this.regEmail,
      phoneNo: this.regPhoneNo,
      username: this.regUsername,
      password: this.regPassword
    }).subscribe({
      next: () => {
        this.showSignIn();
        this.loginUsername = this.regUsername;
        this.loginError = 'Registration successful! Please log in.';
      },
      error: (err: any) => this.registerError = err.error?.message || 'Registration failed.'
    });
  }
}
