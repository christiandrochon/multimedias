import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HistogrammeService {

  private apiUrl = 'http://localhost:8087';

  constructor(private http: HttpClient) {}

  // Appel à l'API pour récupérer l'histogramme et l'URL de l'image
  getGrayLevelHistogram(numBins: number): Observable<{ histogram: number[], imageUrl: string }> {
    return this.http.get<{ histogram: number[], imageUrl: string }>(`${this.apiUrl}/histogramme-gris?numBins=${numBins}`);
  }

  // constructor() { }

  // // Fonction pour calculer l'histogramme en niveaux de gris
  // computeGrayLevelHistogram(imageData: any, numBins: number): number[] {
  //   const histogram = new Array(numBins).fill(0);
  //   for (let i = 0; i < imageData.length; i += 4) {
  //     const gray = Math.floor((imageData[i] + imageData[i + 1] + imageData[i + 2]) / 3);
  //     const binIndex = Math.floor((gray / 255) * (numBins - 1));
  //     histogram[binIndex]++;
  //   }
  //   return histogram;
  // }
  //
  // // Fonction pour calculer l'histogramme en couleurs (RGB)
  // computeRGBHistogram(imageData: any, numLevels: number): { red: number[], green: number[], blue: number[] } {
  //   const redHistogram = new Array(numLevels).fill(0);
  //   const greenHistogram = new Array(numLevels).fill(0);
  //   const blueHistogram = new Array(numLevels).fill(0);
  //
  //   for (let i = 0; i < imageData.length; i += 4) {
  //     const red = imageData[i];
  //     const green = imageData[i + 1];
  //     const blue = imageData[i + 2];
  //
  //     const redBinIndex = Math.floor((red / 255) * (numLevels - 1));
  //     const greenBinIndex = Math.floor((green / 255) * (numLevels - 1));
  //     const blueBinIndex = Math.floor((blue / 255) * (numLevels - 1));
  //
  //     redHistogram[redBinIndex]++;
  //     greenHistogram[greenBinIndex]++;
  //     blueHistogram[blueBinIndex]++;
  //   }
  //
  //   return { red: redHistogram, green: greenHistogram, blue: blueHistogram };
  // }
}
