import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Drive } from '../models/drive';
import { HOST_BACKEND } from '../shared/var.constant';
import { BitacoraService } from './bitacora.service';
import { MemoriaService } from './memoria.service';

@Injectable({
  providedIn: 'root'
})
export class MensajeService {
  
  constructor(private router:Router,private bitacoraService: BitacoraService, private memoriaService: MemoriaService) { }

  goToDniNoValido(dni:string){
    this.router.navigate(['mensaje/dni'],{queryParams: {replace_mensaje:dni,routerTo: 'dni-lectora'}})
  }

  goToBienvenido(nombre: string){
    this.router.navigate(['mensaje/bienvenido'],{queryParams: {replace_mensaje:nombre,routerTo: 'ingrese-expediente/numero'}});
  }

  goToValorIncorrecto(valor: any, router: string){
    this.router.navigate(['mensaje/valor-incorrecto'],{queryParams: {replace_mensaje:valor,routerTo: router}});
  }

  goToNoSeleccion(valor: any, router: string){
    this.router.navigate(['mensaje/no-seleccion'],{queryParams: {replace_mensaje:valor,routerTo: router}});
  }

  goToUsbDetectado(drive: Drive){
    let replace=drive.nombre;
    this.router.navigate(['mensaje/usb-detectado'],{queryParams: {replace_mensaje:replace,routerTo: 'descarga-archivos'}});
  }

  goToErrorSistema(code:any,router:string){
    code= code =='0'? 'DESCONOCIDO':code;
    this.router.navigate(['mensaje/error-server'],{queryParams: {replace_mensaje:code,routerTo: router}});
    
    let modulo = this.memoriaService.getModulo();
    let persona = this.memoriaService.getPersona();
    this.bitacoraService.errorBitacora(modulo,persona,code);
  }

  goToNoDatos(){
    this.router.navigate(['mensaje/no-datos'],{queryParams: {replace_mensaje:'',routerTo: 'dni-lectora'}});
  }

  goToNoCliente(){
    this.router.navigate(['mensaje/no-cliente'],{queryParams: {replace_mensaje:'',routerTo: 'dni-lectora'}}); 
  }

  goToDespedidaError(){
    this.router.navigate(['mensaje/despedida-error'],{queryParams: {replace_mensaje:'',routerTo: 'elegir-archivos'}});
  }
  goToDespedidaSuccess(mensaje:string){
    this.router.navigate(['mensaje/despedida-success'],{queryParams: {replace_mensaje:mensaje,routerTo: ''}});
  }

  goToDespedidaFinal(){
   // this.router.navigate(['mensaje/despedida-final'],{queryParams: {replace_mensaje:'',routerTo: 'dni-lectora'}});
   this.router.navigate(['despedida-final']);
  }

  goToInactividad(){
    this.router.navigate(['mensaje/inactividad'],{queryParams: {replace_mensaje:'',routerTo: 'dni-lectora'}});
  }

  goToEncuesta(){
    this.router.navigate(['encuesta']);
  }

  goToIngreseNumero(){
    this.router.navigate(['ingrese-expediente/numero']);
  }

  goToIngreseEspecialidad(){
    this.router.navigate(['elegir-especialidad']);
  }


  
}
