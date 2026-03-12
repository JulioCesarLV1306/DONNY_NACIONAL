import { AfterViewInit, Component, Input, OnDestroy, OnInit } from '@angular/core';
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

    console.log(this.data)
    if (!this.data.last) {
      console.log('SIGUIENTE')
      this.router.navigate([`${this.data.routerTo}/${(this.data.number + 1)}`]).then(() => {
        window.location.reload();
      });
    }
  }

  clickAtras() {
    if (!this.data.first) {
      this.router.navigate([`${this.data.routerTo}/${(this.data.number - 1)}`]).then(() => {
        window.location.reload();
      });
    }
  }


  clickSalto(salto: number) {
    salto=salto-10
    if(salto>0 && salto <=this.data.totalPages){
      this.router.navigate([`${this.data.routerTo}/${(salto - 1)}`]).then(() => {
        window.location.reload();
      });
    }
  }

  ngOnDestroy(): void {
    console.log('PAGINATOR UNSUSCRIBE')
    this.subscription.unsubscribe();
  }

}
