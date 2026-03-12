import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { EleccionModel } from 'src/app/dto/eleccion-model';
import { WordModel } from 'src/app/dto/word-model';
import { Expediente } from 'src/app/models/expediente';
import { Modulo } from 'src/app/models/modulo';
import { Persona } from 'src/app/models/persona';
import { AnnyangService, eleccionPreguntaFinalCommand } from 'src/app/services/annyang.service';
import { EncuestaService } from 'src/app/services/encuesta.service';
import { InactividadService } from 'src/app/services/inactividad.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';
import { VideosService } from 'src/app/services/videos.service';
import { arrayPreguntaFinal } from 'src/app/shared/var.constant';

@Component({
  selector: 'app-pregunta-final',
  templateUrl: './pregunta-final.component.html',
  styleUrls: ['./pregunta-final.component.scss']
})
export class PreguntaFinalComponent implements OnInit, AfterViewInit, OnDestroy {

  listaOpciones: EleccionModel[] = [];
  opcionSelected!: EleccionModel;

  expedienteActual!: Expediente;
  subscription: Subscription;

  timeoutReload: any;
  timeoutRecord: any;

  tiempoEspera = 500;


  @ViewChild('video_pregunta_final') myVideo!: ElementRef;

  constructor(private memoriaService: MemoriaService, private router: Router, private mensajeService: MensajeService,
    private inactividadService: InactividadService, private encuestaService: EncuestaService,
    private annyangService: AnnyangService, private videosService: VideosService) {
    this.subscription = new Subscription;

  }

  ngOnInit(): void {
    this.annyangService.agregarComandos([eleccionPreguntaFinalCommand]);
    this.listaOpciones = arrayPreguntaFinal;
    this.expedienteActual = this.memoriaService.getExpediente();
    this.inactividadService.iniciarSupervision();
    this.actualizarHTML();
    this.subscribeWordDetected();
  }


  ngAfterViewInit(): void {
    this.videosService.playVideo(this.myVideo);
    this.timeoutReload = this.videosService.iniciarContadorReload('pregunta-final');
    this.timeoutRecord = this.annyangService.startToRecord('pregunta-final');
  }

  actualizarHTML() {
    setInterval(() => {
    }, 500);
  }

  subscribeWordDetected() {
    console.log('PREGUNTA-FINAL SUSCRIBE')
    this.subscription = this.annyangService.wordDetected.subscribe((word: WordModel) => {
      if (word) {
        switch (word.command) {
          case 'eleccionPreguntaFinalCommand':
            this.seleccionarPreguntaFinal(word.value);
            break;
        }
      }
    })
  }


  seleccionarPreguntaFinal(valor: string) {
    const index: number = this.getIndexEleccion(valor);
    if (index > 0 && index <= this.listaOpciones.length) {
      this.opcionSelected = this.listaOpciones[index - 1];

      const persona: Persona = this.memoriaService.getPersona();
      const modulo: Modulo = this.memoriaService.getModulo();
      const numero = this.memoriaService.getNumero();
      const anio = this.memoriaService.getAnio();
      const especialidad = this.memoriaService.getEspecialidad();

      switch (this.opcionSelected.key) {
        case 'continuar':
          setTimeout(() => {
            this.memoriaService.cleanRangoFechas();
            this.router.navigateByUrl('elegir-archivos');
          }, this.tiempoEspera);
          break;
        case 'otro-cuaderno':
          this.memoriaService.limpiarMemoria();
          this.memoriaService.guardarPersona(persona);
          this.memoriaService.guardarModulo(modulo);
          this.memoriaService.setNumero(numero);
          this.memoriaService.setAnio(anio);
          this.memoriaService.setEspecialidad(especialidad || '');

          setTimeout(() => {
            this.router.navigate(['lista-expedientes/0']);
          }, this.tiempoEspera);
          break;
        case 'reiniciar':
          this.memoriaService.limpiarMemoria();
          this.memoriaService.guardarPersona(persona);
          this.memoriaService.guardarModulo(modulo);
          setTimeout(() => {
            if (persona.tipo.idTipo == 8) {
              this.mensajeService.goToIngreseEspecialidad(); 0
            } else {
              this.mensajeService.goToIngreseNumero();
            }

          }, this.tiempoEspera);
          break;
        case 'salir':
          setTimeout(() => {
            let dni = persona.dni;
            this.encuestaService.buscar(dni).subscribe(data => {
              this.mensajeService.goToDespedidaFinal();
            }, err => {
              if (err.status == 404) {
                this.mensajeService.goToEncuesta();
              } else {
                this.mensajeService.goToDespedidaFinal();
              }
            })
          }, this.tiempoEspera);
          break;
        default:
          this.mensajeService.goToDespedidaFinal();
          break;
      }
      //this.memoriaService.setEspecialidad(this.opcionSelected.key);
    }
  }

  getIndexEleccion(key: string): number {
    let index = -1;
    for (let i = 0; i < this.listaOpciones.length; i++) {
      if (this.listaOpciones[i].titulo.toLowerCase() == key) {
        index = i;
        break;
      }
    }
    return index + 1;
  }

  ngOnDestroy(): void {
    console.log('PREGUNTA-FINAL UNSUSCRIBE')
    this.subscription.unsubscribe();
    clearTimeout(this.timeoutRecord);
    clearTimeout(this.timeoutReload);
    this.annyangService.pauseRecord();
  }


}
