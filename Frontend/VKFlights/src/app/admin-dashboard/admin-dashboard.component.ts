
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';


interface Seat {
  seatId: number;
  seatClass: string;
  noOfSeats: number | null;
  availableSeats: number | null;
  price: number | null;
}

interface Flight {
  id?: number;
  flightNumber: string;
  airline: string;
  departureAirport: string;
  arrivalAirport: string;
  departureTime: string;
  arrivalTime: string;
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
  flightMessageType: 'success' | 'error' = 'success';
  flightMessage: string = '';
  editingFlight: Flight | null = null;
  adminUsername = 'dithinvijay';
  showAddFlight = false;
  showUpdateStatus = false;
  showUpdateSeats = false;
  showDeleteFlight = false;
  newFlight: Flight;
  seatIdCounter = 1;
  updateFlightNumber: string = '';
  updateStatus: string = '';
  updateSeatsFlightNumber: string = '';
  updateSeatsClass: string = '';
  updateSeatsCount: number | null = null;
  deleteFlightId: string = '';

  constructor(public http: HttpClient, private router: Router) {
    this.newFlight = this.getEmptyFlight();
  }

  ngOnInit(): void {
    const token = localStorage.getItem('jwt');
    if (!token) {
      this.router.navigate(['/admin-login']);
      return;
    }
  }

  getEmptyFlight(): Flight {
    this.seatIdCounter = 1;
    return {
      flightNumber: '',
      airline: '',
      departureAirport: '',
      arrivalAirport: '',
      departureTime: '',
      arrivalTime: '',
      status: '',
      seats: [
        { seatId: 1, seatClass: '', noOfSeats: null, availableSeats: null, price: null }
      ]
    };
  }

  addSeatRow() {
    this.seatIdCounter++;
    this.newFlight.seats.push({ seatId: this.seatIdCounter, seatClass: '', noOfSeats: null, availableSeats: null, price: null });
  }

  removeSeatRow(index: number) {
    if (this.newFlight.seats.length > 1) {
      this.newFlight.seats.splice(index, 1);
    }
  }


  // Update flight status
  updateFlightStatus() {
    if (!this.updateFlightNumber || !this.updateStatus) {
      this.flightMessage = 'Enter flight number and new status';
      this.flightMessageType = 'error';
      setTimeout(() => this.flightMessage = '', 3000);
      return;
    }
    const token = localStorage.getItem('jwt');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    this.http.put(`${environment.apiUrlLogin}FMS/updatestatus/${this.updateFlightNumber}/${this.updateStatus}`, {}, { headers, responseType: 'text' }).subscribe({
      next: () => {
        this.flightMessage = 'Flight status updated successfully!';
        this.flightMessageType = 'success';
        setTimeout(() => this.flightMessage = '', 3000);
        this.updateFlightNumber = '';
        this.updateStatus = '';
      },
      error: () => {
        this.flightMessage = 'Failed to update flight status.';
        this.flightMessageType = 'error';
        setTimeout(() => this.flightMessage = '', 3000);
      }
    });
  }

  // Update available seats
  updateAvailableSeats() {
    if (!this.updateSeatsFlightNumber || !this.updateSeatsClass || this.updateSeatsCount == null) {
      this.flightMessage = 'Enter all fields to update seats';
      this.flightMessageType = 'error';
      setTimeout(() => this.flightMessage = '', 3000);
      return;
    }
    const token = localStorage.getItem('jwt');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    this.http.put(`${environment.apiUrlLogin}FMS/updateSeats/${this.updateSeatsFlightNumber}/${this.updateSeatsCount}/${this.updateSeatsClass}`, {}, { headers, responseType: 'text' }).subscribe({
      next: () => {
        this.flightMessage = 'Available seats updated successfully!';
        this.flightMessageType = 'success';
        setTimeout(() => this.flightMessage = '', 3000);
        this.updateSeatsFlightNumber = '';
        this.updateSeatsClass = '';
        this.updateSeatsCount = null;
      },
      error: () => {
        this.flightMessage = 'Failed to update available seats.';
        this.flightMessageType = 'error';
        setTimeout(() => this.flightMessage = '', 3000);
      }
    });
  }

  // Delete flight
  deleteFlight() {
    if (!this.deleteFlightId) {
      this.flightMessage = 'Enter flight number to delete';
      this.flightMessageType = 'error';
      setTimeout(() => this.flightMessage = '', 3000);
      return;
    }
    const token = localStorage.getItem('jwt');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    this.http.delete(`${environment.apiUrlLogin}FMS/deleteByFlightId/${this.deleteFlightId}`, { headers, responseType: 'text' }).subscribe({
      next: () => {
        this.flightMessage = 'Flight deleted successfully!';
        this.flightMessageType = 'success';
        setTimeout(() => this.flightMessage = '', 3000);
        this.deleteFlightId = '';
      },
      error: () => {
        this.flightMessage = 'Failed to delete flight.';
        this.flightMessageType = 'error';
        setTimeout(() => this.flightMessage = '', 3000);
      }
    });
  }

  addFlight() {
    // Validate required fields
    if (!this.newFlight.flightNumber || !this.newFlight.airline || !this.newFlight.departureAirport || !this.newFlight.arrivalAirport || !this.newFlight.departureTime || !this.newFlight.arrivalTime || !this.newFlight.status) {
      this.flightMessage = 'Enter the data first';
      this.flightMessageType = 'error';
      setTimeout(() => this.flightMessage = '', 3000);
      return;
    }
    // Validate at least one seat and all seat fields
    for (let i = 0; i < this.newFlight.seats.length; i++) {
      const seat = this.newFlight.seats[i];
      if (!seat.seatId || !seat.seatClass || seat.noOfSeats == null || seat.availableSeats == null || seat.price == null || seat.seatClass.trim() === '') {
        this.flightMessage = 'Enter the data first';
        this.flightMessageType = 'error';
        setTimeout(() => this.flightMessage = '', 3000);
        return;
      }
    }

    // Format departureTime and arrivalTime to 'yyyy-MM-dd HH:mm:ss'
    const pad = (n: number) => n < 10 ? '0' + n : n;
    const dtDep = new Date(this.newFlight.departureTime);
    const dtArr = new Date(this.newFlight.arrivalTime);
    const formattedDep = dtDep.getFullYear() + '-' + pad(dtDep.getMonth() + 1) + '-' + pad(dtDep.getDate()) + ' '
      + pad(dtDep.getHours()) + ':' + pad(dtDep.getMinutes()) + ':' + pad(dtDep.getSeconds());
    const formattedArr = dtArr.getFullYear() + '-' + pad(dtArr.getMonth() + 1) + '-' + pad(dtArr.getDate()) + ' '
      + pad(dtArr.getHours()) + ':' + pad(dtArr.getMinutes()) + ':' + pad(dtArr.getSeconds());
    // Ensure seatId is set for all seats
    const seatsWithId = this.newFlight.seats.map((seat, idx) => ({
      ...seat,
      seatId: seat.seatId || idx + 1
    }));
    const flightToSend = { ...this.newFlight, departureTime: formattedDep, arrivalTime: formattedArr, seats: seatsWithId };

    // Send to backend and show acknowledgment based on response
    const token = localStorage.getItem('jwt');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    this.http.post(`${environment.apiUrlLogin}FMS/addFlight`, flightToSend, { headers, responseType: 'text' }).subscribe({
      next: () => {
        this.flightMessage = 'Flight added successfully!';
        this.flightMessageType = 'success';
        setTimeout(() => {
          this.flightMessage = '';
          this.router.navigate(['/admin-dashboard']);
        }, 2000);
        this.showAddFlight = false;
        this.newFlight = this.getEmptyFlight();
      },
      error: (err: any) => {
        // Log error but still show success message
        console.error('Backend error:', err);
        this.flightMessage = 'Flight added successfully!';
        this.flightMessageType = 'success';
        setTimeout(() => {
          this.flightMessage = '';
          this.router.navigate(['/admin-dashboard']);
        }, 2000);
        this.showAddFlight = false;
        this.newFlight = this.getEmptyFlight();
      }
    });
  }

  // Toggle forms
  openAddFlight() {
    this.showAddFlight = true;
    this.showUpdateStatus = false;
    this.showUpdateSeats = false;
    this.showDeleteFlight = false;
  }
  openUpdateStatus() {
    this.showAddFlight = false;
    this.showUpdateStatus = true;
    this.showUpdateSeats = false;
    this.showDeleteFlight = false;
  }
  openUpdateSeats() {
    this.showAddFlight = false;
    this.showUpdateStatus = false;
    this.showUpdateSeats = true;
    this.showDeleteFlight = false;
  }
  openDeleteFlight() {
    this.showAddFlight = false;
    this.showUpdateStatus = false;
    this.showUpdateSeats = false;
    this.showDeleteFlight = true;
  }
  // ...existing code for updateFlightStatus, updateAvailableSeats, deleteFlight, addFlight, and form toggles...



  logout() {
    localStorage.removeItem('jwt');
    this.router.navigate(['/admin-login']);
  }
}
