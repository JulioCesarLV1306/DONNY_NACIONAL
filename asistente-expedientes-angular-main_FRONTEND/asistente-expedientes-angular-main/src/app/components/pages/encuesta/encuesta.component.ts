import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { EleccionModel } from 'src/app/dto/eleccion-model';
import { WordModel } from 'src/app/dto/word-model';
import { Encuesta } from 'src/app/models/encuesta';
import { AnnyangService, inicioCommand, satisfaccionCommand } from 'src/app/services/annyang.service';
import { EncuestaService } from 'src/app/services/encuesta.service';
import { InactividadService } from 'src/app/services/inactividad.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';
import { VideosService } from 'src/app/services/videos.service';
import { arraySatisfaccion } from 'src/app/shared/var.constant';

@Component({
  selector: 'app-encuesta',
  templateUrl: './encuesta.component.html',
  styleUrls: ['./encuesta.component.scss']
})
export class EncuestaComponent implements OnInit, AfterViewInit, OnDestroy {

  
  listaSatisfaccion: EleccionModel[] = [];
  timeoutReload:any;
  timeoutRecord:any;
  
  subscription: Subscription;

  
  @ViewChild('video_encuesta') myVideo!: ElementRef;

  constructor(private annyangService:AnnyangService, private inactividadService:InactividadService, private videosService: VideosService,
    private mensajeService:MensajeService, private encuestaService:EncuestaService, private memoriaService:MemoriaService) {
    this.subscription = new Subscription;
   }
  

  ngOnInit(): void {
    this.listaSatisfaccion=arraySatisfaccion;
    this.annyangService.agregarComandos([inicioCommand, satisfaccionCommand]);
    //this.timeoutRecord=this.annyangService.startToRecord('encuesta');
    this.inactividadService.iniciarSupervision();
    this.subscribeWordDetected();
  }

  
  subscribeWordDetected() {
    console.log('ENCUESTA SUSCRIBE')
    this.subscription = this.annyangService.wordDetected.subscribe((word: WordModel) => {
      if (word) {
        switch (word.command) {
          case 'satisfaccionCommand':
            this.enviarEncuesta(word.value)
            break;
        }
      }
    })
  }

  enviarEncuesta(valor:any){
    let modulo=this.memoriaService.getModulo();
    let persona=this.memoriaService.getPersona();
    const nIdModulo = (modulo as any)?.nIdModulo ?? (modulo as any)?.idModulo;

    if (!nIdModulo) {
      console.error('No se encontró nIdModulo/idModulo en memoria para registrar encuesta', modulo);
      this.mensajeService.goToDespedidaFinal();
      return;
    }

    // Usar nueva estructura de Encuesta con nomenclatura refactorizada
    // NOTA: Por ahora mantenemos compatibilidad temporal con campos deprecados
    let encuesta:Encuesta={
      nIdModulo,
      nIdUsuario: 0, // TODO: Implementar búsqueda/creación de usuario antes de enviar
      nCalificacion: valor,
      // Campos deprecados - mantener por compatibilidad temporal
      idModulo: nIdModulo,
      dniSece: persona.dni,
      nombreSece: persona.nombre,
      calificacion: valor
    };
    
    // Usar propiedades refactorizadas de Modulo
    this.encuestaService.crear(encuesta, modulo.cPcIp, modulo.cPcUsuario).subscribe(data=>{
      this.mensajeService.goToDespedidaFinal();
    },er=>{
      this.mensajeService.goToDespedidaFinal();
    })
  }

 
  ngAfterViewInit(): void {
    this.videosService.playVideo(this.myVideo);
    this.timeoutReload=this.videosService.iniciarContadorReload('encuesta');
    this.timeoutRecord=this.annyangService.startToRecord('encuesta');
  }

  ngOnDestroy(): void {
    console.log('ENCUESTA UNSUSCRIBE')
    clearTimeout(this.timeoutRecord);
    clearTimeout(this.timeoutReload);
    this.annyangService.pauseRecord();
    this.subscription.unsubscribe()
  }


}
