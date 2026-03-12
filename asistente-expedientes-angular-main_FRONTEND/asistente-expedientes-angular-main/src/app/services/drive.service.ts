import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DriveService {

  private url= `http://localhost:4568/drive`;

  constructor(private http: HttpClient) { }

  verificarUsb(){
    return this.http.put(`${this.url}/verificar-usb`,null);
  }

  expulsarUsb(letra: string){
    return this.http.put(`${this.url}/expulsar-usb?letra=${letra}`,null);
  }

  verificarCliente(){
    return this.http.get(`${this.url}/verificar-cliente`);
  }
}
