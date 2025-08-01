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

  constructor(private router: Router) {}

  onAnimatedSearchClick() {
    if (this.isActive || this.isLoading) return;
    this.isActive = true;
    this.isLoading = true;
    // Save search params to localStorage for flights dashboard
    const today = new Date();
    const date = today.toISOString().split('T')[0];
    localStorage.setItem('flightSearch', JSON.stringify({
      source: this.fromValue,
      destination: this.toValue,
      date: date
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
