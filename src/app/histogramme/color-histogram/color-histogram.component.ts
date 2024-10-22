import {Component, OnInit} from '@angular/core';
import {HistogrammeService} from '../../histogrammeservice/histogramme.service';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {BaseChartDirective} from 'ng2-charts';

@Component({
  selector: 'app-color-histogram',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './color-histogram.component.html',
  styleUrl: './color-histogram.component.css'
})
export class ColorHistogramComponent implements OnInit {

  numBins: number = 8;  // Valeur par défaut pour le nombre de bins
  imageUrl: string = '';  // URL de l'image

  // Histogrammes RVB
  redHistogram: number[] = [];
  greenHistogram: number[] = [];
  blueHistogram: number[] = [];

  // Intervalles des niveaux de couleur
  binRangesRed: { label: string, start: number, end: number }[] = [];
  binRangesGreen: { label: string, start: number, end: number }[] = [];
  binRangesBlue: { label: string, start: number, end: number }[] = [];

  // Configuration des données pour le graphique
  chartData: any[] = [];
  chartLabels: string[] = [];
  chartOptions = {
    responsive: true,
    scales: {
      x: { title: { display: true, text: 'Bins' } },
      y: { title: { display: true, text: 'Pixels' } }
    }
  };

  constructor(private histogramService: HistogrammeService) {
  }

  ngOnInit(): void {
    this.updateBins();
    this.loadHistogram();
    // this.fetchRandomImage();
  }

  // Méthode appelée pour charger une nouvelle image et les histogrammes RVB
  loadHistogram(): void {
    this.histogramService.getRGBHistogram(this.numBins).subscribe(
      (response: { redHistogram: number[], greenHistogram: number[], blueHistogram: number[], imageUrl: string }) => {
        this.redHistogram = response.redHistogram;
        this.greenHistogram = response.greenHistogram;
        this.blueHistogram = response.blueHistogram;
        this.imageUrl = response.imageUrl;

        this.updateChart();  // Mettre à jour le graphique avec les nouvelles données
      },
      (error) => {
        console.error('Erreur lors du chargement de l\'histogramme', error);
      }
    );
  }

  updateChart(): void {
    this.chartLabels = Array.from({ length: this.numBins }, (_, i) => `Bin ${i}`);

    this.chartData = [
      { data: this.redHistogram, label: 'Rouge', backgroundColor: 'rgba(255, 99, 132, 0.5)', borderColor: 'rgba(255, 99, 132, 1)' },
      { data: this.greenHistogram, label: 'Vert', backgroundColor: 'rgba(75, 192, 192, 0.5)', borderColor: 'rgba(75, 192, 192, 1)' },
      { data: this.blueHistogram, label: 'Bleu', backgroundColor: 'rgba(54, 162, 235, 0.5)', borderColor: 'rgba(54, 162, 235, 1)' }
    ];
  }

  // Méthode appelée pour calculer et afficher les intervalles des bins pour chaque couleur
  updateBins(): void {
    const binSize = Math.floor(256 / this.numBins);

    // Réinitialisation des intervalles
    this.binRangesRed = [];
    this.binRangesGreen = [];
    this.binRangesBlue = [];

    for (let i = 0; i < this.numBins; i++) {
      const start = i * binSize;
      const end = (i === this.numBins - 1) ? 255 : (start + binSize - 1);
      this.binRangesRed.push({ label: `Bin ${i}`, start, end });
      this.binRangesGreen.push({ label: `Bin ${i}`, start, end });
      this.binRangesBlue.push({ label: `Bin ${i}`, start, end });
    }
  }

  // Méthode pour recharger une nouvelle image aléatoire
  loadNewImage(): void {
    this.loadHistogram();
  }

  // fetchRandomImage(): void {
  //   // Appel API qui récupère un fichier JSON avec les informations de l'image
  //   this.http.get<any>('http://localhost:8087/histogramme-couleur').subscribe(data => {
  //     this.imageUrl = data.imageUrl;  // URL de l'image pour affichage
  //     this.extractImageData(data.imageData);  // Données de l'image en base64
  //   });
  // }
  //
  // extractImageData(base64ImageData: string): void {
  //   // Création d'une nouvelle image à partir des données base64
  //   const img = new Image();
  //   img.src = base64ImageData;
  //   img.onload = () => {
  //     // Création d'un canvas temporaire pour extraire les données des pixels
  //     const canvas = document.createElement('canvas');
  //     canvas.width = img.width;
  //     canvas.height = img.height;
  //     const ctx = canvas.getContext('2d');
  //     if (ctx) {
  //       ctx.drawImage(img, 0, 0);
  //       const imageData = ctx.getImageData(0, 0, img.width, img.height);
  //       // Calcul de l'histogramme en couleurs à partir des données des pixels
  //       this.colorHistogram = this.histogramService.computeRGBHistogram(imageData.data, 16);
  //     }
  //   };
  // }
}
