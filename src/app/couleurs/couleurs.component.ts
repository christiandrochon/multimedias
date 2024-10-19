import {HttpClient} from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';

interface ImageResponse {
  randomImage: string;
  swappedImage: string;
}

@Component({
  selector: 'app-image-swap',
  templateUrl: './couleurs.component.html',
  standalone: true,
  styleUrls: ['./couleurs.component.css'],
  imports: [CommonModule]  // Necessaire pour ngif dans le html
})
export class CouleursComponent implements OnInit {

  randomImageUrl: string = '';
  swappedImageUrl: string = '';
  randomImageUrls: string[] = [];
  imageUrl: string = '';

  constructor(private http: HttpClient) {
  }

  ngOnInit(): void {
    this.getImages();
  }

  getImages(): void {
    this.http.get<ImageResponse>('http://localhost:8087/swapRandomImage', {responseType: 'json'})
      .subscribe(response => {
        this.randomImageUrl = 'http://localhost:8087' + response.randomImage;
        this.swappedImageUrl = 'http://localhost:8087' + response.swappedImage;
        // this.randomImageUrls = response;  // Les deux URL de la même image
      }, error => {
        console.error('Error fetching images', error);
      });
  }
}
