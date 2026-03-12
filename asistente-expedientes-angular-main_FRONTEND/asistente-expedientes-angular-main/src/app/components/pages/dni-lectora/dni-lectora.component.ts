import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { PersonasService } from 'src/app/services/personas.service';
import { MensajeService } from 'src/app/services/mensaje.service';
import { Persona } from 'src/app/models/persona';
import { MemoriaService } from 'src/app/services/memoria.service';
import { VideosService } from 'src/app/services/videos.service';
import { AnnyangService } from 'src/app/services/annyang.service';
import { DriveService } from 'src/app/services/drive.service';
import { Modulo } from 'src/app/models/modulo';
import { MessageService } from 'primeng/api';
import { VERSION_APP } from 'src/app/shared/var.constant';

@Component({
  selector: 'app-dni-lectora',
  templateUrl: './dni-lectora.component.html',
  styleUrls: ['./dni-lectora.component.scss']
})
export class DniLectoraComponent implements OnInit, AfterViewInit, OnDestroy {

  dniIngresado: string = '';
  moduloExist:boolean = false
  version=VERSION_APP

  @ViewChild('video_dni_lectora') myVideo!: ElementRef;
  @ViewChild('inputDni') inputDni!: ElementRef;

  inputDniThread:any;


  constructor(private annyangService: AnnyangService, private videosService: VideosService, private personasService: PersonasService, private router:Router,
    private driveService: DriveService, private memoriaService: MemoriaService, private mensajeService: MensajeService, private messageService: MessageService) {
      this.router.routeReuseStrategy.shouldReuseRoute = () => false; //RELOAD ON CHANGE PARAMS
    }
 

  ngOnInit(): void {
    this.memoriaService.limpiarMemoria();
    this.annyangService.recordModoPrueba();
    this.setFocusLoop();
    this.driveService.verificarCliente().subscribe((data: any) => {
      if (data) {
        let modulo: Modulo = data;
        this.memoriaService.guardarModulo(modulo);
        this.moduloExist=true
      }
    }, (error) => {
      console.log(error)>
      this.messageService.add({ severity: 'error', summary: 'Cliente Drive no encontrado!', detail: 'Contactarse con el administrador',sticky:true, closable:false });
    })
  }

  ngAfterViewInit(): void {
    this.videosService.playVideo(this.myVideo);
  }

  buscarPorDNI() {
    this.videosService.stopVideo(this.myVideo);
    let modulo: Modulo = this.memoriaService.getModulo();
    if(this.moduloExist) {
      if(this.dniIngresado.length >= 8){
        // Usar propiedades refactorizadas de Modulo
        this.personasService.validarDNI(this.dniIngresado, modulo.cPcIp, modulo.cPcUsuario).subscribe((data: any) => {
          if (data) {
            let persona: Persona = data;
            this.memoriaService.guardarPersona(persona);
            this.mensajeService.goToBienvenido(`${persona.nombre} (${persona.tipo.nombre})`);
            this.memoriaService.setInactividadRuta('/start');
          }
        }, (error) => {
          if (error.status == 404) {
            this.mensajeService.goToDniNoValido(this.dniIngresado);
          } else {
            this.mensajeService.goToErrorSistema(error.status,'dni-lectora');
          }
        });
      }else{
        this.mensajeService.goToDniNoValido(this.dniIngresado);
      }
    }else{
      this.mensajeService.goToNoCliente();
    }

  }
  
  setFocusLoop(){
   this.inputDniThread= setTimeout(() => {
      this.inputDni.nativeElement.focus();
      this.setFocusLoop();
    }, 1000);
    
  }

  ngOnDestroy(): void {
    this.annyangService.pauseRecord();
   clearTimeout(this.inputDniThread);
  }


}
