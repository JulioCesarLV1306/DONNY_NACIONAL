import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Modulo } from '../models/modulo';
import { HOST_BACKEND } from '../shared/var.constant';

@Injectable({
  providedIn: 'root'
})
export class PersonasService {

  private url= `${HOST_BACKEND}/persona`  

  constructor(private http: HttpClient) { }

  /**
   * Valida un DNI en el backend
   * NOTA: ipModulo y usuarioModulo se mantienen por compatibilidad temporal
   * @param dni DNI a validar
   * @param ipModulo IP del módulo (compatibilidad)
   * @param usuarioModulo Usuario del módulo (compatibilidad)
   */
  validarDNI(dni: string, ipModulo:string, usuarioModulo:string){
    return this.http.get(`${this.url}/buscar`, { params: { dni,ipModulo,usuarioModulo} });
  }

}
