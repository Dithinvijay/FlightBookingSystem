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
    this.loading = true;
    this.successMsg = '';
    this.errorMsg = '';
    const data = this.checkinForm.value;
    this.http.post(`${environment.apiUrlLogin}checkIn/addCheckIn`, data).subscribe({
      next: () => {
        this.successMsg = 'Check-in successful!';
        this.loading = false;
        setTimeout(() => this.router.navigate(['/dashboard']), 2000);
      },
      error: (err) => {
        this.errorMsg = err.error?.message || 'Check-in failed, Enter the valid data.';
        this.loading = false;
      }
    });
  }
}
