import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';


interface Seat {
  seatClass: string;
  noOfSeats: number;
  availableSeats: number;
  price: number;
}

interface Flight {
  id?: number;
  flightNumber: string;
  airline: string;
  departureAirport: string;
  arrivalAirport: string;
  departureTime: string;
  status: string;
  seats: Seat[];
}

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class AdminDashboardComponent implements OnInit {
  editingFlight: Flight | null = null;
  adminUsername = 'dithinvijay';
  flights: Flight[] = [];
  showAddFlight = false;
  newFlight: Flight = this.getEmptyFlight();

  addSeatRow() {
    this.newFlight.seats.push({ seatClass: '', noOfSeats: undefined as any, availableSeats: undefined as any, price: undefined as any });
  }

  removeSeatRow(index: number) {
    if (this.newFlight.seats.length > 1) {
      this.newFlight.seats.splice(index, 1);
    }
  }

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    // Maintain session for admin login
    const token = localStorage.getItem('jwt');
    if (!token) {
      this.router.navigate(['/admin-login']);
      return;
    }
    this.fetchFlights();
  }

  getEmptyFlight(): Flight {
    return {
      flightNumber: '',
      airline: '',
      departureAirport: '',
      arrivalAirport: '',
      departureTime: '',
      status: '',
      seats: [
        { seatClass: '', noOfSeats: undefined as any, availableSeats: undefined as any, price: undefined as any }
      ]
    };
  }

  fetchFlights() {
    this.http.get<Flight[]>(`${environment.apiUrlLogin}flights`).subscribe({
      next: (flights) => this.flights = flights,
      error: () => this.flights = []
    });
  }

  addFlight() {
    this.http.post(`${environment.apiUrlLogin}addFlight`, this.newFlight).subscribe({
      next: () => {
        this.showAddFlight = false;
        this.newFlight = this.getEmptyFlight();
        this.fetchFlights();
      }
    });
  }

  editFlight(flight: Flight) {
    this.editingFlight = { ...flight }; // No change needed, keeping for context
  }

  updateFlight() {
    if (!this.editingFlight) return;
    this.http.put(`${environment.apiUrlLogin}updateFlight/${this.editingFlight.flightNumber}`, this.editingFlight).subscribe({
      next: () => {
        this.editingFlight = null;
        this.fetchFlights();
      }
    });
  }

  deleteFlight(flightNumber: string) {
    this.http.delete(`${environment.apiUrlLogin}deleteFlight/${flightNumber}`).subscribe({
      next: () => this.fetchFlights()
    });
  }

  logout() {
    localStorage.removeItem('jwt');
    this.router.navigate(['/admin-login']);
  }
}
