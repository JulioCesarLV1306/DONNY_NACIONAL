import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { PrimengModule } from './modules/primeng.module';
import { HTTP_INTERCEPTORS} from '@angular/common/http'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DniLectoraComponent } from './components/pages/dni-lectora/dni-lectora.component';
import { MensajeComponent } from './components/pages/mensaje/mensaje.component';
import { IngreseExpedienteComponent } from './components/pages/ingrese-expediente/ingrese-expediente.component';
import { ListaExpedientesComponent } from './components/pages/lista-expedientes/lista-expedientes.component';
import { InserteUsbComponent } from './components/pages/inserte-usb/inserte-usb.component';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { SpinnerInterceptor } from './interceptors/spinner.interceptor';
import { PreguntaCuadernoComponent } from './components/pages/pregunta-cuaderno/pregunta-cuaderno.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { DescargaArchivosComponent } from './components/pages/descarga-archivos/descarga-archivos.component';
import { ElegirArchivosComponent } from './components/pages/elegir-archivos/elegir-archivos.component';
import { RangoDescargaComponent } from './components/pages/rango-descarga/rango-descarga.component';
import { MessageService } from 'primeng/api';
import { ElegirEspecialidadComponent } from './components/pages/elegir-especialidad/elegir-especialidad.component';
import { PaginatorComponent } from './components/paginator/paginator.component';
import { VolverComponent } from './components/volver/volver.component';
import { EncuestaComponent } from './components/pages/encuesta/encuesta.component';
import { DespedidaFinalComponent } from './components/pages/despedida-final/despedida-final.component';
import { PreguntaFinalComponent } from './components/pages/pregunta-final/pregunta-final.component';
@NgModule({
  declarations: [
    AppComponent,
    DniLectoraComponent,
    MensajeComponent,
    IngreseExpedienteComponent,
    ListaExpedientesComponent,
    InserteUsbComponent,
    SpinnerComponent,
    PreguntaCuadernoComponent,
    DescargaArchivosComponent,
    ElegirArchivosComponent,
    RangoDescargaComponent,
    ElegirEspecialidadComponent,
    PaginatorComponent,
    VolverComponent,
    EncuestaComponent,
    DespedidaFinalComponent,
    PreguntaFinalComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    PrimengModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    FontAwesomeModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS,useClass:SpinnerInterceptor, multi:true},
    {provide: MessageService}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
