import { Component, OnInit } from '@angular/core';
import { EleccionModel } from 'src/app/dto/eleccion-model';
import { TamanioDescargaRequest } from 'src/app/dto/tamanio-descarga-request';
import { Expediente } from 'src/app/models/expediente';
import { Modulo } from 'src/app/models/modulo';
import { Persona } from 'src/app/models/persona';
import { DownloaderService } from 'src/app/services/downloader.service';
import { DriveService } from 'src/app/services/drive.service';
import { MemoriaService } from 'src/app/services/memoria.service';
import { MensajeService } from 'src/app/services/mensaje.service';

@Component({
  selector: 'app-descarga-archivos',
  templateUrl: './descarga-archivos.component.html',
  styleUrls: ['./descarga-archivos.component.scss']
})
export class DescargaArchivosComponent implements OnInit {

  modeProgressBar: string = 'determinate';

  mensaje: string = '';

  expedienteActual!: Expediente ;
  listaFechas: string[] = [];
  listaEleccion: EleccionModel[] = [];

  tamaniosDescarga: TamanioDescargaRequest[] = [];
  estadosDescarga: string[] = [];

  tiempoEsperaConsulta = 2000;

  modulo!: Modulo;
  persona!: Persona;

  resumenSalida:string='';
  cantidadCarpetasCopy:number=0;

  codeEstado:number=0;

  constructor(private memoriaService: MemoriaService, private downloaderService: DownloaderService,
    private mensajeService: MensajeService, private driveService: DriveService) { }

  ngOnInit(): void {
    this.modoPreparandoDescarga();
    setTimeout(() => {
      
      this.modoCalculandoTamanio();
    }, 1000);

  }

  modoPreparandoDescarga() {
    this.mensaje = 'PREPARANDO LA DESCARGA'
    this.expedienteActual = this.memoriaService.getExpediente();
    this.listaEleccion = this.memoriaService.getEleccionArchivos();
    if(this.listaEleccion[0].key!='depositos'){
      this.listaFechas = this.memoriaService.getRangoFechasFinal().map((item => {
        return item.fecha + '';
      }));
    }
     
    this.modulo = this.memoriaService.getModulo();
    this.persona = this.memoriaService.getPersona();
  }

  modoCalculandoTamanio() {
    this.mensaje = 'CALCULANDO TAMAÑO DE LA DESCARGA'
    console.log(this.listaFechas)
    const keysEleccion = this.listaEleccion.map(item => {
      return item.key;
    })
    this.downloaderService.obtenerTamanios(this.expedienteActual.nunico,this.expedienteActual.nincidente, this.listaFechas, keysEleccion).subscribe((data: any) => {
      if (data) {
        this.tamaniosDescarga = data;
        this.estadosDescarga = Array(this.tamaniosDescarga.length).fill('');

        this.tamaniosDescarga.map(tam => {
          tam.titulo = this.listaEleccion.find(ele => {
            return ele.key == tam.key;
          })?.titulo || ''
        })
        this.codeEstado=1
        this.descargarLista();
      }
    }, (error) => {
      this.mensajeService.goToErrorSistema(error.status,'elegir-archivos');
    })
  }

  descargarLista() {
    if (this.tamaniosDescarga.length > 0) {
      this.descargarEleccion(0);
    }
  }

  copiarEleccion(i: number) {
    if (i < this.tamaniosDescarga.length) {
      this.driveService.verificarUsb().subscribe((drive: any) => {
        if (drive) {
          this.downloaderService.copiar(this.expedienteActual.formatoExpediente,this.expedienteActual.nunico, this.expedienteActual.nincidente, this.listaFechas, this.tamaniosDescarga[i].key, drive.letraUnidad, this.modulo, this.persona).subscribe((copia: any) => {
            if(copia){
              switch(copia.estado){
                case 'copiando':
                  this.mensaje = `COPIANDO CARPETA [1/1]`;
                  this.estadosDescarga[i] = 'descargando';
                  this.bucleComprobarEstadoCopia(i, this.tamaniosDescarga[i].key)
                  break;
                case 'error-copia':
                  this.estadosDescarga[i] = 'error-copia';
                  this.mensaje = `ERROR EN COPIA USB`;
                  this.copiarEleccion(i + 1);
                  break;
                default:
                  this.mensaje = `RECONECTANDO COPIA...`;
                  this.bucleComprobarEstadoCopia(i, this.tamaniosDescarga[i].key)
                  break;
              }
            }
          }, () => {
            this.estadosDescarga[i] = 'error-copia';
            this.mensaje = `ERROR DE CONEXIÓN CON CLIENTE USB`;
            this.copiarEleccion(i + 1);
          })
        }
      }, () => {
        this.mensajeService.goToErrorSistema('CON EL USB','elegir-archivos');
      })
    }else{
      if(this.cantidadCarpetasCopy>0){
        this.mensaje = `COPIADO COMPLETO`;
        this.codeEstado=3;
        setTimeout(() => {
          this.mensajeService.goToDespedidaSuccess(this.resumenSalida);
        }, 1500);
      }else{
        this.mensajeService.goToDespedidaError();
      }
    }
  }

  descargarEleccion(i: number) {
    if (i < this.tamaniosDescarga.length) {
      this.driveService.verificarUsb().subscribe((drive: any) => {

        this.downloaderService.descargar(this.expedienteActual.formatoExpediente, this.expedienteActual.nunico,this.expedienteActual.nincidente, this.listaFechas, this.tamaniosDescarga[i].key, this.tamaniosDescarga[i].tamanio || 0, drive, this.modulo,this.persona).subscribe((descarga: any) => {
          if (descarga) {
            switch(descarga.estado){
              case 'descargando':
                this.mensaje = `DESCARGANDO ${this.tamaniosDescarga[i].titulo}`;
                this.estadosDescarga[i] = 'descargando';
                this.bucleComprobarEstado(i, this.tamaniosDescarga[i].key)
                break;
              case 'falta-espacio':
                this.mensaje = `FALTA ESPACIO PARA ${this.tamaniosDescarga[i].titulo} [${descarga.conteoDescarga + 1}/${descarga.totalDescarga}]`;
                this.estadosDescarga[i] = 'error';
                setTimeout(() => {
                  this.descargarEleccion(i + 1);
                }, this.tiempoEsperaConsulta);
                break;
              default:
                this.mensaje = 'RECONECTANDO...'
                setTimeout(() => {
                  this.bucleComprobarEstado(i,this.tamaniosDescarga[i].key);
                }, this.tiempoEsperaConsulta);
                break;
            }
            
          }
        }, (error) => {
          this.mensajeService.goToErrorSistema(error.status,'elegir-archivos');
        });
      }, () => {
        this.mensajeService.goToErrorSistema('CON EL USB','elegir-archivos');
      })

    } else {
      this.mensaje = `DESCARGAS COMPLETAS`;
      this.codeEstado=2
      setTimeout(() => {
        this.copiarEleccion(0);
      }, 1500);
      
    }
  }

  bucleComprobarEstadoCopia(i:number,keyEleccion:string){
    setTimeout(()=>{
      // Usar propiedades refactorizadas de Modulo
      this.downloaderService.consultarCopia(this.expedienteActual.nunico, this.expedienteActual.nincidente, this.modulo.cPcIp, 
        this.persona.dni, keyEleccion, this.listaFechas).subscribe((consultaCopia: any) => {
          if(consultaCopia){
            switch(consultaCopia.estado){
              case 'copiando':
                this.bucleComprobarEstadoCopia(i, keyEleccion);
                //this.mensaje = `COPIANDO ${this.listaEleccion[i].titulo} [${consulta.archivosDescargados+1}/${consulta.totalArchivos}]`;
                break;
              case 'error-copia':
                this.estadosDescarga[i] = consultaCopia.estado;
                this.copiarEleccion(i + 1);
                break;
              case 'completo-copia':
                this.cantidadCarpetasCopy=this.cantidadCarpetasCopy+1
                this.copiarEleccion(i + 1);
                break;
            }
          }
        })
    },this.tiempoEsperaConsulta)
  }

  bucleComprobarEstado(i: number, keyEleccion: string) {
    setTimeout(() => {
      // Usar propiedades refactorizadas de Modulo
      this.downloaderService.consultar(this.expedienteActual.nunico, this.expedienteActual.nincidente, this.modulo.cPcIp, this.persona.dni,
         keyEleccion, this.listaFechas).subscribe((consulta: any) => {
        if (consulta) {
          console.log(consulta)
          switch (consulta.estado) {
            case 'descargando':
              this.bucleComprobarEstado(i, keyEleccion);
              this.mensaje = `DESCARGANDO ${this.listaEleccion[i].titulo} [${consulta.conteoDescarga+1}/${consulta.totalDescarga}]`;
              break;
            case 'copiando':
              this.bucleComprobarEstado(i, keyEleccion);
              //this.mensaje = `COPIANDO ${this.listaEleccion[i].titulo} [${consulta.conteoDescarga+1}/${consulta.totalDescarga}]`;
              break;
            case 'completo-descarga':
              this.resumenSalida= `${this.resumenSalida} ${keyEleccion.toUpperCase()} [${consulta.conteoDescarga}/${consulta.totalDescarga}] `
              this.estadosDescarga[i] = consulta.estado;
              this.descargarEleccion(i + 1);
              break;
            case 'completo-copia':
              this.cantidadCarpetasCopy=this.cantidadCarpetasCopy+1
              this.copiarEleccion(i + 1);
              break;
            case 'error-descarga':
              this.resumenSalida= `${this.resumenSalida} ${keyEleccion.toUpperCase()} [0/0] `
              this.estadosDescarga[i] = consulta.estado;
              this.descargarEleccion(i + 1);
              break;
            case 'error-copia':
                this.estadosDescarga[i] = consulta.estado;
                this.copiarEleccion(i + 1);
                break;
            default:
                this.bucleComprobarEstado(i, keyEleccion);
                this.mensaje='RECONECTANDO...'
                  break;
          }
        }
      }, (error) => {
        this.mensajeService.goToErrorSistema(error.status,'elegir-archivos');
      });
    }, this.tiempoEsperaConsulta);
  }

}
