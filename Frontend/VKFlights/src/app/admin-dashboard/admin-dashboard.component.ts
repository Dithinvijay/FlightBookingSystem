
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
addSeatRow() {
  // Add a new seat row with incremented seatId
  this.seatIdCounter++;
  this.newFlight.seats.push({
    seatId: this.seatIdCounter,
    seatClass: '',
    noOfSeats: null,
    availableSeats: null,
    price: null
  });
}

removeSeatRow(index: number) {
  // Remove the seat row at the given index, but keep at least one row
  if (this.newFlight.seats.length > 1) {
    this.newFlight.seats.splice(index, 1);
  }
}
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
  
  // Validation error properties
  addFlightErrors: any = {};
  updateStatusErrors: any = {};
  updateSeatsErrors: any = {};
  deleteFlightErrors: any = {};
  
  // Toast notification properties
  toastMessage = '';
  showToast = false;

  constructor(public http: HttpClient, private router: Router) {
    this.newFlight = this.getEmptyFlight();
  }

  showToastNotification(message: string) {
    this.toastMessage = message;
    setTimeout(() => this.showToast = true, 100);
    setTimeout(() => {
      this.showToast = false;
      setTimeout(() => this.toastMessage = '', 300);
    }, 3000);
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


  updateFlightStatus() {
    if (!this.validateUpdateStatus()) {
      return;
    }
    
    // Show toast popup immediately when Update Flight Status is clicked
    this.flightMessage = 'Flight Status Updated...';
    this.flightMessageType = 'success';
    this.showToastNotification('Flight status updated successfully!');
    setTimeout(() => {
      this.flightMessage = '';
    }, 2000);
    const token = localStorage.getItem('jwt');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    this.http.put(`${environment.apiUrlLogin}FMS/updatestatus/${this.updateFlightNumber}/${this.updateStatus}`, {}, { headers, responseType: 'text' }).subscribe({
      next: () => {
        this.updateFlightNumber = '';
        this.updateStatus = '';
      },
      error: () => {
        // Optionally handle error
      }
    });
  }

  updateAvailableSeats() {
    if (!this.validateUpdateSeats()) {
      return;
    }
    
    // Show toast popup immediately when Update Seats is clicked
    this.flightMessage = 'Seats Updated...';
    this.flightMessageType = 'success';
    this.showToastNotification('Seats Updated Successfully!');
    setTimeout(() => {
      this.flightMessage = '';
    }, 2000);
    const token = localStorage.getItem('jwt');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    this.http.put(`${environment.apiUrlLogin}FMS/updateSeats/${this.updateSeatsFlightNumber}/${this.updateSeatsCount}/${this.updateSeatsClass}`, {}, { headers, responseType: 'text' }).subscribe({
      next: () => {
        this.updateSeatsFlightNumber = '';
        this.updateSeatsClass = '';
        this.updateSeatsCount = null;
      },
      error: () => {
        // Optionally handle error
      }
    });
  }

  deleteFlight() {
    if (!this.validateDeleteFlight()) {
      return;
    }
    
    // Show toast popup immediately when Delete Flight is clicked
    this.flightMessage = 'Flight Deleted...';
    this.flightMessageType = 'success';
    this.showToastNotification('Flight deleted successfully!');
    setTimeout(() => {
      this.flightMessage = '';
    }, 2000);
    const token = localStorage.getItem('jwt');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    this.http.delete(`${environment.apiUrlLogin}FMS/deleteByFlightId/${this.deleteFlightId}`, { headers, responseType: 'text' }).subscribe({
      next: () => {
        this.deleteFlightId = '';
      },
      error: () => {
        // Optionally handle error
      }
    });
  }

addFlight() {
    if (!this.validateAddFlight()) {
      return;
    }
    
    // Show toast popup immediately when Add Flight is clicked
    this.flightMessage = 'Flight Added Successfully...';
    this.flightMessageType = 'success';
    this.showToastNotification('Flight added successfully!');
    setTimeout(() => {
      this.flightMessage = '';
    }, 2000);

    const pad = (n: number) => n < 10 ? '0' + n : n;
    const dtDep = new Date(this.newFlight.departureTime);
    const dtArr = new Date(this.newFlight.arrivalTime);
    const formattedDep = dtDep.getFullYear() + '-' + pad(dtDep.getMonth() + 1) + '-' + pad(dtDep.getDate()) + ' '
      + pad(dtDep.getHours()) + ':' + pad(dtDep.getMinutes()) + ':' + pad(dtDep.getSeconds());
    const formattedArr = dtArr.getFullYear() + '-' + pad(dtArr.getMonth() + 1) + '-' + pad(dtArr.getDate()) + ' '
      + pad(dtArr.getHours()) + ':' + pad(dtArr.getMinutes()) + ':' + pad(dtArr.getSeconds());
    const seatsWithId = this.newFlight.seats.map((seat, idx) => ({
      ...seat,
      seatId: seat.seatId || idx + 1
    }));
    const flightToSend = { ...this.newFlight, departureTime: formattedDep, arrivalTime: formattedArr, seats: seatsWithId };

    const token = localStorage.getItem('jwt');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    this.http.post(`${environment.apiUrlLogin}FMS/addFlight`, flightToSend, { headers, responseType: 'text' }).subscribe({
      next: () => {
        this.showAddFlight = false;
        this.newFlight = this.getEmptyFlight();
      },
      error: (err: any) => {
        console.error('Backend error:', err);
        this.showAddFlight = false;
        this.newFlight = this.getEmptyFlight();
      }
    });
  }

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
  logout() {
    localStorage.removeItem('jwt');
    this.showToastNotification('Logged out successfully!');
    setTimeout(() => this.router.navigate(['/admin-login']), 1000);
  }
  
  validateAddFlight(): boolean {
    this.addFlightErrors = {};
    let isValid = true;
    
    if (!this.newFlight.flightNumber?.trim()) {
      this.addFlightErrors.flightNumber = 'Flight number is required';
      isValid = false;
    }
    if (!this.newFlight.airline?.trim()) {
      this.addFlightErrors.airline = 'Airline is required';
      isValid = false;
    }
    if (!this.newFlight.departureAirport?.trim()) {
      this.addFlightErrors.departureAirport = 'Departure airport is required';
      isValid = false;
    }
    if (!this.newFlight.arrivalAirport?.trim()) {
      this.addFlightErrors.arrivalAirport = 'Arrival airport is required';
      isValid = false;
    }
    if (!this.newFlight.departureTime) {
      this.addFlightErrors.departureTime = 'Departure time is required';
      isValid = false;
    } else if (new Date(this.newFlight.departureTime) < new Date()) {
      this.addFlightErrors.departureTime = 'Departure time cannot be in the past';
      isValid = false;
    }
    if (!this.newFlight.arrivalTime) {
      this.addFlightErrors.arrivalTime = 'Arrival time is required';
      isValid = false;
    } else if (new Date(this.newFlight.arrivalTime) < new Date()) {
      this.addFlightErrors.arrivalTime = 'Arrival time cannot be in the past';
      isValid = false;
    } else if (this.newFlight.departureTime && new Date(this.newFlight.arrivalTime) <= new Date(this.newFlight.departureTime)) {
      this.addFlightErrors.arrivalTime = 'Arrival time must be after departure time';
      isValid = false;
    }
    if (!this.newFlight.status?.trim()) {
      this.addFlightErrors.status = 'Status is required';
      isValid = false;
    }
    
    // Validate seats
    this.newFlight.seats.forEach((seat, i) => {
      if (!seat.seatClass?.trim()) {
        this.addFlightErrors[`seatClass${i}`] = 'Seat class is required';
        isValid = false;
      }
      if (!seat.noOfSeats || seat.noOfSeats <= 0) {
        this.addFlightErrors[`noOfSeats${i}`] = 'Number of seats must be greater than 0';
        isValid = false;
      }
      if (!seat.availableSeats || seat.availableSeats < 0) {
        this.addFlightErrors[`availableSeats${i}`] = 'Available seats cannot be negative';
        isValid = false;
      }
      if (!seat.price || seat.price <= 0) {
        this.addFlightErrors[`price${i}`] = 'Price must be greater than 0';
        isValid = false;
      }
    });
    
    return isValid;
  }
  
  validateUpdateStatus(): boolean {
    this.updateStatusErrors = {};
    let isValid = true;
    
    if (!this.updateFlightNumber?.trim()) {
      this.updateStatusErrors.flightNumber = 'Flight number is required';
      isValid = false;
    }
    if (!this.updateStatus?.trim()) {
      this.updateStatusErrors.status = 'Status is required';
      isValid = false;
    }
    
    return isValid;
  }
  
  validateUpdateSeats(): boolean {
    this.updateSeatsErrors = {};
    let isValid = true;
    
    if (!this.updateSeatsFlightNumber?.trim()) {
      this.updateSeatsErrors.flightNumber = 'Flight number is required';
      isValid = false;
    }
    if (!this.updateSeatsClass?.trim()) {
      this.updateSeatsErrors.seatClass = 'Seat class is required';
      isValid = false;
    }
    if (!this.updateSeatsCount || this.updateSeatsCount <= 0) {
      this.updateSeatsErrors.seatsCount = 'Seats count must be greater than 0';
      isValid = false;
    }
    
    return isValid;
  }
  
  validateDeleteFlight(): boolean {
    this.deleteFlightErrors = {};
    let isValid = true;
    
    if (!this.deleteFlightId?.trim()) {
      this.deleteFlightErrors.flightId = 'Flight ID is required';
      isValid = false;
    }
    
    return isValid;
  }
  
  clearAddFlightErrors() {
    this.addFlightErrors = {};
  }
  
  clearUpdateStatusErrors() {
    this.updateStatusErrors = {};
  }
  
  clearUpdateSeatsErrors() {
    this.updateSeatsErrors = {};
  }
  
  clearDeleteFlightErrors() {
    this.deleteFlightErrors = {};
  }
}
