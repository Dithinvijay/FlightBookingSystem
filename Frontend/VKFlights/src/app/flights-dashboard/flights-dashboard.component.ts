import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Pipe, PipeTransform } from '@angular/core';
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

// Standalone pipe for filtering flights
@Pipe({ name: 'flightFilter', standalone: true })
export class FlightFilterPipe implements PipeTransform {
  transform(flights: any[], filterText: string): any[] {
    if (!filterText) return flights;
    filterText = filterText.toLowerCase();
    return flights.filter(flight =>
      (flight.airline && flight.airline.toLowerCase().includes(filterText)) ||
      (flight.status && flight.status.toLowerCase().includes(filterText)) ||
      (flight.departureAirport && flight.departureAirport.toLowerCase().includes(filterText)) ||
      (flight.arrivalAirport && flight.arrivalAirport.toLowerCase().includes(filterText))
    );
  }
}

@Component({
  selector: 'app-flights-dashboard',
  templateUrl: './flights-dashboard.component.html',
  styleUrls: ['./flights-dashboard.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, FlightFilterPipe]
})
export class FlightsDashboardComponent implements OnInit {
onBookFlight(_t28: any) {
throw new Error('Method not implemented.');
}
  flights: Flight[] = [];
  error: string = '';
  loading: boolean = false;
  filterText: string = '';

  // Airline logo and color mapping
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
    'indigo': '#3A5BA0', // blue
    'airindia': '#D9232E', // red
    'spicejet': '#E03C31', // red
    'vistara': '#5B2C6F', // purple
    'goair': '#1A237E', // dark blue
    'airasia': '#D91C1F', // red
    'akasa': '#FF8200', // orange
    'allianceair': '#D9232E', // red
    'default': '#1976d2' // fallback blue
  };
isLoggedIn: any;

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
    // Add Authorization header if token exists
    const token = localStorage.getItem('jwt');
    const headers = token ? { headers: { 'Authorization': `Bearer ${token}` } } : {};
    this.http.get<Flight[]>(`${environment.apiUrlLogin}FMS/flights/${source}/${destination}`, headers
    ).subscribe({
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

