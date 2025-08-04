import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

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

  constructor(private router: Router) {
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    this.todayDateString = `${yyyy}-${mm}-${dd}`;
  }

  onAnimatedSearchClick() {
    this.searchError = '';
    if (this.isActive || this.isLoading) return;
    // Check login
    const token = localStorage.getItem('jwt');
    if (!token) {
      this.searchError = 'Please login first to search for flights.';
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 1800);
      return;
    }
    // Validate fields
    if (!this.fromValue || !this.toValue || !this.dateValue) {
      this.searchError = 'Please enter source, destination, and date.';
      return;
    }
    // Validate date is not less than today
    const today = new Date();
    today.setHours(0,0,0,0);
    const userDate = new Date(this.dateValue);
    userDate.setHours(0,0,0,0);
    if (userDate < today) {
      this.searchError = 'Departure date cannot be in the past.';
      return;
    }
    this.isActive = true;
    this.isLoading = true;
    localStorage.setItem('flightSearch', JSON.stringify({
      source: this.fromValue,
      destination: this.toValue,
      date: this.dateValue
    }));
    setTimeout(() => {
      this.isActive = false;
      this.isFinished = true;
      this.isLoading = false;
      setTimeout(() => {
        this.router.navigate(['/flights-dashboard']);
      }, 800); // Show checkmark before redirect
    }, 1800); // Animation duration
  }

  swapFromTo() {
    const temp = this.fromValue;
    this.fromValue = this.toValue;
    this.toValue = temp;
  }
}
