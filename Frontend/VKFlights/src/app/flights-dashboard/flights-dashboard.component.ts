import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

interface Flight {
  id: number;
  flightNumber: string;
  airline: string;
  departureAirport: string;
  arrivalAirport: string;
  departureTime: string;
  status: string;
}

@Component({
  selector: 'app-flights-dashboard',
  templateUrl: './flights-dashboard.component.html',
  styleUrls: ['./flights-dashboard.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class FlightsDashboardComponent implements OnInit {
  flights: Flight[] = [];
  error: string = '';
  loading: boolean = false;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    // Get search params from localStorage (set by home component)
    const search = localStorage.getItem('flightSearch');
    if (!search) {
      this.error = 'No search parameters found.';
      return;
    }
    const { source, destination, date } = JSON.parse(search);
    this.loading = true;
    this.http.get<Flight[]>(`${environment.apiUrlLogin}flights`, {
      params: new HttpParams()
        .set('source', source)
        .set('destination', destination)
        .set('date', date)
    }).subscribe({
      next: (flights) => {
        this.flights = flights;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to fetch flights.';
        this.loading = false;
      }
    });
  }
}
