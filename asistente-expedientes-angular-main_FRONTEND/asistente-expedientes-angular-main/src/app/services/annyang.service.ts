import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { WordModel } from '../dto/word-model';
import { codificarRango, hasLetters } from '../shared/util';
import { arrayEleccion, arrayEspecialidad, arrayPreguntaFinal, arraySatisfaccion, arrayVoiceRecordStart } from '../shared/var.constant';
import { MicrophoneService } from './microphone.service';

declare const annyang: any;

let wordDetectedSource = new Subject<WordModel>();
let wordDetected = wordDetectedSource.asObservable();
let microphoneService:any;

@Injectable({
  providedIn: 'root'
})

export class AnnyangService {

  wordDetected = wordDetected;

  constructor(private router: Router, private micS:MicrophoneService) {
    microphoneService=micS;
  }

  initConfig() {
    if (annyang) {
      //annyang.debug();
      
      annyang.removeCommands();
      annyang.setLanguage('es-PE');
      annyang.addCommands(pruebaAudioCommand);
      console.log('Annynang cargado!')

      /*annyang.addCallback('start', function() {
        console.log("ANNYANG START!");
      });

      annyang.addCallback("result", (frases: any) => {
        console.log("El arreglo de frases: ", frases);
      });*/

      annyang.addCallback("resultMatch", (frases: any) => {
        console.log("Te escuche decir: ", frases);
      });

      annyang.addCallback("resultNoMatch", (frases: any) => {
        console.log("NoMatch: ", frases);
        microphoneService.setWrong(frases[0]);
      });

      annyang.addCallback("soundstart", () => {
        console.log("Escuche algo!");
      });
      
    } else {
      this.navegadorNOSoportado();
    }
  }

  recordModoPrueba(){
    annyang.addCommands(pruebaAudioCommand);
    annyang.start({ autoRestart :true, continuous: false });
  }

  agregarComandos(comands: any[]) {
    console.log('AGREGANDO COMANDOS REMOV')
    if (annyang) {
      annyang.removeCommands();
      for (let i = 0; i < comands.length; i++) {
        annyang.addCommands(comands[i]);
      }
    } else {
      this.navegadorNOSoportado();
    }
  }

  agregarComandosSinRemover(comands: any[]) {
    console.log('AGREGANDO COMANDOS SIN REMOV')
    if (annyang) {
      for (let i = 0; i < comands.length; i++) {
        console.log(comands[i])
        annyang.addCommands(comands[i]);
      }
    } else {
      this.navegadorNOSoportado();
    }
  }

  borrarComandos(commands: any[]){
    if(annyang){
      annyang.removeCommands();
    }
  }

  navegadorNOSoportado(){
    alert("Navegador NO soporta reconocimiento de Voz");
      this.router.navigate(['dni-lectora']);
  }

  startToRecord(key: string) {
    const itemRecord: any= arrayVoiceRecordStart.find(item=>{
      return item.key == key
    })
    return setTimeout(() => {
      console.log('EMPEZANDO A GRABAR')
      this.startRecord();
    }, itemRecord?.timeToRecord);
  }

  pauseRecord(){
    annyang.abort();
    microphoneService.setIsRecording(false);
  }

  private startRecord(){
    annyang.start({ autoRestart :true, continuous: false });
    microphoneService.setIsRecording(true);
  }


}


export const pruebaAudioCommand = {
  'prueba de audio': function () { console.log('Se escucha perfectamente!') }
}


export const volverCommand={
  'volver': function () {  actualizarSource("volverCommand", "volver")  }
}

export const inicioCommand = {
  'inicio': function () {  actualizarSource("inicioCommand", "inicio")  }
}

export const satisfaccionCommand = {
  '*valor': function(valor: string){
    const val = valor.toLowerCase();
    let satVal=getSatisfaccionCalificacion(val);
    if(satVal>0){
      actualizarSource("satisfaccionCommand",satVal)  
    }else{
      microphoneService.setWrong(`${valor}`); 
    }
  }
}

export const seleccionarCommand = {
  'seleccionar :valor': function (valor: any) {
    let val=getNumeroValor(valor);
    if (val != -1) { 
      actualizarSource("seleccionarCommand", val) ;
    } else {
      microphoneService.setWrong(`Seleccionar ${valor}`); 
    }
  },
  'fila :valor': function (valor: any) {
    let val=getNumeroValor(valor);
    if (val != -1 ) {
      actualizarSource("seleccionarCommand",val) 
    }else{
      microphoneService.setWrong(`Seleccionar ${valor}`); 
    }
   }
}

export const seleccionarRangoCommand = {
  'seleccionar :valor hasta :valor2': function (valor1: any, valor2: any) {
    const val_1=getNumeroValor(valor1);
    const val_2=getNumeroValor(valor2);
    if (val_1 != -1 && val_2!=-1  && val_2>val_1 ) {
      actualizarSource("seleccionarRangoCommand",codificarRango(val_1,val_2))
    }else{
      microphoneService.setWrong(`Seleccionar ${valor1} hasta ${valor2}`); 
    }
  },
  'fila :valor hasta fila :valor2': function (valor1: any, valor2: any) {
    const val_1=getNumeroValor(valor1);
    const val_2=getNumeroValor(valor2);
    if (val_1 != -1 && val_2!=-1 && val_2>val_1 ) {
      actualizarSource("seleccionarRangoCommand",codificarRango(val_1,val_2))
    }else{
      microphoneService.setWrong(`Fila ${valor1} hasta ${valor2}`); 
    }
   }
}

export const dictadoExpedienteCommand={
  '*valor': function (valor: any) {
    console.log('has letter: '+hasLetters(valor))
    if(hasLetters(valor)){
      let val=getNumeroValor(valor);
      if (val != -1 ) {
        actualizarSource("dictadoExpedienteCommand",val)  
      }else{
        microphoneService.setWrong(`${valor}`); 
      }
    }else{
      valor=valor.replace(/ /g,'')
      valor=valor.replace(',','')
      actualizarSource("dictadoExpedienteCommand",valor)  
    }
    
  }
}

export const si_noCommand={
  ':valor':function(valor:string){
    let toReturn = -1;
    switch (valor.toLowerCase()) {
      case 'si':
        toReturn=1;
        break;
      case 'sí':
        toReturn=1;
        break; 
      case 'no':
        toReturn=0;
        break;
      case 'nó':
        toReturn=0;
        break;

    }
    if(toReturn>-1){
      actualizarSource("si_noCommand",toReturn)  
    }else{
      microphoneService.setWrong(`${valor}`); 
    }
  }
}

export const paginatorCommand={
  'lista :valor': function (valor: string) {
    let toReturn = -1;
    switch (valor.toLowerCase()) {
      case 'siguiente':
        toReturn=1;
        break;
      case 'anterior':
        toReturn=0;
        break;
      default:
        if(hasLetters(valor)){
          toReturn=getNumeroValor(valor)
          if(toReturn!=-1){
            toReturn=toReturn+10
          }
        }else{
          toReturn=+valor+10
        }
    }
    if(toReturn>-1){
      actualizarSource("paginatorCommand",toReturn)  
    }else{
      microphoneService.setWrong(`Lista ${valor}`); 
    }
  }
}

export const descargarCommand={
  'descargar': function(){
      actualizarSource("descargarCommand",'descargar')  
  }
}

export const eleccionArchivosCommand={
  '*valor': function(valor: string){
    const val = valor.toLowerCase();
    if(existInEleccionArray(val)){
      actualizarSource("eleccionArchivosCommand",val)  
    }else{
      microphoneService.setWrong(`${valor}`); 
    }
  }
}

export const eleccionEspecialidadCommand={
  '*valor': function(valor: string){
    const val = valor.toLowerCase();
    if(existInEspecialidadArray(val)){
      actualizarSource("eleccionEspecialidadCommand",val)  
    }else{
      microphoneService.setWrong(`${valor}`); 
    }
  }
}


export const eleccionPreguntaFinalCommand={
  '*valor': function(valor: string){
    const val = valor.toLowerCase();
    if(existInPreguntaFinalArray(val)){
      actualizarSource("eleccionPreguntaFinalCommand",val)  
    }else{
      microphoneService.setWrong(`${valor}`); 
    }
  }
}

export const siguientePaginaCommand={
  'siguiente': function(){
    actualizarSource("eleccionArchivosCommand",'siguiente')  
  }
}


/*************** FUNCIONES DE APOYO *************/
function actualizarSource(key:string, value:any){
  console.log('Actualizando source '+key+' - valor: '+value)
  wordDetectedSource.next(new WordModel(key,value));

}

function getSatisfaccionCalificacion(val:string):number{
  var cali :number= 0;
  cali = parseInt( arraySatisfaccion.find(sat =>{
    return sat.titulo.toLowerCase() == val;
  })?.key || '0') 

  return cali;
}

function existInPreguntaFinalArray(val:string):boolean{
  var found = false;
  for (var i = 0; i < arrayPreguntaFinal.length; i++) {
    if (arrayPreguntaFinal[i].titulo.toLowerCase()== val) {
      found = true;
    }
  }
  return found;
}

function existInEspecialidadArray(val:string):boolean{
  var found = false;
  for (var i = 0; i < arrayEspecialidad.length; i++) {
    if (arrayEspecialidad[i].titulo.toLowerCase()== val) {
      found = true;
    }
  }
  return found;
}

function existInEleccionArray(val: string): boolean {
  var found = false;
  for (var i = 0; i < arrayEleccion.length; i++) {
    if (arrayEleccion[i].titulo.toLowerCase()== val) {
      found = true;
    }
  }
  return found;
}


function getNumeroValor(val: string): number {
  switch (val.toLowerCase()) {
    case '0':
    case 'cero':
      return 0;
    case '1':
    case 'uno':
      return 1;
    case '2':
    case 'dos':
      return 2;
    case '3':
    case 'tres':
      return 3;
    case '4':
    case 'cuatro':
      return 4;
    case '5':
    case 'cinco':
      return 5;
    case '6':
    case 'seis':
      return 6;
    case '7':
    case 'siete':
      return 7;
    case '8':
    case 'ocho':
      return 8;
    case '9':
    case 'nueve':
      return 9;
    case '10':
    case 'diez':
      return 10;
    case '11':
    case 'once':
      return 11;
    case '12':
    case 'doce':
      return 12;
    case '13':
    case 'trece':
      return 13;
    case '14':
    case 'catorce':
      return 14;
    case '15':
    case 'quince':
      return 15;
    default:
      return -1
  }
}

