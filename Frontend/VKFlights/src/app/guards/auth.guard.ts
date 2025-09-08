import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    // Force refresh localStorage to avoid cache issues
    const token = localStorage.getItem('jwt');
    localStorage.setItem('_cache_bust', Date.now().toString());
    
    if (token) {
      return true;
    }
    this.router.navigate(['/login']);
    return false;
  }
}