import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faHome, faUndoAlt } from '@fortawesome/free-solid-svg-icons';
import { Subscription } from 'rxjs';
import { WordModel } from 'src/app/dto/word-model';
import { AnnyangService, inicioCommand, volverCommand } from 'src/app/services/annyang.service';

@Component({
  selector: 'app-volver',
  templateUrl: './volver.component.html',
  styleUrls: ['./volver.component.scss']
})
export class VolverComponent implements OnInit, OnDestroy {

  constructor(private router: Router,private annyangService: AnnyangService) {
    this.subscription = new Subscription;
  }

  faInicio = faHome;
  faVolver = faUndoAlt;

  @Input()
  inicioRoute: string = 'enabled';
  @Input()
  volverRoute: string = '';


  subscription: Subscription

  ngOnInit(): void {
    /*if (this.volverRoute != '') { this.annyangService.agregarComandosSinRemover([volverCommand]) }
    if (this.inicioRoute != 'disabled') { this.annyangService.agregarComandosSinRemover([inicioCommand]) }*/
    this.subscribeWordDetected()
  }

  subscribeWordDetected() {
    console.log('VOLVER SUSCRIBE')
    this.subscription = this.annyangService.wordDetected.subscribe((word: WordModel) => {
      if (word) {
        switch (word.command) {
          case 'volverCommand':
            this.volver();
            break;
          case 'inicioCommand':
            this.inicio();
            break;
        }
      }
    })
  }

  ngOnDestroy(): void {
    console.log('VOLVER UNSUSCRIBE')
    this.subscription.unsubscribe();
  }

  volver(){
    if(this.volverRoute!=''){
      this.router.navigate([`${this.volverRoute}`]).then(() => {
        window.location.reload();
      });
    }
  }

  inicio(){
    if(this.inicioRoute != 'disabled'){
      this.router.navigate([`dni-lectora`]).then(() => {
        window.location.reload();
      });
    }
  }





}
