import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { environment } from '../../environments/environment';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import jsPDF from 'jspdf';
@Component({
  selector: 'app-booking-dashboard',
  templateUrl: './booking-dashboard.component.html',
  styleUrls: ['./booking-dashboard.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule]
})
export class BookingDashboardComponent implements OnInit, AfterViewInit {
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
  isFormSubmitted: boolean = false;
  showCaptcha: boolean = false;
  captchaVerified: boolean = false;
  captchaCode: string = '';
  captchaInput: string = '';
  captchaError: string = '';
  @ViewChild('captchaCanvas', { static: false }) captchaCanvas!: ElementRef<HTMLCanvasElement>;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder
  ) {
    this.bookingForm = this.fb.group({
      seatClass: ['', Validators.required],
      noOfSeats: [1, [Validators.required, Validators.min(1), Validators.max(6)]],
      email: ['', [Validators.required, Validators.email]],
      passengerBookingId: [''],
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
    this.generatePassengerBookingId();
    this.bookingForm.get('noOfSeats')?.valueChanges.subscribe(() => this.updatePassengers());
    this.updatePassengers();
    this.bookingForm.valueChanges.subscribe(() => {
      if (this.isFormSubmitted) {
        this.isFormSubmitted = false;
      }
    });
  }

  get passengers() {
    return this.bookingForm.get('passengers') as FormArray;
  }

  updatePassengers() {
    const noOfSeats = this.bookingForm.get('noOfSeats')?.value || 1;
    while (this.passengers.length < noOfSeats) {
      this.passengers.push(this.fb.group({
        passengerName: ['', [Validators.required, Validators.maxLength(12)]],
        gender: ['', Validators.required],
        age: ['', [Validators.required, Validators.min(1), Validators.max(90)]]
      }));
    }
    while (this.passengers.length > noOfSeats) {
      this.passengers.removeAt(this.passengers.length - 1);
    }
  }

  submitBooking() {
    if (this.bookingForm.invalid || this.isFormSubmitted) return;
    this.isFormSubmitted = true;
    this.showCaptcha = true;
    setTimeout(() => this.generateCaptcha(), 100);
  }

  ngAfterViewInit(): void {
    // Canvas will be available after view init
  }

  generateCaptcha(): void {
    if (!this.captchaCanvas) return;
    
    const canvas = this.captchaCanvas.nativeElement;
    const ctx = canvas.getContext('2d')!;
    
    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // Generate random 6-character code
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    this.captchaCode = '';
    for (let i = 0; i < 6; i++) {
      this.captchaCode += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    
    // Set background with gradient
    const gradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height);
    gradient.addColorStop(0, '#f0f4f8');
    gradient.addColorStop(0.5, '#e2e8f0');
    gradient.addColorStop(1, '#cbd5e0');
    ctx.fillStyle = gradient;
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    
    // Add noise lines
    for (let i = 0; i < 8; i++) {
      ctx.strokeStyle = this.getRandomColor();
      ctx.lineWidth = Math.random() * 2 + 1;
      ctx.beginPath();
      ctx.moveTo(Math.random() * canvas.width, Math.random() * canvas.height);
      ctx.lineTo(Math.random() * canvas.width, Math.random() * canvas.height);
      ctx.stroke();
    }
    
    // Add noise dots
    for (let i = 0; i < 50; i++) {
      ctx.fillStyle = this.getRandomColor();
      ctx.beginPath();
      ctx.arc(Math.random() * canvas.width, Math.random() * canvas.height, Math.random() * 2, 0, 2 * Math.PI);
      ctx.fill();
    }
    
    // Draw captcha text
    const fonts = ['Arial', 'Helvetica', 'Times New Roman', 'Courier New', 'Verdana'];
    for (let i = 0; i < this.captchaCode.length; i++) {
      const char = this.captchaCode[i];
      const fontSize = 24 + Math.random() * 8;
      const font = fonts[Math.floor(Math.random() * fonts.length)];
      const angle = (Math.random() - 0.5) * 0.4;
      const x = 20 + i * 28 + Math.random() * 10;
      const y = 35 + Math.random() * 20;
      
      ctx.save();
      ctx.translate(x, y);
      ctx.rotate(angle);
      ctx.font = `bold ${fontSize}px ${font}`;
      ctx.fillStyle = this.getRandomDarkColor();
      ctx.strokeStyle = this.getRandomDarkColor();
      ctx.lineWidth = 0.5;
      ctx.fillText(char, 0, 0);
      ctx.strokeText(char, 0, 0);
      ctx.restore();
    }
    
    // Add distortion lines
    for (let i = 0; i < 3; i++) {
      ctx.strokeStyle = this.getRandomColor();
      ctx.lineWidth = Math.random() * 1.5 + 0.5;
      ctx.beginPath();
      const startX = Math.random() * canvas.width;
      const startY = Math.random() * canvas.height;
      const endX = Math.random() * canvas.width;
      const endY = Math.random() * canvas.height;
      const cpX = Math.random() * canvas.width;
      const cpY = Math.random() * canvas.height;
      ctx.moveTo(startX, startY);
      ctx.quadraticCurveTo(cpX, cpY, endX, endY);
      ctx.stroke();
    }
    
    this.captchaInput = '';
    this.captchaError = '';
  }
  
  getRandomColor(): string {
    const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff'];
    return colors[Math.floor(Math.random() * colors.length)] + '80';
  }
  
  getRandomDarkColor(): string {
    const colors = ['#2c3e50', '#34495e', '#7f8c8d', '#27ae60', '#2980b9', '#8e44ad', '#e74c3c'];
    return colors[Math.floor(Math.random() * colors.length)];
  }
  
  verifyCaptcha(): void {
    if (!this.captchaInput.trim()) {
      this.captchaError = 'Please enter the captcha code';
      return;
    }
    
    if (this.captchaInput.toUpperCase() === this.captchaCode.toUpperCase()) {
      this.captchaVerified = true;
      this.showCaptcha = false;
      this.captchaError = '';
      this.proceedToPayment();
    } else {
      this.captchaError = 'Invalid captcha code. Please try again.';
      this.generateCaptcha();
    }
  }
  
  closeCaptcha(): void {
    this.showCaptcha = false;
    this.isFormSubmitted = false;
    this.captchaVerified = false;
    this.captchaInput = '';
    this.captchaError = '';
  }

  proceedToPayment() {
    const { seatClass, noOfSeats, email, passengerBookingId, passengers } = this.bookingForm.value;
    const bookingData = {
      flightNumber: this.flight.flightNumber,
      seatClass,
      noOfSeats,
      email,
      passengerBookingId: String(passengerBookingId),
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
      this.router.navigate(['/checkin']);
    }, 500);
  }

  generatePassengerBookingId(): void {
    if (!this.flight?.airline) return;
    const airlinePrefix = this.flight.airline.replace(/\s+/g, '').substring(0, 3).toUpperCase();
    const randomNumber = Math.floor(10000 + Math.random() * 90000);
    const bookingId = `${airlinePrefix}${randomNumber}`;
    this.bookingForm.patchValue({ passengerBookingId: bookingId });
  }

  limitSeatsInput(event: any): void {
    const value = parseInt(event.target.value);
    if (value > 6) {
      event.target.value = '6';
      this.bookingForm.patchValue({ noOfSeats: 6 });
      alert('Maximum 6 seats allowed!');
    }
  }

  limitAgeInput(event: any, passengerIndex: number): void {
    const value = parseInt(event.target.value);
    if (value > 90) {
      event.target.value = '90';
      this.passengers.at(passengerIndex).patchValue({ age: 90 });
      alert('Maximum age is 90 years!');
    }
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
