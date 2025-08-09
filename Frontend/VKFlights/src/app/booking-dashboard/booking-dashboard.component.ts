import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { environment } from '../../environments/environment';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import jsPDF from 'jspdf';
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
    this.isLoggedIn = !!localStorage.getItem('jwt');
    if (!this.isLoggedIn) {
      this.router.navigate(['/login']);
      return;
    }
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
    const bookingData = {
      flightNumber: this.flight.flightNumber,
      seatClass,
      noOfSeats,
      email,
      passengerBookingId,
      passengers,
      flight: this.flight
    };
    const orderPayload = {
      amount: this.calculateTotalPrice(seatClass, noOfSeats), 
      currency: 'INR',
      receipt: `rcptid_${Date.now()}`,
      notes: { email, flightNumber: this.flight.flightNumber }
    };
    this.http.post<any>('http://localhost:1004/api/payments/orders', orderPayload).subscribe({
      next: (order) => {
        this.launchRazorpay(order, bookingData);
      },
      error: (err) => {
        this.error = 'Failed to initiate payment. Please try again.';
      }
    });
  }

  launchRazorpay(order: any, bookingData: any) {
    if (!order?.razorpayOrderId || !order?.amount) {
      this.error = 'Payment order not found or invalid.';
      return;
    }
    const options = {
      key: 'rzp_test_Krj1zUQFRHYumw',
      amount: order.amount * 100,
      currency: order.currency || 'INR',
      name: 'VKFlights',
      description: 'Flight Booking Payment',
      order_id: order.razorpayOrderId,
      handler: (response: any) => {
        this.onPaymentComplete(bookingData);
      },
      prefill: { email: bookingData.email || '' },
      theme: { color: '#1976d2' }
    };
    const rzp = new (window as any).Razorpay(options);
    rzp.on('payment.failed', (_: any) => {
      this.onPaymentComplete(bookingData);
    });
    rzp.open();
  }

  onPaymentComplete(bookingData: any) {
    const token = localStorage.getItem('jwt');
    const headers = token ? { headers: { 'Authorization': `Bearer ${token}` } } : {};
    this.http.post<any>(`${environment.apiUrlLogin}BMS/bookTickets/${bookingData.flightNumber}/${bookingData.seatClass}/${bookingData.noOfSeats}`, {
      email: bookingData.email,
      passengerBookingId: bookingData.passengerBookingId,
      passengers: bookingData.passengers
    }, headers).subscribe({
      next: () => {
        this.generateTicketPDFAndNavigate(bookingData);
      },
      error: () => {
        this.generateTicketPDFAndNavigate(bookingData);
      }
    });
  }

  generateTicketPDFAndNavigate(bookingData: any) {
    const doc = new jsPDF();
    const b = bookingData;
    doc.setFontSize(18);
    doc.text('VKFlights - E-Ticket', 70, 15);
    doc.setFontSize(12);
    doc.text('Booking Details:', 15, 30);
    doc.text(`Flight Number: ${b.flight.flightNumber}`, 15, 40);
    doc.text(`Airline: ${b.flight.airline}`, 15, 48);
    doc.text(`From: ${b.flight.departureAirport}`, 15, 56);
    doc.text(`To: ${b.flight.arrivalAirport}`, 15, 64);
    doc.text(`Departure: ${b.flight.departureTime}`, 15, 72);
    doc.text(`Arrival: ${b.flight.arrivalTime}`, 15, 80);
    doc.text(`Seat Class: ${b.seatClass}`, 15, 88);
    doc.text(`No. of Seats: ${b.noOfSeats}`, 15, 96);
    doc.text(`Total Price: ₹${this.calculateTotalPrice(b.seatClass, b.noOfSeats) / 100}`, 15, 104);
    doc.text(`Booking Email: ${b.email}`, 15, 112);
    doc.text(`Passenger Booking ID: ${b.passengerBookingId}`, 15, 120);
    doc.text('Passengers:', 15, 132);
    let y = 140;
    b.passengers.forEach((p: any, i: number) => {
      doc.text(
        `${i + 1}. Name: ${p.passengerName} | Gender: ${p.gender} | Age: ${p.age}` +
          (p.passengerId ? ` | Passenger ID: ${p.passengerId}` : ''),
        18,
        y
      );
      y += 8;
    });
    const pdfBlob = doc.output('blob');
    const pdfUrl = URL.createObjectURL(pdfBlob);
    window.open(pdfUrl, '_blank');
    setTimeout(() => {
      this.router.navigate(['/']);
    }, 500);
  }

  calculateTotalPrice(seatClass: string, noOfSeats: number): number {
    if (!this.flight || !this.flight.seats) return 0;
    const seat = this.flight.seats.find((s: any) => s.seatClass === seatClass);
    if (seat) {
      return seat.price * noOfSeats * 100; 
    }
    return 0;
  }
}
