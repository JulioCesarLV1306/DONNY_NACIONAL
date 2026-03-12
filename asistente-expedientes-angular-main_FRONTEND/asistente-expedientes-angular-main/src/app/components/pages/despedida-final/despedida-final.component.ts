import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Modulo } from 'src/app/models/modulo';
import { Persona } from 'src/app/models/persona';
import { BitacoraService } from 'src/app/services/bitacora.service';
import { DriveService } from 'src/app/services/drive.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { VideosService } from 'src/app/services/videos.service';

@Component({
  selector: 'app-despedida-final',
  templateUrl: './despedida-final.component.html',
  styleUrls: ['./despedida-final.component.scss']
})
export class DespedidaFinalComponent implements OnInit {

  mensaje: string = 'ESPERA UN MOMENTO'
  timeoutReload: any;
  timeoutRecord: any;

  subscription: Subscription;

  success: boolean = false
  porcentaje: number = 50

  @ViewChild('video_despedida_final') myVideo!: ElementRef;


  constructor(private router: Router, private videosService: VideosService, private memoriaService: MemoriaService, private bitacoraService: BitacoraService,
    private driveService: DriveService) {
    this.subscription = new Subscription;
  }

  ngOnInit(): void {
    const modulo : Modulo = this.memoriaService.getModulo();
    const persona : Persona = this.memoriaService.getPersona();

    this.bitacoraService.create(modulo,persona, 'FINALIZA-ATENCION','EXPULSA USB Y ACABA LA ATENCION DEL ASISTENTE');

    this.driveService.verificarUsb().subscribe((drive: any) => {
      let letra: string = drive.letraUnidad
      this.driveService.expulsarUsb(letra.substr(0, 1)).subscribe(data => {
        this.startVideo();
      }, (err) => {
        this.startVideo();
      })
    },(error)=>{
      this.startVideo();
    })


  }


  startVideo() {
    this.success = true;
    this.mensaje = 'YA PUEDES RETIRAR TU USB'
    setTimeout(() => {
      this.videosService.playVideo(this.myVideo);
      setTimeout(() => {
        this.router.navigateByUrl('dni-lectora');
      }, 5000);
    }, 200);
  }



}
