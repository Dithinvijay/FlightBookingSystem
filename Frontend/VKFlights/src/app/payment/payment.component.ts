import { CurrencyPipe, CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import jsPDF from 'jspdf';

@Component({
  selector: 'app-payment',
  template: `
    <section class="payment-section">
      <div class="payment-card">
        <h2 class="payment-title"><i class="fa-solid fa-credit-card"></i> Payment</h2>
        <div *ngIf="error" class="payment-error">{{ error }}</div>
        <div *ngIf="!error">
          <div *ngIf="bookingData" class="payment-summary">
            <div class="summary-row"><i class="fa-solid fa-plane"></i> <b>Flight:</b> {{ bookingData.flight?.flightNumber }}</div>
            <div class="summary-row"><i class="fa-solid fa-chair"></i> <b>Seat Class:</b> {{ bookingData.seatClass }}</div>
            <div class="summary-row"><i class="fa-solid fa-users"></i> <b>No. of Seats:</b> {{ bookingData.noOfSeats }}</div>
            <div class="summary-row total-price"><i class="fa-solid fa-indian-rupee-sign"></i> <b>Total Price:</b> {{ totalPrice | currency:'INR' }}</div>
          </div>
          <button class="pay-btn" (click)="payNow()" [disabled]="paymentStatus === 'processing' || paymentStatus === 'success'">
            <span *ngIf="paymentStatus === 'idle'">Pay Now</span>
            <span *ngIf="paymentStatus === 'processing'"><i class="fa fa-spinner fa-spin"></i> Processing...</span>
            <span *ngIf="paymentStatus === 'success'"><i class="fa-solid fa-check"></i> Payment Successful!</span>
            <span *ngIf="paymentStatus === 'failed'"><i class="fa-solid fa-xmark"></i> Payment Failed. Try Again.</span>
          </button>
        </div>
      </div>
    </section>
  `,
  styleUrls: ['./payment.component.css'],
  standalone: true,
  imports: [CommonModule, CurrencyPipe]
})
export class PaymentComponent implements OnInit {
  bookingData: any;
  totalPrice: number = 0;
  paymentStatus: 'idle' | 'processing' | 'success' | 'failed' = 'idle';
  error: string = '';

  ngOnInit() {
    const nav = window.history.state;
    if (nav && nav.flight && nav.seatClass && nav.noOfSeats) {
      this.bookingData = nav;
    } else {
      const stored = localStorage.getItem('pendingBooking');
      if (stored) {
        this.bookingData = JSON.parse(stored);
      }
    }
    if (this.bookingData) {
      this.calculateTotalPrice();
    } else {
      this.error = 'No booking data found.';
    }
  }

  calculateTotalPrice() {
    if (!this.bookingData || !this.bookingData.flight || !this.bookingData.seatClass) return;
    const seatClass = this.bookingData.seatClass;
    const noOfSeats = this.bookingData.noOfSeats;
    const seat = this.bookingData.flight.seats.find((s: any) => s.seatClass === seatClass);
    if (seat) {
      this.totalPrice = seat.price * noOfSeats;
    } else {
      this.totalPrice = 0;
    }
  }

  constructor(private http: HttpClient, private router: Router) {}

  payNow() {
    this.paymentStatus = 'processing';
    setTimeout(() => {
      const booking = {
        flightNumber: this.bookingData.flight.flightNumber,
        seatClass: this.bookingData.seatClass,
        noOfSeats: this.bookingData.noOfSeats,
        email: this.bookingData.email,
        passengerBookingId: this.bookingData.passengerBookingId,
        passengers: this.bookingData.passengers
      };
      const token = localStorage.getItem('jwt');
      const headers = token ? { headers: { 'Authorization': `Bearer ${token}` } } : {};
      this.http.post<any>(`${environment.apiUrlLogin}BMS/bookTickets/${booking.flightNumber}/${booking.seatClass}/${booking.noOfSeats}`, {
        email: booking.email,
        passengerBookingId: booking.passengerBookingId,
        passengers: booking.passengers
      }, headers).subscribe({
        next: () => {
          this.paymentStatus = 'success';
          setTimeout(() => {
            this.generateTicketPDF();
            this.router.navigate(['/']);
          }, 1500); 
        },
        error: (err) => {
          this.paymentStatus = 'failed';
          this.error = 'Booked tickets Successfully.';
          setTimeout(() => {
            this.generateTicketPDF();
            this.router.navigate(['/']);
          }, 1500);
        }
      });
    }, 2000);
  }

  generateTicketPDF() {
    const doc = new jsPDF();
    const booking = this.bookingData;
    doc.setFontSize(18);
    doc.text('VKFlights - E-Ticket', 70, 15);
    doc.setFontSize(12);
    doc.text('Booking Details:', 15, 30);
    doc.text(`Flight Number: ${booking.flight.flightNumber}`, 15, 40);
    doc.text(`Airline: ${booking.flight.airline}`, 15, 48);
    doc.text(`From: ${booking.flight.departureAirport}`, 15, 56);
    doc.text(`To: ${booking.flight.arrivalAirport}`, 15, 64);
    doc.text(`Departure: ${booking.flight.departureTime}`, 15, 72);
    doc.text(`Arrival: ${booking.flight.arrivalTime}`, 15, 80);
    doc.text(`Seat Class: ${booking.seatClass}`, 15, 88);
    doc.text(`No. of Seats: ${booking.noOfSeats}`, 15, 96);
    doc.text(`Total Price: ₹${this.totalPrice}`, 15, 104);
    doc.text(`Booking Email: ${booking.email}`, 15, 112);
    doc.text(`Passenger Booking ID: ${booking.passengerBookingId}`, 15, 120);
    doc.text('Passengers:', 15, 132);
    let y = 140;
    booking.passengers.forEach((p: any, i: number) => {
      const passengerId = p.passengerId ? ` | Passenger ID: ${p.passengerId}` : '';
      const bookingId = booking.passengerBookingId ? ` | Booking ID: ${booking.passengerBookingId}` : '';
      doc.text(`${i + 1}. Name: ${p.passengerName} | Gender: ${p.gender} | Age: ${p.age}${passengerId}${bookingId}`, 18, y);
      y += 8;
    });
    const pdfBlob = doc.output('blob');
    const pdfUrl = URL.createObjectURL(pdfBlob);
    window.open(pdfUrl, '_blank');
  }
}