import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
  message: string;
  type: 'success' | 'error';
  show: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastSubject = new BehaviorSubject<Toast>({ message: '', type: 'success', show: false });
  toast$ = this.toastSubject.asObservable();

  showToast(message: string, type: 'success' | 'error' = 'success') {
    this.toastSubject.next({ message, type, show: true });
    setTimeout(() => this.hideToast(), 3000);
  }

  private hideToast() {
    this.toastSubject.next({ message: '', type: 'success', show: false });
  }
}