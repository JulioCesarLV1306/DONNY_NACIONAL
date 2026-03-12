import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { MemoriaService } from './memoria.service';
import { MensajeService } from './mensaje.service';

@Injectable({
  providedIn: 'root'
})
export class InactividadService {
  
  private timeoutInactividad:any;
  private maximoPermitidoSegs:number=300
  private i :number=0

  constructor( private router: Router, private memoriaService:MemoriaService, private mensajeService:MensajeService) { }

  iniciarSupervision(){
    const actualRuta=this.router.url;
    const rutaPrevia = this.memoriaService.getInactividadRuta();
    console.log('rutaPrevia: '+rutaPrevia)
    console.log('actualRuta: '+actualRuta)
    if(rutaPrevia=='/start' || actualRuta!=rutaPrevia || this.i==0){
      this.memoriaService.setInactividadRuta(actualRuta);
      clearTimeout(this.timeoutInactividad);
      this.i=0
      this.contar();
    } 
  }

 private contar(){
    if(this.i<this.maximoPermitidoSegs){
      this.timeoutInactividad=setTimeout(() => {
        console.log('Tiempo inactivo: '+this.i)
        this.i=this.i+1
        this.contar();
      }, 1000);
    }else{
     this.mensajeService.goToInactividad();
    }
    
  }

}
