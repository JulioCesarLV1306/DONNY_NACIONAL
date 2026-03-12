import { ServidorFtp } from "./servidor-ftp";

export interface Video{
    nombreArchivo: string,
    formatoExpediente: string,
    naudiencia: string
    servidorFtp: ServidorFtp
}