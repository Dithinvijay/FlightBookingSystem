import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-checkin',
  templateUrl: './checkin.component.html',
  styleUrls: ['./checkin.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class CheckinComponent {
  checkinForm: FormGroup;
  loading = false;

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router, public toastService: ToastService) {
    this.checkinForm = this.fb.group({
      passengerId: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.checkinForm.invalid) return;
    
    this.loading = true;
    
    const passengerId = this.checkinForm.value.passengerId;
    const token = localStorage.getItem('jwt');
    const options = token ? {
      responseType: 'text' as const,
      headers: { Authorization: `Bearer ${token}` }
    } : {
      responseType: 'text' as const
    };
    
    this.http.get(`http://localhost:8080/checkIn/checkIn/${passengerId}`, options).subscribe({
      next: (response: string) => {
        this.toastService.showToast(response, 'success');
        this.loading = false;
        this.checkinForm.reset();
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 1500);
      },
      error: (err) => {
        let errorMessage = 'An error occurred during check-in';
        if (err.error && typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.message) {
          errorMessage = err.message;
        }
        this.toastService.showToast(errorMessage, 'error');
        this.loading = false;
      }
    });
  }
}
