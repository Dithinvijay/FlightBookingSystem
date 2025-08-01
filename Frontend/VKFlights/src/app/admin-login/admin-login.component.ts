import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';

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

  constructor(private router: Router, private http: HttpClient) {}

  onAdminLogin() {
    if (!this.adminUsername || !this.adminPassword) {
      this.adminLoginError = 'Please fill in all fields.';
      return;
    }
    if (this.adminUsername !== 'dithinvijay') {
      this.adminLoginError = 'Only admin credentials will work.';
      return;
    }
    this.adminLoginError = '';
    this.http.post(`${environment.apiUrlLogin}login`, {
      username: this.adminUsername,
      password: this.adminPassword
    }, { responseType: 'text' }).subscribe({
      next: (token: string) => {
        if (token && token.length > 0) {
          localStorage.setItem('jwt', token);
          this.router.navigate(['/admin-dashboard']);
        } else {
          this.adminLoginError = 'Login failed: No token received.';
        }
      },
      error: (err: any) => this.adminLoginError = err.error?.message || 'Login failed.'
    });
  }
}
