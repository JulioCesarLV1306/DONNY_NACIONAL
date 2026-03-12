import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Modulo } from '../models/modulo';
import { Persona } from '../models/persona';
import { HOST_BACKEND } from '../shared/var.constant';

@Injectable({
  providedIn: 'root'
})
export class BitacoraService {

  private url= `${HOST_BACKEND}/bitacora`;

  constructor(private http: HttpClient) { }

  errorBitacora(modulo: Modulo, persona: Persona, codigo: string){
    const descripcion =''
    this.http.put(`${this.url}/error`,{modulo, persona, codigo,descripcion}).subscribe(data=>console.log('RETORNO DE BITACORA: '+data));
  }

  create(modulo: Modulo, persona: Persona, codigo: string, descripcion:string){
    this.http.put(`${this.url}/create`,{modulo, persona, codigo, descripcion}).subscribe(data=>console.log('RETORNO DE BITACORA: '+data));
  }


}
