import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Encuesta } from '../models/encuesta';
import { HOST_BACKEND } from '../shared/var.constant';

@Injectable({
  providedIn: 'root'
})
export class EncuestaService {

  
  private url= `${HOST_BACKEND}/encuesta`;

  constructor(private http: HttpClient) { }

  /**
   * Crea una encuesta en el backend
   * NOTA: ipModulo y usuarioModulo se mantienen por compatibilidad temporal
   * El backend los usa para registrar bitácora
   */
  crear(encuesta:Encuesta, ipModulo:string, usuarioModulo:string){
    return this.http.post(`${this.url}/crear`,encuesta,{params:{ipModulo,usuarioModulo}})
  }
  
  buscar(dni:string){
    return this.http.get(`${this.url}/buscar`,{params : {dni}})
  }
}
