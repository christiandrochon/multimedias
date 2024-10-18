import {Component, OnInit} from '@angular/core';
import {ServicesService} from '../services/services.service';

@Component({
  selector: 'app-hello',
  standalone: true,
  imports: [],
  templateUrl: './hello.component.html',
  styleUrl: './hello.component.css'
})
export class HelloComponent  implements OnInit {
  greeting: string | undefined;

  constructor(private myApiService: ServicesService) { }

  ngOnInit(): void {
    this.myApiService.getGreeting().subscribe(
      (data: string) => {
        this.greeting = data;
      },
      (error) => {
        console.error('Error fetching data from backend', error);
      }
    );
  }

}
