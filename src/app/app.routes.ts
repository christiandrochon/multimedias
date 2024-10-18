import {Routes} from '@angular/router';
import {MultimediaComponent} from './multimedia/multimedia.component';  // Chemin correct pour MultimediaComponent
import {AppComponent} from './app.component';
import {HelloComponent} from './hello/hello.component';

export const appRoutes: Routes = [
  {path: '', component: AppComponent},
  {path: 'images', component: MultimediaComponent},
  {path: 'hello', component: HelloComponent}
];
