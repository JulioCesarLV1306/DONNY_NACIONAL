import { Component, HostListener, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { WordModel } from 'src/app/dto/word-model';
import { AnnyangService } from 'src/app/services/annyang.service';

@Component({
  selector: 'app-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss']
})
export class PaginatorComponent implements OnInit, OnDestroy {



  subscription: Subscription
  private isNavigating = false;

  @Input()
  data: any;

  constructor(private router: Router, private annyangService: AnnyangService) {
    this.subscription = new Subscription;
  }

  ngOnInit(): void {
    this.subscribeWordDetected()
  }

  subscribeWordDetected() {
    console.log('PAGINATOR SUSCRIBE')
    this.subscription = this.annyangService.wordDetected.subscribe((word: WordModel) => {
      if (word) {
        switch (word.command) {
          case 'paginatorCommand':
            switch (word.value) {
              case 0:
                this.clickAtras();
                break;
              case 1:
                this.clickSiguiente();
                break;
              default:
                this.clickSalto(word.value);
                break;
            }
            break;
        }
      }
    })
  }

  clickSiguiente() {
    if (!this.data || this.isNavigating) {
      return;
    }

    const currentPage = Number(this.data.number ?? 0);
    if (!this.data.last) {
      this.isNavigating = true;
      console.log('SIGUIENTE')
      this.router.navigate([`${this.data.routerTo}/${(currentPage + 1)}`]).then(() => {
        window.location.reload();
      });
    }
  }

  clickAtras() {
    if (!this.data || this.isNavigating) {
      return;
    }

    const currentPage = Number(this.data.number ?? 0);
    if (!this.data.first) {
      this.isNavigating = true;
      this.router.navigate([`${this.data.routerTo}/${(currentPage - 1)}`]).then(() => {
        window.location.reload();
      });
    }
  }


  clickSalto(salto: number) {
    if (!this.data || this.isNavigating) {
      return;
    }

    salto = Number(salto) - 10;
    const totalPages = Number(this.data.totalPages ?? 0);
    if(salto>0 && salto <= totalPages){
      this.isNavigating = true;
      this.router.navigate([`${this.data.routerTo}/${(salto - 1)}`]).then(() => {
        window.location.reload();
      });
    }
  }

  @HostListener('window:keydown.arrowleft', ['$event'])
  onArrowLeft(event: KeyboardEvent) {
    event.preventDefault();
    this.clickAtras();
  }

  @HostListener('window:keydown.arrowright', ['$event'])
  onArrowRight(event: KeyboardEvent) {
    event.preventDefault();
    this.clickSiguiente();
  }

  ngOnDestroy(): void {
    console.log('PAGINATOR UNSUSCRIBE')
    this.subscription.unsubscribe();
  }

}
