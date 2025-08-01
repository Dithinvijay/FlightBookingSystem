
import { Component, OnInit } from '@angular/core';
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

  constructor(private http: HttpClient) {}

  ngOnInit() {
    const token = localStorage.getItem('jwt');
    if (!token) {
      this.error = 'No authentication token found. Please log in.';
      return;
    }
    // Decode JWT to get user id (payload is base64 encoded)
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
    // Fetch user by id
    this.http.get<User>(`${environment.apiUrlLogin}getUser/${userId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (user) => this.user = user,
      error: (err) => this.error = err.error?.message || 'Failed to fetch user data.'
    });
  }

  onUpdate() {
    if (!this.user) return;
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
      // Optionally call backend to invalidate session/token (if supported)
      this.http.post(`${environment.apiUrlLogin}logout`, {}, {
        headers: { Authorization: `Bearer ${token}` },
        responseType: 'text'
      }).subscribe({
        next: () => {},
        error: () => {}
      });
    }
    localStorage.removeItem('jwt');
    window.location.href = '/login';
  }
// removed extra closing brace
}
