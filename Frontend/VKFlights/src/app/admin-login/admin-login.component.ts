import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class AdminLoginComponent {
  adminUsername = '';
  adminPassword = '';
  adminLoginError = '';
  showAdminPassword = false;

  constructor(private router: Router) {}

  onAdminLogin() {
    if (!this.adminUsername || !this.adminPassword) {
      this.adminLoginError = 'Please fill in all fields.';
      return;
    }
    this.adminLoginError = '';
    // TODO: Replace with real admin login API call
    if (this.adminUsername === 'admin' && this.adminPassword === 'admin') {
      // Simulate successful login
      this.router.navigate(['/dashboard']);
    } else {
      this.adminLoginError = 'Invalid admin credentials.';
    }
  }
}
