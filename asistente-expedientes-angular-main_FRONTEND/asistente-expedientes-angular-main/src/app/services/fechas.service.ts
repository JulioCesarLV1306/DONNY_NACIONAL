import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Fecha } from '../models/fecha';
import { HOST_BACKEND } from '../shared/var.constant';

@Injectable({
  providedIn: 'root'
})
export class FechasService {

  private url_actas= `${HOST_BACKEND}`;

  constructor(private http: HttpClient) { }


  getFechasVideos(nUnico:string, nIncidente:number , page : number, size:number){
    return this.http.get(`${this.url_actas}/videos/fechas`, { params: { nUnico,nIncidente, page, size } });
  }


  getFechasActas(nUnico:string, nIncidente:number , page : number, size:number){
    return this.http.get(`${this.url_actas}/actas/fechas`, { params: { nUnico,nIncidente, page, size } });
  }

  getFechasResoluciones(nUnico:string, nIncidente:number, page : number, size:number){
    return this.http.get(`${this.url_actas}/resoluciones/fechas`, { params: { nUnico,nIncidente, page, size } });
  }

  getFechasDigitalizados(nUnico:string, nIncidente:number, page : number, size:number){
    return this.http.get(`${this.url_actas}/digitalizados/fechas`, { params: { nUnico,nIncidente, page, size } });
  }

}
