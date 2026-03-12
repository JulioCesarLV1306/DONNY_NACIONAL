import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HOST_BACKEND } from '../shared/var.constant';

@Injectable({
  providedIn: 'root'
})
export class ExpedientesService {

  private url= `${HOST_BACKEND}/expediente`;

  constructor(private http: HttpClient) { }

  buscarExpedientes(numero:number, anio:number, cuaderno: number, especialidad:string, dni: string, 
    id_tipo: number,nombrePersona:string, ipModulo:string, idModulo:number, usuarioModulo:string,page: number, size: number){
    // NOTA: Parámetros ipModulo, idModulo, usuarioModulo se mantienen por compatibilidad con backend
    // pero deberían ser reemplazados por un objeto Usuario en el futuro
    return this.http.get(`${this.url}/buscar`, { params: { numero, anio, cuaderno: cuaderno == null ? -1 : cuaderno, especialidad,
       dni, id_tipo, nombrePersona, ipModulo, idModulo, usuarioModulo, page, size } });
  }

  buscarExpedientesPorDNI(dni:string, especialidad:string,id_tipo: number,nombrePersona:string, ipModulo:string, 
    idModulo:number, usuarioModulo:string,page: number, size: number){
    // NOTA: Parámetros ipModulo, idModulo, usuarioModulo se mantienen por compatibilidad con backend
    return this.http.get(`${this.url}/buscar/dni`, { params: { especialidad, dni, id_tipo, nombrePersona, ipModulo,
      idModulo, usuarioModulo, page, size } });
  }

  contarArchivos(nUnico:string, nIncidente:number){
    return this.http.get(`${this.url}/contar`, { params: { nUnico, nIncidente } });
  }

  
}
