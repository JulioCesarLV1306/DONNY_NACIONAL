import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { faLongArrowAltRight } from '@fortawesome/free-solid-svg-icons';
import { Subscription } from 'rxjs';
import { EleccionModel } from 'src/app/dto/eleccion-model';
import { EleccionRequest } from 'src/app/dto/eleccion-request';
import { WordModel } from 'src/app/dto/word-model';
import { Expediente } from 'src/app/models/expediente';
import { AnnyangService, eleccionArchivosCommand, inicioCommand, paginatorCommand, volverCommand } from 'src/app/services/annyang.service';
import { ExpedientesService } from 'src/app/services/expedientes.service';
import { InactividadService } from 'src/app/services/inactividad.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';
import { VideosService } from 'src/app/services/videos.service';
import { arrayEleccion } from 'src/app/shared/var.constant';

@Component({
  selector: 'app-elegir-archivos',
  templateUrl: './elegir-archivos.component.html',
  styleUrls: ['./elegir-archivos.component.scss']
})
export class ElegirArchivosComponent implements OnInit, OnDestroy, AfterViewInit {

  listaEleccion: EleccionModel[] = [];
  opcionSelected!: EleccionModel;
  //checkedListaEleccion: boolean[] = [];
  subscription: Subscription;

  expedienteActual!: Expediente;

  timeoutReload:any;
  timeoutRecord:any;

  faArrow = faLongArrowAltRight;

  tiempoEspera = 500;


  @ViewChild('video_elegir_archivos') myVideo!: ElementRef;

  constructor(private videosService: VideosService, private router: Router,private annyangService: AnnyangService, 
    private memoriaService: MemoriaService, private inactividadService:InactividadService,
    private expedientesServices:ExpedientesService, private mensajeService: MensajeService) { 
    this.subscription = new Subscription;
  }


  ngOnInit(): void {
    this.annyangService.agregarComandos([inicioCommand,volverCommand,eleccionArchivosCommand]);
    this.listaEleccion=arrayEleccion;
    this.expedienteActual=this.memoriaService.getExpediente();
    this.inactividadService.iniciarSupervision();
    this.subscribeWordDetected();
    this.actualizarHTML();
  }

  
  actualizarHTML() {
    setInterval(() => {
    }, 500);
  }


  ngAfterViewInit(): void {
    this.cargarConteos();
  }

  ngOnDestroy(): void {
    console.log('ELEGIR-ARCHIVOS UNSUSCRIBE')
    clearTimeout(this.timeoutRecord);
    clearTimeout(this.timeoutReload);
    this.annyangService.pauseRecord();
    this.subscription.unsubscribe()
  }

  
  cargarConteos(){
    let listaConteo:EleccionRequest[];
    this.expedientesServices.contarArchivos(this.expedienteActual.nunico,this.expedienteActual.nincidente).subscribe((data:any)=>{
      if(data){
        console.log(data)
        listaConteo=data;
        this.listaEleccion.forEach(element => {
          const eleccionRequest = listaConteo.find(ele =>{
            return ele.key === element.key ;
          })
          element.conteo=eleccionRequest?.conteo || 0;
          element.active=element.conteo > 0;
        });

        //this.listaEleccion = this.listaEleccion.filter(l => { return l.conteo > 0 })

        if(this.listaEleccion.length > 0){
          this.videosService.playVideo(this.myVideo);
          this.timeoutReload=this.videosService.iniciarContadorReload('elegir-archivos');
          this.timeoutRecord=this.annyangService.startToRecord('elegir-archivos');
        } else{
          this.mensajeService.goToNoDatos();
        }

      }

     
    },(error)=>{
      this.mensajeService.goToErrorSistema(error.status,'dni-lectora');
    })
  }


  subscribeWordDetected() {
    console.log('ELEGIR-ARCHIVOS SUSCRIBE')
    this.subscription = this.annyangService.wordDetected.subscribe((word: WordModel) => {
      if (word) {
        switch (word.command) {
          case 'eleccionArchivosCommand':
            this.seleccionarEleccion(word.value);
            /*if (word.value == 'siguiente'){
              this.avanzarPagina();
            }else{
              
            }*/
            break;
        }
      }
    })
  }

  seleccionarEleccion(valor: string) {
    const index:number = this.getIndexEleccion(valor);
    if (index > 0 && index <= this.listaEleccion.length && this.listaEleccion[index-1].active) {
      this.opcionSelected = this.listaEleccion[index - 1];
      setTimeout(() => {
        this.avanzarPagina();
      }, this.tiempoEspera);
      
      //this.listaEleccion[index-1].checked = !this.listaEleccion[index-1].checked
      //this.updateDisabled(index-1);
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

  avanzarPagina(){
    if(this.verificarSeleccionados()){
      //const listaSelecccionados:EleccionModel[] = this.getListaSeleccionados();
      const listaSelecccionados = [this.opcionSelected]
      this.memoriaService.guardarEleccionArchivos(listaSelecccionados);
      if(listaSelecccionados.length == 1 && listaSelecccionados[0].key=='depositos'){
        this.router.navigate(['inserte-usb']);
      }else{
        this.router.navigate(['rango-descarga/0']);
      }
      
    }else{
      this.mensajeService.goToNoSeleccion("CASILLERO","elegir-archivos");
    }
  }

  verificarSeleccionados():boolean{
    /*let flag = false;
    this.listaEleccion.forEach(element => {
      if(element.checked ){
        flag=true
      }
    });
    return flag;*/
    return this.opcionSelected != undefined;
  }

  /*getListaSeleccionados():EleccionModel[]{
    const listaSeleccionados:EleccionModel[]=[];
    for (let i = 0; i < this.listaEleccion.length; i++) {
      if(this.listaEleccion[i].checked==true){
        listaSeleccionados.push(this.listaEleccion[i])
      }
    }
    return listaSeleccionados;
  }*/

  /*updateDisabled(index:number){
    const eleccion = this.listaEleccion[index];
    for (let i = 0; i < this.listaEleccion.length; i++) {
      const i_elec = this.listaEleccion[i];
      if(i_elec.grupo_key != eleccion.grupo_key) {
        this.listaEleccion[i].checked=false;
      }
    }
  }*/


}
