import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Drive } from '../models/drive';
import { Modulo } from '../models/modulo';
import { Persona } from '../models/persona';
import { HOST_BACKEND } from '../shared/var.constant';

@Injectable({
  providedIn: 'root'
})
export class DownloaderService {

  private url= `${HOST_BACKEND}/downloader`;

  constructor(private http: HttpClient) { }

  obtenerTamanios(nUnico:string, nIncidente:number,fechas:string[],eleccion:string[]){
    return this.http.put(`${this.url}/tamanio?nUnico=${nUnico}&nIncidente=${nIncidente}&fechas=${fechas.join(',')}&elecciones=${eleccion.join(',')}`,null);
  }

  descargar(expediente:string,nUnico:string, nIncidente:number,fechas:string[],eleccion:string, tamanio:number, drive: Drive, modulo: Modulo, persona:Persona){
    // Usar propiedades refactorizadas de Modulo
    return this.http.put(`${this.url}/descarga?expediente=${expediente}&nUnico=${nUnico}&nIncidente=${nIncidente}&fechas=${fechas.join(',')}&eleccion=${eleccion}&tamanio=${tamanio}&ipModulo=${modulo.cPcIp}&idModulo=${modulo.nIdModulo}&usuarioModulo=${modulo.cPcUsuario}&dniPersona=${persona.dni}&nombrePersona=${persona.nombre}`,drive);
  }

  consultar(nUnico:string, nIncidente:number, ipModulo:string, dniPersona:string, eleccion:string, fechas:string[]){
    return this.http.put(`${this.url}/consultar?nUnico=${nUnico}&nIncidente=${nIncidente}&eleccion=${eleccion}&ipModulo=${ipModulo}&dniPersona=${dniPersona}&fechas=${fechas.join(',')}`,null);
  }

  copiar(expediente:string,nUnico:string, nIncidente:number, fechas:string[], eleccion:string, driveLetra:string,  modulo: Modulo, persona:Persona){
      // Usar propiedades refactorizadas de Modulo
      return this.http.put(`http://localhost:4568/downloader/copiar?expediente=${expediente}&nUnico=${nUnico}&nIncidente=${nIncidente}&eleccion=${eleccion}&dniPersona=${persona.dni}&nombrePersona=${persona.nombre}&driveLetra=${driveLetra}&moduloIP=${modulo.cPcIp}&usuarioModulo=${modulo.cPcUsuario}&fechas=${fechas.join(',')}`,null);
  }

  consultarCopia(nUnico:string, nIncidente:number, ipModulo:string, dniPersona:string, eleccion:string, fechas:string[]){
    return this.http.put(`http://localhost:4568/downloader/consultar?nUnico=${nUnico}&nIncidente=${nIncidente}&eleccion=${eleccion}&moduloIP=${ipModulo}&dniPersona=${dniPersona}&fechas=${fechas.join(',')}`,null);
  }
}
