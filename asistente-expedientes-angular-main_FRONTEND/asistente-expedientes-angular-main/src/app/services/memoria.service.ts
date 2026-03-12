import { Injectable } from '@angular/core';
import { EleccionModel } from '../dto/eleccion-model';
import { Drive } from '../models/drive';
import { Expediente } from '../models/expediente';
import { Fecha } from '../models/fecha';
import { Modulo } from '../models/modulo';
import { Persona } from '../models/persona';

@Injectable({
  providedIn: 'root'
})
export class MemoriaService {

  keyPersona:string='persona_login';
  keyExpediente: string = 'expediente_actual';

  keyNumero:string='key_numero';
  keyAnio:string='key_anio';
  keyCuaderno:string='key_cuaderno';
  keyEspecialidad:string='key_especialidad';
  keyEleccion:string='key_eleccion';
  keyRangoFechas: string='key_rangofechas';
  keyDrive:string = 'key_drive';
  keyModulo:string = 'key_modulo';

  keyContadorBusquedaUSB:string = 'key_contbususb';
  keyInactividadRuta:string = 'key_inactividadruta';

  constructor() { }

  limpiarMemoria(){
    console.log('Memoria limpia...');
    localStorage.clear();
  }


  private guardar(key:string,object:any){
    console.log('Guardando en Memoria '+key);
    console.log(object)
    localStorage.setItem(key,JSON.stringify(object));
  }

  private get(key:string){
    console.log('Recuperando de Memoria '+key);
    console.log(JSON.parse(localStorage.getItem(key) || '{}'))
    return JSON.parse(localStorage.getItem(key) || '{}');
  }


  guardarPersona(persona : Persona){
    this.guardar(this.keyPersona,persona);
  }
  getPersona():Persona{
    return this.get(this.keyPersona);
  }


  guardarModulo(modulo : Modulo){
    this.guardar(this.keyModulo,modulo);
  }
  getModulo():Modulo{
    return this.get(this.keyModulo);
  }


  guardarExpediente(expediente: Expediente){
    this.guardar(this.keyExpediente,expediente);
  } 
  getExpediente():Expediente{
    return this.get(this.keyExpediente);
  }



  guardarEleccionArchivos(listaEleccion: EleccionModel[]){
    this.guardar(this.keyEleccion,listaEleccion);
  }
  getEleccionArchivos(): EleccionModel[]{
    return this.get(this.keyEleccion);
  }



  guardarDrive(drive: Drive){
    this.guardar(this.keyDrive,drive);
  }
  getDrive():Drive{
    return this.get(this.keyDrive);
  }


  borrarRangoFechasPagina(pagina:number){
    localStorage.removeItem(this.keyRangoFechas+'-'+pagina);
  }
  guardarRangoFechasPagina(pagina:number,listaFechas: Fecha[]){
    this.guardar(this.keyRangoFechas+'-'+pagina,listaFechas);
  }
  getRangoFechasPagina(pagina:number):Fecha[]{
    return this.get(this.keyRangoFechas+'-'+pagina)
  }

  guardarRangoFechasFinal(listaFechas: Fecha[]){
    this.guardar(this.keyRangoFechas,listaFechas);
  }
  getRangoFechasFinal():Fecha[]{
    return this.get(this.keyRangoFechas)
  }

  cleanRangoFechas(){
    Object.keys(localStorage)
    .forEach((key)=>{
      if(key.startsWith(this.keyRangoFechas)){
        localStorage.removeItem(key);
      }
    })
  }

   

  /****** BUSQUEDA EXPEDIENTE******/

  setNumero(numero: any){
    localStorage.setItem(this.keyNumero,numero);
  }

  setAnio(anio: any){
    localStorage.setItem(this.keyAnio,anio);
  }

  setCuaderno(cuaderno: any){
    localStorage.setItem(this.keyCuaderno,cuaderno);
  }

  setEspecialidad(especialidad: string){
    localStorage.setItem(this.keyEspecialidad,especialidad);
  }

  getNumero(){
    return localStorage.getItem(this.keyNumero);
  }

  getAnio(){
    return localStorage.getItem(this.keyAnio);
  }

  getCuaderno(){
    return localStorage.getItem(this.keyCuaderno);
  }

  getEspecialidad(){
    return localStorage.getItem(this.keyEspecialidad);
  }


    /****** CONTADORES GENERALES******/
    /*aumentarContadorBusquedaUSB(){
      let contadorActual=this.getContadorBusquedaUSB();
      let num;
      if(contadorActual==undefined){
        num=1;
        this.guardar(this.keyContadorBusquedaUSB,num.toString());
      }else{
        num=parseInt(contadorActual,10)+1;
        this.guardar(this.keyContadorBusquedaUSB,num.toString());
      }
      return num;
    }
    getContadorBusquedaUSB(){
      return this.get(this.keyContadorBusquedaUSB);
    }*/

    setInactividadRuta(ruta:string){
      localStorage.setItem(this.keyInactividadRuta,ruta);
    }

    getInactividadRuta(){
      return localStorage.getItem(this.keyInactividadRuta) ;
    }

  



}
