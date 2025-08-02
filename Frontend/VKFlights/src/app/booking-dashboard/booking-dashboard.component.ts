import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { environment } from '../../environments/environment';

import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-booking-dashboard',
  templateUrl: './booking-dashboard.component.html',
  styleUrls: ['./booking-dashboard.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class BookingDashboardComponent implements OnInit {
  isLoggedIn: boolean = false;
  airlineLogoMap: { [key: string]: string } = {
    'indigo': 'assets/indigologo.png',
    'airindia': 'assets/airindialogo.png',
    'vistara': 'assets/vistaralogo.png',
    'spicejet': 'https://upload.wikimedia.org/wikipedia/commons/2/2d/SpiceJet_logo.svg',
    'goair': 'https://upload.wikimedia.org/wikipedia/commons/2/2a/Go_First_logo.svg',
    'airasia': 'https://upload.wikimedia.org/wikipedia/commons/6/6e/AirAsia_New_Logo.svg',
    'akasa': 'https://upload.wikimedia.org/wikipedia/commons/2/2d/Akasa_Air_logo.svg',
    'allianceair': 'https://upload.wikimedia.org/wikipedia/commons/2/2d/Alliance_Air_logo.svg',
    'default': 'https://upload.wikimedia.org/wikipedia/commons/6/65/No-Image-Placeholder.svg'
  };

  airlineColorMap: { [key: string]: string } = {
    'indigo': '#3A5BA0',
    'airindia': '#D9232E',
    'spicejet': '#E03C31',
    'vistara': '#5B2C6F',
    'goair': '#1A237E',
    'airasia': '#D91C1F',
    'akasa': '#FF8200',
    'allianceair': '#D9232E',
    'default': '#1976d2'
  };

  getAirlineLogo(airline: string): string {
    if (!airline) return this.airlineLogoMap['default'];
    const key = airline.replace(/\s+/g, '').toLowerCase();
    return this.airlineLogoMap[key] || this.airlineLogoMap['default'];
  }

  getAirlineColor(airline: string): string {
    if (!airline) return this.airlineColorMap['default'];
    const key = airline.replace(/\s+/g, '').toLowerCase();
    return this.airlineColorMap[key] || this.airlineColorMap['default'];
  }

  flight: any;
  bookingForm: FormGroup;
  bookingStatus: string = '';
  error: string = '';
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder
  ) {
    this.bookingForm = this.fb.group({
      seatClass: ['', Validators.required],
      noOfSeats: [1, [Validators.required, Validators.min(1)]],
      email: ['', [Validators.required, Validators.email]],
      passengerBookingId: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      passengers: this.fb.array([])
    });
  }

  ngOnInit(): void {
    // Check authentication
    this.isLoggedIn = !!localStorage.getItem('jwt');
    if (!this.isLoggedIn) {
      this.router.navigate(['/login']);
      return;
    }
    // Get flight from navigation state or localStorage
    const nav = this.router.getCurrentNavigation();
    this.flight = nav?.extras?.state?.['flight'];
    if (!this.flight) {
      const stored = localStorage.getItem('selectedFlight');
      if (stored) {
        this.flight = JSON.parse(stored);
      } else {
        this.error = 'No flight selected.';
        return;
      }
    }
    this.bookingForm.get('noOfSeats')?.valueChanges.subscribe(() => this.updatePassengers());
    this.updatePassengers();
  }

  get passengers() {
    return this.bookingForm.get('passengers') as FormArray;
  }

  updatePassengers() {
    const noOfSeats = this.bookingForm.get('noOfSeats')?.value || 1;
    while (this.passengers.length < noOfSeats) {
      this.passengers.push(this.fb.group({
        passengerName: ['', Validators.required],
        gender: ['', Validators.required],
        age: ['', [Validators.required, Validators.min(1)]]
      }));
    }
    while (this.passengers.length > noOfSeats) {
      this.passengers.removeAt(this.passengers.length - 1);
    }
  }

  submitBooking() {
    if (this.bookingForm.invalid) return;
    const { seatClass, noOfSeats, email, passengerBookingId, passengers } = this.bookingForm.value;
    const booking = {
      email,
      passengerBookingId,
      passengers
    };
    const token = localStorage.getItem('jwt');
    const headers = token ? { headers: { 'Authorization': `Bearer ${token}` } } : {};
    this.http.post(`${environment.apiUrlLogin}BMS/bookTickets/${this.flight.flightNumber}/${seatClass}/${noOfSeats}`, booking, { ...headers, responseType: 'text' })
      .subscribe({
        next: (res) => {
          this.bookingStatus = 'Booking successful!';
          setTimeout(() => this.router.navigate(['/']), 2000);
        },
        error: (err) => {
          this.error = 'Booking failed. Please try again.';
        }
      });
  }
}
