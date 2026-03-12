import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DescargaArchivosComponent } from './components/pages/descarga-archivos/descarga-archivos.component';
import { DniLectoraComponent } from './components/pages/dni-lectora/dni-lectora.component';
import { IngreseExpedienteComponent } from './components/pages/ingrese-expediente/ingrese-expediente.component';
import { InserteUsbComponent } from './components/pages/inserte-usb/inserte-usb.component';
import { ListaExpedientesComponent } from './components/pages/lista-expedientes/lista-expedientes.component';
import { MensajeComponent } from './components/pages/mensaje/mensaje.component';
import { PreguntaCuadernoComponent } from './components/pages/pregunta-cuaderno/pregunta-cuaderno.component';
import { ElegirArchivosComponent } from './components/pages/elegir-archivos/elegir-archivos.component';
import { RangoDescargaComponent } from './components/pages/rango-descarga/rango-descarga.component';
import { ElegirEspecialidadComponent } from './components/pages/elegir-especialidad/elegir-especialidad.component';
import { EncuestaComponent } from './components/pages/encuesta/encuesta.component';
import { DespedidaFinalComponent } from './components/pages/despedida-final/despedida-final.component';
import { PreguntaFinalComponent } from './components/pages/pregunta-final/pregunta-final.component';

const routes: Routes = [
  { path:'' , redirectTo: 'dni-lectora', pathMatch: 'full' },
  { path: 'dni-lectora', component: DniLectoraComponent },
  { path: 'ingrese-expediente/:key', component: IngreseExpedienteComponent},
  { path: 'lista-expedientes/:page', component: ListaExpedientesComponent},
  { path: 'mensaje/:key',component:MensajeComponent},
  { path: 'pregunta-cuaderno',component: PreguntaCuadernoComponent},
  { path: 'inserte-usb',component: InserteUsbComponent},
  { path: 'descarga-archivos',component: DescargaArchivosComponent},
  { path: 'elegir-archivos',component:ElegirArchivosComponent},
  { path: 'elegir-especialidad',component:ElegirEspecialidadComponent},
  { path: 'rango-descarga/:page',component:RangoDescargaComponent},
  { path: 'encuesta',component:EncuestaComponent},
  { path: 'pregunta-final',component:PreguntaFinalComponent},
  { path: 'despedida-final',component:DespedidaFinalComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
