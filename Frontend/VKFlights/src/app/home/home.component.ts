import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  imports: [NgIf, FormsModule]
})
export class HomeComponent {
  isActive = false;
  isFinished = false;
  isLoading = false;

  fromValue = '';
  toValue = '';
  searchResults: any[] = [];
  searchError: string = '';
  searchPerformed = false;

  constructor(private router: Router, private http: HttpClient) {}

  onAnimatedSearchClick() {
    if (this.isActive || this.isLoading) return;
    this.isActive = true;
    this.isLoading = true;
    this.searchError = '';
    this.searchResults = [];
    this.searchPerformed = false;
    // Save search params to localStorage for flights dashboard
    const today = new Date();
    const date = today.toISOString().split('T')[0];
    localStorage.setItem('flightSearch', JSON.stringify({
      source: this.fromValue,
      destination: this.toValue,
      date: date
    }));
    // Fetch search results from backend using user input for source and destination
    const source = this.fromValue.trim();
    const destination = this.toValue.trim();
    if (!source || !destination || !date) {
      this.searchError = 'Please enter source, destination and date.';
      this.isActive = false;
      this.isLoading = false;
      return;
    }
    // Add Authorization header if token exists
    const token = localStorage.getItem('jwt');
    const headers = token ? { headers: { 'Authorization': `Bearer ${token}` } } : {};
    this.http.get<any[]>(`${environment.apiUrlLogin}FMS/flights/${source}/${destination}`, headers
    ).subscribe({
      next: (flights) => {
        this.searchResults = flights;
        this.isActive = false;
        this.isFinished = true;
        this.isLoading = false;
        this.searchPerformed = true;
        if (flights && flights.length > 0) {
          // Navigate to flights dashboard to show results
          this.router.navigate(['/flights-dashboard']);
        }
      },
      error: (err) => {
        this.searchError = err.error?.message || 'Failed to fetch flights.';
        this.isActive = false;
        this.isFinished = false;
        this.isLoading = false;
        this.searchPerformed = true;
      }
    });
  }

  swapFromTo() {
    const temp = this.fromValue;
    this.fromValue = this.toValue;
    this.toValue = temp;
  }
}
