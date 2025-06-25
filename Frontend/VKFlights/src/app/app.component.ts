import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';;
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
// This component serves as the root component for the Angular application
export class AppComponent { //these are properties of the AppComponent class
  title = 'VKFlights'; // The title of the application
  name= 'Dithin';

}
