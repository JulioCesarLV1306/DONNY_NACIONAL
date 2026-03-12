import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { WordModel } from 'src/app/dto/word-model';
import { Expediente } from 'src/app/models/expediente';
import { Modulo } from 'src/app/models/modulo';
import { Persona } from 'src/app/models/persona';
import { AnnyangService, inicioCommand, paginatorCommand, seleccionarCommand, volverCommand } from 'src/app/services/annyang.service';
import { ExpedientesService } from 'src/app/services/expedientes.service';
import { InactividadService } from 'src/app/services/inactividad.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';
import { VideosService } from 'src/app/services/videos.service';

@Component({
  selector: 'app-lista-expedientes',
  templateUrl: './lista-expedientes.component.html',
  styleUrls: ['./lista-expedientes.component.scss']
})
export class ListaExpedientesComponent implements OnInit, OnDestroy, AfterViewInit {
  listaExpedientes: Expediente[] = [];
  selectedExpediente!: Expediente;
  tiempoEspera = 1500;
  subscription: Subscription

  paginatorDefaultSize: number = 8;
  paginatorTotalRecord: number = 0;

  paginatorDataObj:any;

  numberCurrentPage:number ;
  


  numero!: any;
  anio: any;
  cuaderno: any;
  especialidad: any;

  timeoutReload: any;
  timeoutRecord: any;

  @ViewChild('video_lista_expedientes') myVideo!: ElementRef;

  constructor(private videosService: VideosService, private router: Router, private expedientesServices: ExpedientesService, 
    private inactividadService:InactividadService, 
    private annyangService: AnnyangService, private memoriaService: MemoriaService, private mensajeService: MensajeService,private route: ActivatedRoute) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false; //RELOAD ON CHANGE PARAMS
    this.numberCurrentPage= +(this.route.snapshot.paramMap.get('page') || '0');
    this.subscription = new Subscription;
  }

  ngOnInit(): void {
    this.numero = this.memoriaService.getNumero();
    this.anio = this.memoriaService.getAnio();
    this.cuaderno = this.memoriaService.getCuaderno();
    this.especialidad = this.memoriaService.getEspecialidad();
    this.inactividadService.iniciarSupervision();
    this.subscribeWordDetected();
  }

  ngAfterViewInit(): void {
    this.cargarListaExpedientes(this.numberCurrentPage)
  }

  subscribeWordDetected() {
    console.log('LISTA-EXPEDIENTES SUSCRIBE')
    this.subscription = this.annyangService.wordDetected.subscribe((word: WordModel) => {
      if (word) {
        switch (word.command) {
          case 'seleccionarCommand':
            this.seleccionarExpediente(word.value);
            break;
         
        }
      }
    })
  }

  cargarListaExpedientes(page: any) {
    let persona: Persona = this.memoriaService.getPersona();
    let modulo: Modulo = this.memoriaService.getModulo();
    let idTipo= persona.tipo.idTipo
    if(idTipo==8){
      // Usar propiedades refactorizadas de Modulo
      this.expedientesServices.buscarExpedientesPorDNI(persona.dni,this.especialidad,persona.tipo.idTipo, persona.nombre, modulo.cPcIp, modulo.nIdModulo, modulo.cPcUsuario, page, this.paginatorDefaultSize).subscribe((data:any)=>{
        if(data){
          this.listaExpedientes = data.content;
          this.paginatorTotalRecord = data.totalElements;
          this.paginatorDataObj=data;
          this.paginatorDataObj.routerTo='lista-expedientes'
          if (data.totalElements > 1) {
            if (this.paginatorTotalRecord > this.paginatorDefaultSize) {
              this.annyangService.agregarComandos([inicioCommand,volverCommand,seleccionarCommand, paginatorCommand ]);
            } else {
              this.annyangService.agregarComandos([inicioCommand,volverCommand,seleccionarCommand]);
            }
            this.videosService.playVideo(this.myVideo);
            this.timeoutReload = this.videosService.iniciarContadorReload('lista-expedientes');
            this.timeoutRecord = this.annyangService.startToRecord('lista-expedientes');
          } else if (data.totalElements == 1) {
            this.seleccionarExpediente(1);
          } else if (data.totalElements == 0) {
            this.mensajeService.goToNoDatos();
          }
        }
      })
    }else{
      // Usar propiedades refactorizadas de Modulo
      this.expedientesServices.buscarExpedientes(this.numero, this.anio, this.cuaderno, this.especialidad, persona.dni, persona.tipo.idTipo, 
        persona.nombre, modulo.cPcIp, modulo.nIdModulo, modulo.cPcUsuario, page, this.paginatorDefaultSize).subscribe((data: any) => {
        if (data) {
          console.log(data)
          this.listaExpedientes = data.content;
          this.paginatorTotalRecord = data.totalElements;
          this.paginatorDataObj=data;
          this.paginatorDataObj.routerTo='lista-expedientes'
          if (data.totalElements > 1) {
            if (this.paginatorTotalRecord > this.paginatorDefaultSize) {
              this.annyangService.agregarComandos([inicioCommand,volverCommand,seleccionarCommand, paginatorCommand ]);
            } else {
              this.annyangService.agregarComandos([inicioCommand,volverCommand,seleccionarCommand]);
            }
            this.videosService.playVideo(this.myVideo);
            this.timeoutReload = this.videosService.iniciarContadorReload('lista-expedientes');
            this.timeoutRecord = this.annyangService.startToRecord('lista-expedientes');
          } else if (data.totalElements == 1) {
            this.seleccionarExpediente(1);
          } else if (data.totalElements == 0) {
            this.mensajeService.goToNoDatos();
          }
        
        }
      }, (error) => {
        this.mensajeService.goToErrorSistema(error.status,'dni-lectora');
      })
    }
  }

  seleccionarExpediente(index: number) {
    if (index > 0 && index <= this.listaExpedientes.length) {
      this.selectedExpediente = this.listaExpedientes[index - 1];
      this.memoriaService.guardarExpediente(this.selectedExpediente);
      setTimeout(() => {
        this.router.navigate([`elegir-archivos`]).then(() => {
          window.location.reload();
        });
      }, this.tiempoEspera);
    }
  }

  paginate(event: any) {
    this.cargarListaExpedientes(event.page)
  }

  ngOnDestroy() {
    console.log('LISTA-EXPEDIENTES UNSUSCRIBE')
    this.subscription.unsubscribe();
    clearTimeout(this.timeoutRecord);
    clearTimeout(this.timeoutReload);
    this.annyangService.pauseRecord();
  }

}
