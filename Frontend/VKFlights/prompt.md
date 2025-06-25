# Indigo Offers/Deals Static Page Prompt

## Objective
Develop a modern, responsive Angular static page inspired by the Indigo "Deals and Offers" page ([reference link](https://www.goindigo.in/campaigns/indigo-offers.html?linkNav=Deals%20and%20Offers%7C%7CHeader)), but tailored for the VKFlights application. The page should showcase a set of hardcoded flight deals, offers, and promotions in an engaging, user-friendly format. No backend or API integration is required.

## Requirements

### 1. Layout & UI
- Use a clean, visually appealing layout with VKFlights color theme (primary: #76B2B9, secondary: #173154, background: #EDF6F7, accent: #B9D1D8, white: #FAFCFB).
- Responsive design: grid or card-based layout for offers, adapting to mobile and desktop.
- Prominent header/banner for "Deals & Offers".
- Each offer/deal should be displayed as a card with:
  - Title
  - Short description
  - Offer code (if any)
  - Validity period
  - Call-to-action button (e.g., "Book Now", "View Details")
  - Optional: image or icon

### 2. Features
- All deals/offers are hardcoded in the component (no backend/API).
- Highlight featured or expiring soon offers.
- Show offer details in a modal or separate section when clicked.
- (Optional) Integrate with booking flow if user is logged in.

### 3. Accessibility & UX
- All buttons and links should be keyboard accessible.
- Use clear, readable fonts and sufficient color contrast.
- Animate card hover/focus for interactivity.

### 4. Code Structure
- Use Angular standalone components for modularity.
- Separate components for:
  - DealsDashboardComponent (main page)
  - DealCardComponent (individual offer)
  - (Optional) DealDetailsModalComponent
- All deals data is defined as a static array in the dashboard component.

### 5. Example Data Model
```typescript
interface Deal {
  id: string;
  title: string;
  description: string;
  code?: string;
  validTill: string;
  imageUrl?: string;
  featured?: boolean;
}
```

### 6. Inspiration
- Reference: [Indigo Offers Page](https://www.goindigo.in/campaigns/indigo-offers.html?linkNav=Deals%20and%20Offers%7C%7CHeader)
- Use VKFlights branding and color palette.
- Focus on clarity, ease of use, and mobile-friendliness.

---

**Deliverables:**
- Angular components as described above.
- CSS/SCSS for responsive, modern UI.
- All deals/offers hardcoded in the component (no backend/API).
- Demo page accessible at `/deals` route.
