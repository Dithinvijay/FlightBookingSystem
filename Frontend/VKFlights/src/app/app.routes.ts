import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { DealsDashboardComponent } from './dealsdashboard/deals-dashboard.component';
import { AboutComponent } from './about/about.component';
import { AuthComponent } from './auth/auth.component';
import { DashboardComponent } from './dashboard/dashboard.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'deals', component: DealsDashboardComponent },
  { path: 'about', component: AboutComponent },
  { path: 'login', component: AuthComponent },
  { path: 'register', component: AuthComponent },
  { path: 'dashboard', component: DashboardComponent },
];
