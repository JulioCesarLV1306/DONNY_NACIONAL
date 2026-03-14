import { Tipo } from "./tipo";

export interface Persona{
    nIdUsuario?: number;
    idPersona:number;
    correo:string;
    dni:string;
    estado: number;
    nombre: string;
    numero: string;
    tipo: Tipo;
}