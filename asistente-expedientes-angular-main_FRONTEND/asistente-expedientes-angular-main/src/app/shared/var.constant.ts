import { EleccionModel } from "../dto/eleccion-model";
import { MensajeModel } from "../dto/mensaje-model";
import { ToRecordModel } from "../dto/torecord-model";

export const HOST_BACKEND = 'http://localhost:8080/apiv1';//CAMBIAR IP DE SERVIDOR
export const VERSION_APP='4.0'
export const REPLACE_MENSAJE = '<{replace_mensaje}>'

export const arrayMensaje:MensajeModel[] = [
    {
      key: 'dni',
      mensaje: `EL DNI <b>${REPLACE_MENSAJE}</b> NO<br>TIENE ACCESO AL SISTEMA`,
      tipoMensaje:'error'
    },
    {
      key: 'bienvenido',
      mensaje: `BIENVENIDO <br><b>${REPLACE_MENSAJE}</b>`,
      tipoMensaje:'success'
    },
    {
      key: 'valor-incorrecto',
      mensaje: `EL DATO <b>${REPLACE_MENSAJE}</b><br>ES <b>INCORRECTO</b>`,
      tipoMensaje:'error'
    },
    {
      key: 'no-seleccion',
      mensaje: `TIENE QUE <b>SELECCIONAR</b> AL MENOS UN <b>${REPLACE_MENSAJE}</b>`,
      tipoMensaje:'error'
    },
    {
      key: 'usb-detectado',
      mensaje: `EL DISPOSITIVO <b>${REPLACE_MENSAJE}</b><br>HA SIDO DETECTADO`,
      tipoMensaje:'success'
    },
    {
      key: 'error-server',
      mensaje: `HA OCURRIDO UN ERROR <b>${REPLACE_MENSAJE}</b><br>EN LA APLICACIÓN`,
      tipoMensaje:'error'
    },
    {
      key: 'no-datos',
      mensaje: `UPS, <b>NO</b> SE HAN <b>ENCONTRADO</b> DATOS`,
      tipoMensaje:'error'
    },
    {
      key: 'no-cliente',
      mensaje: `<b>NO</b> SE HA <b>DETECTADO</b><br>EL APLICATIVO <b>CLIENTE</b>`,
      tipoMensaje:'error'
    },
    {
      key: 'despedida-error',
      mensaje: `UPS, <b>NO</b> SE HAN <b>COPIADO</b> TUS ARCHIVOS`,
      tipoMensaje:'error'
    },
    {
      key: 'despedida-success',
      mensaje: `LA TAREA HA FINALIZADO<br>${REPLACE_MENSAJE}`, //ACTAS [10/10] VIDEOS [10/10]
      tipoMensaje:'success'
    },
    {
      key: 'despedida-final',
      mensaje: `<b>GRACIAS</b> POR USAR EL<br>ASISTENTE DONNY`,
      tipoMensaje:'success'
    },
    {
      key: 'inactividad',
      mensaje: `SE HA <b>TERMINADO</b> EL TIEMPO<br>DE LA <b>SESIÓN</b>`,
      tipoMensaje:'error'
    }
  ] ;

  export const arrayPreguntaFinal:EleccionModel[] = [
    {
      key: 'continuar',
      titulo: 'CONTINUAR',
      icono:'assets/img/ELECCION-CONTINUAR.png',
      grupo_key:'Con el mismo expediente.',
      conteo:0,
      checked: false,
      active:false
    },
    {
      key: 'reiniciar',
      titulo: 'REINICIAR',
      icono:'assets/img/ELECCION-REINICIAR.png',
      grupo_key:'En otro expediente.',
      conteo:0,
      checked: false,
      active:false
    },
    {
      key: 'otro-cuaderno',
      titulo: 'OTRO CUADERNO',
      icono:'assets/img/ELECCION-OTRO-CUADERNO.png',
      grupo_key:'Busca en otro cuaderno.',
      conteo:0,
      checked: false,
      active:false
    },
    {
      key: 'salir',
      titulo: 'SALIR',
      icono:'assets/img/ELECCION-SALIR.png',
      grupo_key:'Finaliza la atención.',
      conteo:0,
      checked: false,
      active:false
    }


  ] ;


  export const arrayEleccion:EleccionModel[] = [
    {
      key: 'videos',
      titulo: 'VIDEOS',
      icono:'assets/img/ELECCION-VIDEOS.png',
      grupo_key:'g1',
      conteo:0,
      checked: false,
      active:false
    },
    {
      key: 'actas',
      titulo: 'ACTAS',
      icono:'assets/img/ELECCION-ACTAS.png',
      grupo_key:'g1',
      conteo:0,
      checked: false,
      active:false
    },
    {
      key: 'resoluciones',
      titulo: 'RESOLUCIONES',
      icono:'assets/img/ELECCION-RESOLUCIONES.png',
      grupo_key:'g2',
      conteo:0,
      checked: false,
      active:false
    },
    {
      key: 'documentosdigitalizados',
      titulo: 'DOCUMENTOS DIGITALIZADOS',
      icono:'assets/img/ELECCION-DOCUMENTOS.png',
      grupo_key:'g3',
      conteo:0,
      checked: false,
      active:false
    },
    {
      key: 'depositos',
      titulo: 'DEPÓSITOS',
      icono:'assets/img/ELECCION-DEPOSITOS.png',
      grupo_key:'g4',
      conteo:0,
      checked: false,
      active:false
    }

  ] ;

  export const arrayEspecialidad:EleccionModel[] = [
    {
      key: 'PE',
      titulo: 'PENAL',
      icono:'assets/img/ESPECIALIDAD-PENAL.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    },
    {
      key: 'LA',
      titulo: 'LABORAL',
      icono:'assets/img/ESPECIALIDAD-LABORAL.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    },
    {
      key: 'CI',
      titulo: 'CIVIL',
      icono:'assets/img/ESPECIALIDAD-CIVIL.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    },
    {
      key: 'FA',
      titulo: 'FAMILIA',
      icono:'assets/img/ESPECIALIDAD-FAMILIA.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    },
    {
      key: 'ED',
      titulo: 'EXTINCIÓN',
      icono:'assets/img/ESPECIALIDAD-EXTINCION.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    },
    {
      key: 'TODOS',
      titulo: 'TODOS',
      icono:'assets/img/ESPECIALIDAD-TODOS.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    }

  ] ;

  
  export const arraySatisfaccion:EleccionModel[] = [
    {
      key: '1',
      titulo: 'PÉSIMA',
      icono:'assets/img/PESIMA.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    },
    {
      key: '2',
      titulo: 'MALA',
      icono:'assets/img/MALA.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    },
    {
      key: '3',
      titulo: 'NEUTRAL',
      icono:'assets/img/NEUTRAL.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    },
    {
      key: '4',
      titulo: 'BUENA',
      icono:'assets/img/BUENA.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    },
    {
      key: '5',
      titulo: 'EXCELENTE',
      icono:'assets/img/EXCELENTE.png',
      grupo_key:'',
      conteo:0,
      checked: false,
      active:true
    }

  ] ;



  export const arrayVoiceRecordStart: ToRecordModel[] = [
    {
      key: "numero",
      timeToRecord:4000,
      durationVideo: 90000
    },
    {
      key: "anio",
      timeToRecord:3000,
      durationVideo: 90000
    },
    {
      key: "cuaderno",
      timeToRecord: 3000,
      durationVideo: 90000
    },
    {
      key: "pregunta-cuaderno",
      timeToRecord: 4000,
      durationVideo: 90000
    },
    {
      key: "lista-expedientes",
      timeToRecord: 4500,
      durationVideo: 95000
    },
    {
      key: "elegir-archivos",
      timeToRecord: 3500,
      durationVideo: 90000
    },
    {
      key: "elegir-especialidad",
      timeToRecord: 3500,
      durationVideo: 90000
    },
    {
      key: "rango-descarga",
      timeToRecord: 5000,
      durationVideo: 122000
    },
    {
      key: "encuesta",
      timeToRecord:4000,
      durationVideo:86000
    },
    {
      key: "despedida-final",
      timeToRecord:0,
      durationVideo:5000
    },
    {
      key: "pregunta-final",
      timeToRecord: 3500,
      durationVideo: 90000
    }
    
  ]

  export const arrayRouterTo = [
    {
      key: "numero",
      routerTo: "ingrese-expediente/anio"
    },{
      key: "anio",
      routerTo: "pregunta-cuaderno"
    },{
      key: "cuaderno",
      routerTo: "elegir-especialidad"
    }
  ]

  