import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  urls = [
    { path: '/', description: 'Home Page' },
    { path: '/images', description: 'Multimedia Component' },
    { path: '/hello', description: 'Hello Component' },
    { path: '/createCheckerboard', description: 'Create Checkerboard' },
    { path: '/swapRandomImage', description: 'Swap Random Image' }
  ];
}
