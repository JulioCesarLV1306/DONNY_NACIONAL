import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

let isRecordingSource = new Subject<boolean>();
let recordDetected = isRecordingSource.asObservable();

let wrongSource = new Subject<string>();
let wrongDetected = wrongSource.asObservable();


@Injectable({
  providedIn: 'root'
})
export class MicrophoneService {

  recordDetected=recordDetected;
  wrongDetected=wrongDetected;
  
  constructor() { }

  setIsRecording(recording: boolean){
    isRecordingSource.next(recording);
  }

  setWrong(word: string){
    wrongSource.next(word);
  }


}
