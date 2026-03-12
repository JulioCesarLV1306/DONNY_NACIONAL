import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AnnyangService, inicioCommand, si_noCommand, volverCommand } from 'src/app/services/annyang.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { WordModel } from 'src/app/dto/word-model';
import { VideosService } from 'src/app/services/videos.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { InactividadService } from 'src/app/services/inactividad.service';

@Component({
  selector: 'app-pregunta-cuaderno',
  templateUrl: './pregunta-cuaderno.component.html',
  styleUrls: ['./pregunta-cuaderno.component.scss']
})
export class PreguntaCuadernoComponent implements OnInit, OnDestroy, AfterViewInit  {

  goToCuaderno:number=-1;
  subscription: Subscription;
  tiempoEspera = 500;

  timeoutReload:any;
  timeoutRecord:any;

  numero:any;
  anio:any;

  @ViewChild('video_pregunta_cuaderno') myVideo!: ElementRef;

  constructor(private annyangService: AnnyangService, private router: Router, private videosService: VideosService,
     private memoriaService: MemoriaService, private inactividadService:InactividadService) { 
    this.subscription= new Subscription;
   
  }
  

  ngOnInit(): void {
    this.inactividadService.iniciarSupervision();
    this.annyangService.agregarComandos([inicioCommand,volverCommand,si_noCommand]);
    this.subscribeWordDetected();
    this.actualizarHTML();
    this.numero=this.memoriaService.getNumero();
    this.anio=this.memoriaService.getAnio();
  }

  ngAfterViewInit(): void {
    this.videosService.playVideo(this.myVideo);
    this.timeoutReload=this.videosService.iniciarContadorReload('pregunta-cuaderno');
    this.timeoutRecord=this.annyangService.startToRecord('pregunta-cuaderno');
  }

  subscribeWordDetected() {
  console.log('PREGUNTA-CUADERNO SUSCRIBE')
   this.subscription= this.annyangService.wordDetected.subscribe((word: WordModel) => {
     if(word){
       switch(word.command){
         case 'si_noCommand':
          this.seleccionarRespuesta(word.value);
           break;
       }
     }
    })
  }

  actualizarHTML() {
    setInterval(() => { /*ACTUALIZA HTML */
      this.goToCuaderno = this.goToCuaderno;
    }, 500);
  }

  eventEleccion(){
    console.log(this.goToCuaderno)
  }



  seleccionarRespuesta(valor: number){
    this.goToCuaderno = valor;
    setTimeout(() => {
      if(this.goToCuaderno == 1) {
        this.router.navigate(['ingrese-expediente/cuaderno']);
      } else if (this.goToCuaderno == 0){
        this.router.navigate(['elegir-especialidad']);
      }
      
    }, this.tiempoEspera);
   }

   ngOnDestroy() {
    console.log('PREGUNTA-CUADERNO UNSUSCRIBE')
    clearTimeout(this.timeoutRecord);
    clearTimeout(this.timeoutReload);
    this.annyangService.pauseRecord();
    this.subscription.unsubscribe()
  }

}
