import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

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
  dateValue = '';
  searchError: string = '';
  todayDateString: string = '';
  
  // Validation error properties
  fromError: string = '';
  toError: string = '';
  dateError: string = '';

  constructor(private router: Router, private http: HttpClient) {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    this.todayDateString = `${yyyy}-${mm}-${dd}`;
  }

  onAnimatedSearchClick() {
    this.searchError = '';
    if (this.isActive || this.isLoading) return;
    
    if (!this.validateForm()) {
      return;
    }
    
    const token = localStorage.getItem('jwt');
    if (!token) {
      this.searchError = 'Please login first to search for flights.';
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 1800);
      return;
    }
    const today = new Date();
    today.setHours(0,0,0,0);
    const userDate = new Date(this.dateValue);
    userDate.setHours(0,0,0,0);
    if (userDate < today) {
      this.searchError = 'Departure date cannot be in the past.';
      return;
    }
    // Ensure date is in yyyy-MM-dd format
    const dateObj = new Date(this.dateValue);
    const yyyy = dateObj.getFullYear();
    const mm = String(dateObj.getMonth() + 1).padStart(2, '0');
    const dd = String(dateObj.getDate()).padStart(2, '0');
    const formattedDate = `${yyyy}-${mm}-${dd}`;
    this.isActive = true;
    this.isLoading = true;
    const url = `http://localhost:8080/FMS/flights/${this.fromValue}/${this.toValue}/${formattedDate}`;
    console.log('Requesting flights from:', url);
    const headers = { headers: { 'Authorization': `Bearer ${token}` } };
    this.http.get<any[]>(url, headers).subscribe({
      next: (flights) => {
        console.log('Flights received:', flights);
        localStorage.setItem('flightSearchResults', JSON.stringify(flights));
        localStorage.setItem('flightSearch', JSON.stringify({
          source: this.fromValue,
          destination: this.toValue,
          date: formattedDate
        }));
        this.isActive = false;
        this.isFinished = true;
        this.isLoading = false;
        setTimeout(() => {
          this.router.navigate(['/flights-dashboard']);
        }, 1800);
      },
      error: (err) => {
        this.isActive = false;
        this.isLoading = false;
        this.searchError = ('No flights found for your search.');
        console.error('Flight search error:', err);
      }
    });
  }

  swapFromTo() {
    const temp = this.fromValue;
    this.fromValue = this.toValue;
    this.toValue = temp;
    this.clearErrors();
  }
  
  validateForm(): boolean {
    let isValid = true;
    
    // Validate From field
    if (!this.fromValue.trim()) {
      this.fromError = 'Departure city is required';
      isValid = false;
    } else if (this.fromValue.trim().length < 2) {
      this.fromError = 'City name must be at least 2 characters';
      isValid = false;
    }
    
    // Validate To field
    if (!this.toValue.trim()) {
      this.toError = 'Destination city is required';
      isValid = false;
    } else if (this.toValue.trim().length < 2) {
      this.toError = 'City name must be at least 2 characters';
      isValid = false;
    } else if (this.fromValue.trim().toLowerCase() === this.toValue.trim().toLowerCase()) {
      this.toError = 'Destination must be different from departure city';
      isValid = false;
    }
    
    // Validate Date field
    if (!this.dateValue) {
      this.dateError = 'Departure date is required';
      isValid = false;
    } else {
      const today = new Date();
      today.setHours(0,0,0,0);
      const userDate = new Date(this.dateValue);
      userDate.setHours(0,0,0,0);
      if (userDate < today) {
        this.dateError = 'Departure date cannot be in the past';
        isValid = false;
      }
    }
    
    return isValid;
  }
  
  clearErrors() {
    this.fromError = '';
    this.toError = '';
    this.dateError = '';
  }
  
  onFieldChange() {
    this.clearErrors();
  }
}