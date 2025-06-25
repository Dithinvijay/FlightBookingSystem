import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Deal {
  id: string;
  title: string;
  description: string;
  code?: string;
  validTill: string;
  imageUrl?: string;
  featured?: boolean;
}

@Component({
  selector: 'app-deals-dashboard',
  templateUrl: './deals-dashboard.component.html',
  styleUrls: ['./deals-dashboard.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class DealsDashboardComponent {
bannerUrl = 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=80';
  deals: Deal[] = [
    {
      id: '1',
      title: 'Summer Bonanza: 20% Off on All Domestic Flights',
      description: 'Book now and get 20% off on all domestic routes. Limited period offer!',
      code: 'SUMMER20',
      validTill: '2025-07-15',
      imageUrl: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=600&q=80',
      featured: true
    },
    {
      id: '2',
      title: 'Student Special: Extra Baggage Allowance',
      description: 'Students get an extra 10kg baggage allowance on select flights.',
      code: 'STUDENT10',
      validTill: '2025-08-31',
      imageUrl: 'https://images.unsplash.com/photo-1464037866556-6812c9d1c72e?auto=format&fit=crop&w=600&q=80'
    },
    {
      id: '3',
      title: 'Early Bird: Flat ₹1000 Off',
      description: 'Book 30 days in advance and get flat ₹1000 off on your booking.',
      code: 'EARLYBIRD',
      validTill: '2025-09-30',
      imageUrl: 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?auto=format&fit=crop&w=600&q=80',
      featured: false
    },
    {
      id: '4',
      title: 'International Getaway: 15% Off',
      description: 'Fly international and save 15% on select destinations.',
      code: 'INTL15',
      validTill: '2025-07-31',
      imageUrl: 'https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=600&q=80'
    },
    {
      id: '5',
      title: 'Family Saver: Kids Fly Free',
      description: 'Book for 2 adults and 2 kids, and pay only for the adults!',
      code: 'FAMILYFREE',
      validTill: '2025-08-15',
      imageUrl: 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=600&q=80',
      featured: true
    }
  ];
  selectedDeal: Deal | null = null;

  showDetails(deal: Deal) {
    this.selectedDeal = deal;
  }
  closeDetails() {
    this.selectedDeal = null;
  }
}
