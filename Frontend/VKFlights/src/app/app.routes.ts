
import { Routes } from '@angular/router';
import { FlightsDashboardComponent } from './flights-dashboard/flights-dashboard.component';
import { HomeComponent } from './home/home.component';
import { DealsDashboardComponent } from './dealsdashboard/deals-dashboard.component';
import { AboutComponent } from './about/about.component';
import { AuthComponent } from './auth/auth.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AdminLoginComponent } from './admin-login/admin-login.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { BookingDashboardComponent } from './booking-dashboard/booking-dashboard.component';
import { CheckinComponent } from './checkin/checkin.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'deals', component: DealsDashboardComponent },
  { path: 'about', component: AboutComponent },
  { path: 'login', component: AuthComponent },
  { path: 'register', component: AuthComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'flights-dashboard', component: FlightsDashboardComponent },
  { path: 'booking-dashboard', component: BookingDashboardComponent },
  { path: 'admin-login', component: AdminLoginComponent },
  { path: 'admin-dashboard', component: AdminDashboardComponent },
  { path: 'checkin', component: CheckinComponent },

];
