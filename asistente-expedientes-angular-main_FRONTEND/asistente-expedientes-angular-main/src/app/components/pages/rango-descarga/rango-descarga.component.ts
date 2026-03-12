import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { faLongArrowAltRight } from '@fortawesome/free-solid-svg-icons';
import { Subscription } from 'rxjs';
import { WordModel } from 'src/app/dto/word-model';
import { Expediente } from 'src/app/models/expediente';
import { Fecha } from 'src/app/models/fecha';
import { AnnyangService, descargarCommand, inicioCommand, paginatorCommand, seleccionarCommand, seleccionarRangoCommand, volverCommand } from 'src/app/services/annyang.service';
import { FechasService } from 'src/app/services/fechas.service';
import { InactividadService } from 'src/app/services/inactividad.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';
import { VideosService } from 'src/app/services/videos.service';
import { decodificarRango } from 'src/app/shared/util';

@Component({
  selector: 'app-rango-descarga',
  templateUrl: './rango-descarga.component.html',
  styleUrls: ['./rango-descarga.component.scss']
})
export class RangoDescargaComponent implements OnInit, OnDestroy, AfterViewInit {

  listaFechas:Fecha[]=[];
  checkedListaFechas: boolean[] = [];

  paginatorDefaultSize: number=6;
  paginatorTotalRecord:number=0;
  paginatorTotalPages:number=0;

  numberCurrentPage:number ;

  paginatorDataObj:any
  
  tiempoEspera=1500;

  expedienteActual!:Expediente;
  eleccionActual!: string;

  subscription: Subscription;

  timeoutReload:any;
  timeoutRecord:any;

  
  faArrow = faLongArrowAltRight;

  @ViewChild('video_rango_descarga') myVideo!: ElementRef; 

  constructor(private memoriaService: MemoriaService, private annyangService: AnnyangService,
     private mensajeService: MensajeService, private inactividadService:InactividadService,
    private router: Router, private fechasService: FechasService, private videosService: VideosService,private route: ActivatedRoute) {

      this.router.routeReuseStrategy.shouldReuseRoute = () => false; //RELOAD ON CHANGE PARAMS
      this.numberCurrentPage= +(this.route.snapshot.paramMap.get('page') || '0');
    this.subscription = new Subscription;
  }

  ngOnInit(): void { 
    this.annyangService.agregarComandos([inicioCommand,volverCommand,seleccionarCommand,seleccionarRangoCommand,paginatorCommand,descargarCommand]);
    this.expedienteActual=this.memoriaService.getExpediente();
    this.subscribeWordDetected();
    this.actualizarHTML();
    this.inactividadService.iniciarSupervision();
    //this.cargarListaFechas(this.numberCurrentPage);
  }

  ngAfterViewInit(): void {
    this.cargarListaFechas(this.numberCurrentPage);
  }

  ngOnDestroy(): void {
    console.log('RANGO-DESCARGA UNSUSCRIBE')
    this.subscription.unsubscribe();
    clearTimeout(this.timeoutRecord);
    clearTimeout(this.timeoutReload);
    this.annyangService.pauseRecord();
  }

  actualizarHTML() {
    setInterval(() => {
    }, 500);
  }

  subscribeWordDetected() {
    console.log('RANGO-DESCARGA SUSCRIBE')
    this.subscription = this.annyangService.wordDetected.subscribe((word: WordModel) => {
      if (word) {
        switch (word.command) {
          case 'seleccionarCommand':
            this.seleccionarFecha(word.value);
            break;
          case 'seleccionarRangoCommand':
            const rango_arr: number[] = decodificarRango(word.value);
            this.seleccionarRangoFechas(rango_arr[0], rango_arr[1]);
            break;
          case 'descargarCommand':
            this.irAInserteUSB();
            break;
          case 'volverCommand':
            console.log('Borrando fechas')
            for (let i = 0; i < this.paginatorTotalPages; i++) {
              this.memoriaService.borrarRangoFechasPagina(i)
            }
            break;
        }
      }
    })
  }

  reproducirVideo(){
    this.videosService.playVideo(this.myVideo);
    this.timeoutReload=this.videosService.iniciarContadorReload('rango-descarga');
    this.timeoutRecord=this.annyangService.startToRecord('rango-descarga');
  }

 

  cargarListaFechas(page:number){
    switch (this.memoriaService.getEleccionArchivos()[0].key) {
      case 'actas':
       
        this.fechasService.getFechasActas(this.expedienteActual.nunico, this.expedienteActual.nincidente,page, this.paginatorDefaultSize).subscribe((data:any)=>{
          if(data){
            this.cargarData(data);
            this.eleccionActual='ACTAS'
          }
        },(error)=>{
          this.mensajeService.goToErrorSistema(error,'lista-expedientes/0');
        })
        break;
      case 'videos':
     
        this.fechasService.getFechasVideos(this.expedienteActual.nunico, this.expedienteActual.nincidente,page, this.paginatorDefaultSize).subscribe((data:any)=>{
          if(data){
            this.cargarData(data);
            this.eleccionActual='VIDEOS'
          }
        },(error)=>{
          this.mensajeService.goToErrorSistema(error,'lista-expedientes/0');
        })
        break;
      case 'resoluciones':
      
        this.fechasService.getFechasResoluciones(this.expedienteActual.nunico, this.expedienteActual.nincidente,page, this.paginatorDefaultSize).subscribe((data:any)=>{
          if(data){ 
            this.cargarData(data);
            this.eleccionActual='RESOLUCIONES'
          }
        },(error)=>{
          this.mensajeService.goToErrorSistema(error,'lista-expedientes/0');
        })
        break;
      case 'documentosdigitalizados':
     
        this.fechasService.getFechasDigitalizados(this.expedienteActual.nunico, this.expedienteActual.nincidente,page, this.paginatorDefaultSize).subscribe((data:any)=>{
          if(data){
            this.cargarData(data);
            this.eleccionActual='DOCUMENTOS DIGITALIZADOS'
          }
        },(error)=>{
          this.mensajeService.goToErrorSistema(error,'lista-expedientes/0');
        })
        break;
      default:
        this.mensajeService.goToErrorSistema(500,'lista-expedientes/0');
        break;
    }
  }

  cargarData(data: any) {
    console.log(data)
    this.paginatorDataObj=data
    this.paginatorDataObj.routerTo='rango-descarga'
    this.listaFechas = data.content; 
    this.paginatorTotalRecord = data.totalElements;
    this.paginatorTotalPages=data.totalPages;

    if(this.listaFechas.length == 1 && this.numberCurrentPage == 0){
      this.seleccionarFecha(1);
      this.irAInserteUSB();
    } else{
      this.cargarPreSeleccionadosPagina();
      this.reproducirVideo();
    }
    
  }

  cargarPreSeleccionadosPagina(){
    this.checkedListaFechas = Array(this.listaFechas.length).fill(false) ;
    const listRangFec = this.memoriaService.getRangoFechasPagina(this.numberCurrentPage);
    for (let i = 0; i < listRangFec.length; i++) {
      for(let j = 0; j < this.listaFechas.length; j++){
        if(JSON.stringify(listRangFec[i]) == JSON.stringify(this.listaFechas[j])){
          this.checkedListaFechas[j]=true;
        }
      }
    }
  }

  seleccionarFecha(index: number){
    if(index > 0 && index <= this.listaFechas.length){
      this.checkedListaFechas[index - 1] = !this.checkedListaFechas[index - 1]
      this.memoriaService.guardarRangoFechasPagina(this.numberCurrentPage,this.getListaFechasPagina());
    }
  }

  seleccionarRangoFechas(index1:number, index2:number){
    if(index1 > 0 && index1 <= this.listaFechas.length && index2 > 0 && index2 <= this.listaFechas.length){
      for (let i = index1-1; i < index2; i++) {
        this.checkedListaFechas[i]=true;
      }
      
      this.memoriaService.guardarRangoFechasPagina(this.numberCurrentPage,this.getListaFechasPagina());
    }
  }

  getListaFechasFinal():Fecha[]{
    const listaFinal:Fecha[]=[];
    for (let i = 0; i < this.paginatorTotalPages; i++) { //
      let listaPagina = this.memoriaService.getRangoFechasPagina(i);
      for (let j = 0; j < listaPagina.length; j++) {
        listaFinal.push(listaPagina[j])
      }
      
    }
    return listaFinal;
  }

  getListaFechasPagina():Fecha[]{
    const listaFechaPagina:Fecha[]=[];
    for (let i = 0; i < this.listaFechas.length; i++) {
      if(this.checkedListaFechas[i]) {listaFechaPagina.push(this.listaFechas[i]); }
    }
    return listaFechaPagina;
  }

  irAInserteUSB(){
    const lis_fechas = this.getListaFechasFinal();
    console.log(lis_fechas)
    if(lis_fechas.length>0){
      this.memoriaService.guardarRangoFechasFinal(lis_fechas);
      setTimeout(() => {
        this.router.navigate(['inserte-usb']);
      }, this.tiempoEspera);
    } else{
      this.mensajeService.goToNoSeleccion('ITEM','rango-descarga/0');
    }
    
  }


}
