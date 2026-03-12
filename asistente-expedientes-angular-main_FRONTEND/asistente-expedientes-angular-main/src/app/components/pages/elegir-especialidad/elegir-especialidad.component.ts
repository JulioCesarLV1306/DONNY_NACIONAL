import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { EleccionModel } from 'src/app/dto/eleccion-model';
import { WordModel } from 'src/app/dto/word-model';
import { Expediente } from 'src/app/models/expediente';
import { AnnyangService, eleccionArchivosCommand, eleccionEspecialidadCommand, inicioCommand, volverCommand } from 'src/app/services/annyang.service';
import { ExpedientesService } from 'src/app/services/expedientes.service';
import { InactividadService } from 'src/app/services/inactividad.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';
import { VideosService } from 'src/app/services/videos.service';
import { arrayEspecialidad } from 'src/app/shared/var.constant';

@Component({
  selector: 'app-elegir-especialidad',
  templateUrl: './elegir-especialidad.component.html',
  styleUrls: ['./elegir-especialidad.component.scss']
})
export class ElegirEspecialidadComponent implements OnInit, OnDestroy, AfterViewInit {

  tiempoEspera = 1000;

  listaEleccion: EleccionModel[] = [];
  especialidadSelected!: EleccionModel;
  subscription: Subscription;

  timeoutReload:any;
  timeoutRecord:any;

  numero:any='X';
  anio:any='X';
  cuaderno:any='X';

  idTipo:number=0;
  
  @ViewChild('video_elegir_archivos') myVideo!: ElementRef;

  constructor(private videosService: VideosService, private router: Router,private annyangService: AnnyangService, 
    private memoriaService: MemoriaService, private inactividadService:InactividadService) { 
    this.subscription = new Subscription;
  }
 

  ngOnInit(): void {
    this.idTipo=this.memoriaService.getPersona().tipo.idTipo
    if(this.idTipo==8){
      this.annyangService.agregarComandos([inicioCommand,eleccionEspecialidadCommand]);
    }else{
      this.annyangService.agregarComandos([inicioCommand,volverCommand,eleccionEspecialidadCommand]);
    }
    
    this.listaEleccion=arrayEspecialidad;
    this.subscribeWordDetected();
    let num=this.memoriaService.getNumero();
    let an=this.memoriaService.getAnio();
    let cua= this.memoriaService.getCuaderno();
   
    if(cua!=null){
    this.cuaderno=cua;
    }
    if(num!=null){
      this.numero=num;
    }
    if(an!=null){
      this.anio=an;
    }
    this.inactividadService.iniciarSupervision();
  } 
  
  
  ngAfterViewInit(): void {
    this.videosService.playVideo(this.myVideo);
    this.timeoutReload=this.videosService.iniciarContadorReload('elegir-especialidad');
    this.timeoutRecord=this.annyangService.startToRecord('elegir-especialidad');
  }
  
  subscribeWordDetected() {
    console.log('ELEGIR-ESPECIALIDAD SUSCRIBE')
    this.subscription = this.annyangService.wordDetected.subscribe((word: WordModel) => {
      if (word) {
        switch (word.command) {
          case 'eleccionEspecialidadCommand':
            this.seleccionarEspecialidad(word.value);
            break;
        }
      }
    })
  }

  seleccionarEspecialidad(valor: string){
    const index:number = this.getIndexEleccion(valor);
    if (index > 0 && index <= this.listaEleccion.length) {
      this.especialidadSelected = this.listaEleccion[index-1];
      this.memoriaService.setEspecialidad(this.especialidadSelected.key);
      setTimeout(() => {
        this.router.navigate([`lista-expedientes/0`]).then(() => {
          window.location.reload();
        });
      }, this.tiempoEspera);
     
    }
  }
  
  getIndexEleccion(key: string):number {
    let index = -1;
    for (let i = 0; i < this.listaEleccion.length; i++) {
      if(this.listaEleccion[i].titulo.toLowerCase() == key) {
        index = i;
        break;
      }
    }
    return index+1;
  }


  ngOnDestroy(): void {
    console.log('ELEGIR-ESPECIALIDAD UNSUSCRIBE')
    this.subscription.unsubscribe();
    clearTimeout(this.timeoutRecord);
    clearTimeout(this.timeoutReload);
    this.annyangService.pauseRecord();
  }


}
