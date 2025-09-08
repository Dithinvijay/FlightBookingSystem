import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const AuthGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = localStorage.getItem('jwt');
  
  if (token) {
    return true;
  }
  
  router.navigate(['/login']);
  return false;
};