
import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'VKFlights';
  name = 'Dithin';
  isLoggedIn = false;

  constructor(private router: Router) {
    this.isLoggedIn = this.hasValidToken();
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.isLoggedIn = this.hasValidToken();
      }
    });
  }

  ngOnInit() {
    this.isLoggedIn = this.hasValidToken();
  }

  hasValidToken(): boolean {
    const token = localStorage.getItem('jwt');
    if (!token) return false;
    const parts = token.split('.');
    if (parts.length !== 3) return false;
    try {
      const payload = JSON.parse(atob(parts[1]));
      if (payload.exp && Date.now() >= payload.exp * 1000) return false;
      return true;
    } catch {
      return false;
    }
  }

  logout() {
    localStorage.removeItem('jwt');
    this.isLoggedIn = false;
    this.router.navigate(['/login']);
  }

  isAdminDashboard(): boolean {
    return this.router.url.startsWith('/admin-dashboard');
  }

  isLoginOrDashboard(): boolean {
    const url = this.router.url;
    return url.startsWith('/login') || url.startsWith('/admin-login') || url.startsWith('/dashboard') || url.startsWith('/admin-dashboard');
  }
}
