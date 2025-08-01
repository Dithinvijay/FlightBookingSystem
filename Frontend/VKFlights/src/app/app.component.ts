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
    // Listen to router events to update login state on navigation
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.isLoggedIn = !!localStorage.getItem('jwt');
      }
    });
  }

  ngOnInit() {
    this.isLoggedIn = !!localStorage.getItem('jwt');
  }
}
