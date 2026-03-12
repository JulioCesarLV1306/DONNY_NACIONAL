import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AnnyangService } from './services/annyang.service';
import { faMicrophone } from '@fortawesome/free-solid-svg-icons';
import { LOCALE_ID, NgModule } from '@angular/core';
import { registerLocaleData } from '@angular/common';

import localeEsPE from '@angular/common/locales/es-PE';
import { MicrophoneService } from './services/microphone.service';
import { MessageService } from 'primeng/api';

registerLocaleData(localeEsPE, 'es-PE');

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  providers: [ { provide: LOCALE_ID, useValue: 'es-PE' } ],
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy{


  isRecording: boolean = false;
  textWrong:string = '';
  
  faMicrophone = faMicrophone;
  @ViewChild('audioMic') audioPlayerRef!: ElementRef;


  constructor(private annyangService:AnnyangService , private microphoneService: MicrophoneService, 
     private messageService: MessageService){
    }
 


  ngOnInit(): void {
   this.annyangService.initConfig();
    this.microphoneService.wrongDetected.subscribe((data:any)=>{
      if(this.isRecording){
        this.textWrong=data;
        if(this.textWrong!=''){
          setTimeout(() => {
            this.textWrong='';
          }, 2000);
        }

      }
    });

    this.microphoneService.recordDetected.subscribe((data:any)=>{
        this.isRecording=data;
        if(this.isRecording){
          this.audioPlayerRef.nativeElement.play();

          this.messageService.add({key:"mic", severity:'custom', 
          summary: 'MICRÓFONO ACTIVO', detail: 'Ya puedes hablar', closable:false});
        }
    });

    /*
    */
  }

  ngOnDestroy(): void {
    this.messageService.clear();
  }


}
