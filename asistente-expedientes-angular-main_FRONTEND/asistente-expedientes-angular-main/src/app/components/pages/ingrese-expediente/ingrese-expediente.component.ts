import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { WordModel } from 'src/app/dto/word-model';
import { AnnyangService, dictadoExpedienteCommand, inicioCommand, volverCommand } from 'src/app/services/annyang.service';
import { InactividadService } from 'src/app/services/inactividad.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';
import { VideosService } from 'src/app/services/videos.service';
import { arrayRouterTo } from 'src/app/shared/var.constant';

@Component({
  selector: 'app-ingrese-expediente',
  templateUrl: './ingrese-expediente.component.html',
  styleUrls: ['./ingrese-expediente.component.scss']
})
export class IngreseExpedienteComponent implements OnInit, OnDestroy, AfterViewInit {

  objVideo: any;
  palabraDetectada: any;
  tiempoEspera = 500;

  subscription: Subscription

  rango_min_anio: number = 2014;
  rango_max_anio: number = new Date().getFullYear();

  timeoutReload:any;
  timeoutRecord:any;

  keyActual:string='';

  numero:any;
  cuaderno:any;
  anio:any;



  @ViewChild('video_ingrese_expediente') myVideo!: ElementRef;

  constructor(private videosService: VideosService, private annyangService: AnnyangService, private memoriaService: MemoriaService,
    private mensajeService: MensajeService, private router: Router, private route: ActivatedRoute, private inactividadService:InactividadService) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false; //RELOAD ON CHANGE PARAMS
    this.subscription = new Subscription;
  }
  ngAfterViewInit(): void {
    this.videosService.playVideo(this.myVideo);
    this.timeoutReload=this.videosService.iniciarContadorReload(this.objVideo.key);
    this.timeoutRecord=this.annyangService.startToRecord(this.objVideo.key);
  }

  ngOnInit(): void {
    
    this.annyangService.agregarComandos([inicioCommand,volverCommand,dictadoExpedienteCommand]);
    this.route.params.subscribe(routeParams => {
      this.updateObjVideo(routeParams.key);
      this.keyActual=routeParams.key;
      switch (routeParams.key) {
        case 'numero':
          this.palabraDetectada='XXXXX'
          break;
        case 'anio':
          this.palabraDetectada='XXXX'
          break;
        case 'cuaderno':
          this.palabraDetectada='XXX'
          break;
      }
    });

    this.subscribeWordDetected();
    this.actualizarHTML();
    this.numero=this.memoriaService.getNumero();
    this.anio=this.memoriaService.getAnio();
    this.cuaderno=this.memoriaService.getCuaderno();
    this.inactividadService.iniciarSupervision();
  }


  seleccionarValor(valor: any) {
    this.videosService.stopVideo(this.myVideo);
    this.palabraDetectada = valor;
    if (this.validarNumero(this.palabraDetectada)) {
      this.setVariableMemoria(this.palabraDetectada);
      setTimeout(() => {
        this.router.navigate([this.objVideo.routerTo]);
      }, this.tiempoEspera);
    } else {
      this.mensajeService.goToValorIncorrecto(valor, `ingrese-expediente/${this.objVideo.key}`);
    }

  }

  actualizarHTML() {
    setInterval(() => {
      this.palabraDetectada = this.palabraDetectada;
    }, 500);
  }

  subscribeWordDetected() {
    console.log('INGRESE-EXPEDIENTES SUSCRIBE')
    this.subscription = this.annyangService.wordDetected.subscribe((word: WordModel) => {
      if (word) {
        switch (word.command) {
          case 'dictadoExpedienteCommand':
            this.seleccionarValor(word.value);
            break;
        }
      }
    })
  }

  updateObjVideo(key: any) {
    this.objVideo = arrayRouterTo.find((element: any) => {
      return element.key == key;
    });
  }

  setVariableMemoria(valor: number) {
    switch (this.objVideo.key) {
      case 'numero':
        this.memoriaService.setNumero(valor);
        break;
      case 'anio':
        this.memoriaService.setAnio(valor);
        break;
      case 'cuaderno':
        this.memoriaService.setCuaderno(valor);
        break;
    }

  }

  validarNumero(valor: number): boolean {
    switch (this.objVideo.key) {
      case 'numero':
        return valor > 0 && valor <=99999 ;
      case 'anio':
        return valor >= this.rango_min_anio && valor <= this.rango_max_anio;
      case 'cuaderno':
        return valor >= 0 && valor <=999;
      default:
        return false;
    }
  }

 

  ngOnDestroy() {
    console.log('INGRESE-EXPEDIENTES UNSUSCRIBE')
    clearTimeout(this.timeoutRecord);
    clearTimeout(this.timeoutReload);
    this.annyangService.pauseRecord();
    this.subscription.unsubscribe()
  }



}

