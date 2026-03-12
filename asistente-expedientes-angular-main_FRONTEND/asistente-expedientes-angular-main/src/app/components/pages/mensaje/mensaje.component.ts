import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MensajeModel } from 'src/app/dto/mensaje-model';
import { BitacoraService } from 'src/app/services/bitacora.service';
import { EncuestaService } from 'src/app/services/encuesta.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';
import { arrayMensaje, REPLACE_MENSAJE } from 'src/app/shared/var.constant';

@Component({
  selector: 'app-mensaje',
  templateUrl: './mensaje.component.html',
  styleUrls: ['./mensaje.component.scss']
})
export class MensajeComponent implements OnInit {

  tiempoMensaje: number = 3000;
  objMensaje?: MensajeModel ;

  replace_mensaje:any;
  routerTo:any;

  key!:string
  
  constructor(private route: ActivatedRoute, private router: Router, private encuestaService:EncuestaService,
    private mensajeService: MensajeService, private memoriaService: MemoriaService) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false; //RELOAD ON CHANGE PARAMS
    this.key = this.route.snapshot.paramMap.get('key') || '';
    this.replace_mensaje= this.route.snapshot.queryParamMap.get('replace_mensaje');
    this.routerTo= this.route.snapshot.queryParamMap.get('routerTo');


    this.objMensaje =arrayMensaje.find((element: MensajeModel) => {
      return element.key == this.key;
    });

    if (this.objMensaje) {
      this.objMensaje.mensaje = this.objMensaje.mensaje.replace(REPLACE_MENSAJE,this.replace_mensaje);
    }   

  }

  ngOnInit(): void {
    setTimeout(() => {

      switch (this.key) {
        case 'despedida-success':
          this.router.navigate(['pregunta-final']);

          /**/
          break;
        case 'bienvenido':
            let idTipo = this.memoriaService.getPersona().tipo.idTipo
            if(idTipo==8){
              this.mensajeService.goToIngreseEspecialidad()
            }else{
              this.mensajeService.goToIngreseNumero()
            }
            break;
        default:
          this.router.navigate([this.routerTo]).then(() => {
            window.location.reload();
          });
          break;
      }
  
    }, this.tiempoMensaje);
  }

}
