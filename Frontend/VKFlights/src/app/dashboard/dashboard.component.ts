
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNo: string;
  username: string;
  roles?: string[];
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class DashboardComponent implements OnInit {
  user: User | null = null;
  error: string = '';
  updating = false;
  updateSuccess = false;
  updateError = '';
  
  // Toast notification properties
  toastMessage = '';
  showToast = false;
  
  // Form change tracking
  originalUser: User | null = null;
  hasFormChanges = false;
  
  // Validation errors
  validationErrors: any = {};

  constructor(private http: HttpClient, private router: Router) {}

  showToastNotification(message: string) {
    this.toastMessage = message;
    setTimeout(() => this.showToast = true, 100);
    setTimeout(() => {
      this.showToast = false;
      setTimeout(() => this.toastMessage = '', 300);
    }, 3000);
  }

  checkFormChanges() {
    if (!this.user || !this.originalUser) {
      this.hasFormChanges = false;
      return;
    }
    this.hasFormChanges = 
      this.user.email !== this.originalUser.email ||
      this.user.phoneNo !== this.originalUser.phoneNo ||
      this.user.firstName !== this.originalUser.firstName ||
      this.user.lastName !== this.originalUser.lastName;
  }

  onFieldChange() {
    this.checkFormChanges();
    this.validateForm();
  }

  checkNameLength(event: any, fieldName: string): void {
    const value = event.target.value;
    if (value.length > 8) {
      const fieldLabel = fieldName === 'firstName' ? 'First name' : 'Last name';
      alert(`${fieldLabel} must be maximum 8 characters!`);
    }
  }
  
  validateForm(): boolean {
    this.validationErrors = {};
    let isValid = true;
    
    if (!this.user) return false;

    // Email validation
    if (!this.user.email.includes('@gmail.com')) {
      this.validationErrors.email = 'Enter valid email';
      isValid = false;
    }

    // Phone validation
    if (this.user.phoneNo.length !== 10 || !/^[6-9]/.test(this.user.phoneNo)) {
      this.validationErrors.phone = 'Phone number must be 10 digits starting with 6-9';
      isValid = false;
    }

    // First name validation
    if (this.user.firstName.length > 8) {
      this.validationErrors.firstName = 'First name must be maximum 8 characters';
      isValid = false;
    }

    // Last name validation
    if (this.user.lastName.length > 8) {
      this.validationErrors.lastName = 'Last name must be maximum 8 characters';
      isValid = false;
    }

    return isValid;
  }
  goToCheckin() {
    this.router.navigate(['/checkin']);
  }

  ngOnInit() {
    const token = localStorage.getItem('jwt');
    if (!token) {
      this.error = 'No authentication token found. Please log in.';
      return;
    }
    let userId = '';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      userId = payload.id || payload.userId || payload.sub || '';
    } catch (e) {
      this.error = 'Invalid token.';
      return;
    }
    if (!userId) {
      this.error = 'Could not extract user id from token.';
      return;
    }
    this.http.get<User>(`${environment.apiUrlLogin}getUser/${userId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (user) => {
        this.user = user;
        this.originalUser = { ...user };
        this.checkFormChanges();
      },
      error: (err) => this.error = err.error?.message || 'Failed to fetch user data.'
    });
  }

  onUpdate() {
    if (!this.user) return;
    
    if (!this.validateForm()) {
      this.showToastNotification('Please fix validation errors before updating.');
      return;
    }
    
    this.updating = true;
    this.updateSuccess = false;
    this.updateError = '';
    const token = localStorage.getItem('jwt');
    this.http.put(`${environment.apiUrlLogin}updateUser/${this.user.id}`, this.user, {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: () => {
        this.updating = false;
        this.updateSuccess = true;
        this.originalUser = { ...this.user! };
        this.hasFormChanges = false;
        this.showToastNotification('Profile updated successfully!');
        setTimeout(() => this.updateSuccess = false, 2000);
      },
      error: (err) => {
        this.updating = false;
        this.updateError = err.error?.message || 'Failed to update user.';
      }
    });
  }

  logout() {
    const token = localStorage.getItem('jwt');
    if (token) {
      this.http.post(`${environment.apiUrlLogin}logout`, {}, {
        headers: { Authorization: `Bearer ${token}` },
        responseType: 'text'
      }).subscribe({
        next: () => {},
        error: () => {}
      });
    }
    localStorage.removeItem('jwt');
    this.showToastNotification('Logged out successfully!');
    setTimeout(() => window.location.href = '/login', 500);
  }
}
