import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { DealsComponent } from './deals/deals.component';
import { AboutComponent } from './about/about.component';
import { LoginComponent } from './login/login.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'deals', component: DealsComponent },
  { path: 'about', component: AboutComponent },
  { path: 'login', component: LoginComponent },
];
