import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DriveService } from 'src/app/services/drive.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';

@Component({
  selector: 'app-inserte-usb',
  templateUrl: './inserte-usb.component.html',
  styleUrls: ['./inserte-usb.component.scss']
})
export class InserteUsbComponent implements OnInit {

  tiempoMaximoEspera: number = 120000;
  tiempoEntreRequest: number = 1000;
  iteraciones: number = 0;

  i: number = 0;

  tiempoRestante: number = 60;
  porcentajeRestante: number = 100


  constructor(private driveService: DriveService, private mensajeService: MensajeService, private memoriaService: MemoriaService, private router: Router) { }

  ngOnInit(): void {
    this.iteraciones = this.tiempoMaximoEspera / this.tiempoEntreRequest;
    this.verificarUsbBucle();
  }

  verificarUsbBucle() {
    setTimeout(() => {
      console.log(`[${this.i + 1},${this.iteraciones}] Buscando USB...`);
      this.verificarUsb();
      this.porcentajeRestante = 100 - Math.round(((this.i + 1) / this.iteraciones) * 100)
      this.tiempoRestante = (this.tiempoMaximoEspera - ((this.i + 1) * this.tiempoEntreRequest)) / 1000
      this.i++;
      if (this.i < this.iteraciones) {
        this.verificarUsbBucle();
      } else {
        //let contador = this.memoriaService.aumentarContadorBusquedaUSB();
        this.mensajeService.goToNoDatos();
        /*console.log('contador usb: ' + contador)
        if (contador >= 2) {
         
        } else {
          this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
            this.router.navigate(['inserte-usb']); 
          }); 
        }*/
      }

    }, this.tiempoEntreRequest);
  }

  verificarUsb() {
    this.driveService.verificarUsb().subscribe((usb: any) => {
      if (usb) {
        this.mensajeService.goToUsbDetectado(usb);
      }
    });
  }



}
