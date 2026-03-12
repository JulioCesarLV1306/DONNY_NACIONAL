
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { arrayVoiceRecordStart } from '../shared/var.constant';
import { AnnyangService } from './annyang.service';

@Injectable({
  providedIn: 'root'
})
export class VideosService {

  constructor(private annyangService: AnnyangService, private router: Router) { }

  playVideo(myVideo: any) {
    myVideo.nativeElement.currentTime=0;
    let promisePlay=myVideo.nativeElement.play()
    if (promisePlay !== undefined) {
      promisePlay.then((_: any) => {
       
      }).catch((error: any) => {
        console.log(error)
      })
    }
  }

  stopVideo(myVideo: any) {
    let promisePause=myVideo.nativeElement.pause()
    if (promisePause !== undefined) {
      promisePause.then((_: any) => {
        
      }).catch((error: any) => {
        console.log(error)
      })
    }
  }

  iniciarContadorReload(keyRecord:string){
    let itemRecord = arrayVoiceRecordStart.find(item=>{
      return item.key == keyRecord;
    })
    return setTimeout(() => {
      const currentRoute = this.router.url;
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate([currentRoute]); 
    }); 
    }, itemRecord?.durationVideo);
    
  }

 

}
