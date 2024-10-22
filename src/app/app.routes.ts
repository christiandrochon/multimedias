import {provideRouter, Routes} from '@angular/router';

import {MultimediaComponent} from './multimedia/multimedia.component'; // Chemin correct pour MultimediaComponent
import {HelloComponent} from './hello/hello.component';
import {CouleursComponent} from './couleurs/couleurs.component';
import {HomeComponent} from './home/home.component';
import {GreyHistogramComponent} from './histogramme/grey-histogram/grey-histogram.component';


export const appRoutes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'images', component: MultimediaComponent},
  {path: 'hello', component: HelloComponent},
  {path: 'createCheckerboard', component: CouleursComponent},
  {path: 'swapRandomImage', component: CouleursComponent},
  {path: 'histogramme-gris/:bins', component: GreyHistogramComponent},
];

export const appRoutingProviders = [
  provideRouter(appRoutes)
];
