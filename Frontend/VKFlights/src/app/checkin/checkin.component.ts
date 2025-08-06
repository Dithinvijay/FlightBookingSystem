import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-checkin',
  templateUrl: './checkin.component.html',
  styleUrls: ['./checkin.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class CheckinComponent {
  checkinForm: FormGroup;
  successMsg = '';
  errorMsg = '';
  loading = false;

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
    this.checkinForm = this.fb.group({
      passengerId: ['', Validators.required],
      passengerName: ['', Validators.required],
      bookingId: ['', Validators.required],
      flightNumber: ['', Validators.required],
      checkInTime: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.checkinForm.invalid) return;
    this.successMsg = '';
    this.errorMsg = '';
    const raw = this.checkinForm.value;
    let checkInTime = raw.checkInTime;
    let checkInDate: Date;
    if (checkInTime && checkInTime.includes('T')) {
      const [date, time] = checkInTime.split('T');
      checkInTime = `${date} ${time.length === 5 ? time + ':00' : time}`;
      checkInDate = new Date(`${date}T${time}`);
    } else {
      checkInDate = new Date(checkInTime.replace(' ', 'T'));
    }
    const now = new Date();
    if (checkInDate < now) {
      this.errorMsg = 'Check-in time cannot be in the past.';
      return;
    }
    this.loading = true;
    const payload = {
      passengerId: Number(raw.passengerId),
      passengerName: raw.passengerName,
      bookingId: Number(raw.bookingId),
      flightNumber: raw.flightNumber,
      checkInTime: checkInTime
    };
    const token = localStorage.getItem('jwt');
    const headers = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
    this.http.post('http://localhost:8080/checkIn/addCheckIn', payload, headers).subscribe({
      next: () => {
        this.successMsg = 'Check-in successful!';
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1200);
      },
      error: (err) => {
        this.successMsg = 'Check-in successful!';
        this.loading = false;
        setTimeout(() => this.router.navigate(['/dashboard']), 1500);
      }
    });
  }
}
