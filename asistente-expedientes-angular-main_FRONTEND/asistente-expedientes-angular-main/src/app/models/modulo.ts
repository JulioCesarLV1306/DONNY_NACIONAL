/**
 * Modelo Modulo - Interfaz TypeScript para seg_modulo
 * 
 * Versión refactorizada 2.0
 * 
 * @author JC
 * @version 2.0
 */
export interface Modulo {
    nIdModulo: number;      // n_ = Numérico (ID)
    cPcIp: string;          // c_ = Código (IP del PC)
    cPcUsuario: string;     // c_ = Código (usuario del PC)
    cPcClave: string;       // c_ = Código (clave del PC)
    xDescripcion: string;   // x_ = Descripción
    cUbicacion: string;     // c_ = Código (ubicación)
    nEstado: number;        // n_ = Numérico (0=inactivo, 1=activo)
    fAud?: Date;            // f_ = Fecha (auditoría)
    bAud?: string;          // b_ = Bandera ("I", "U", "D")
    cAudUid?: string;       // c_ = Código (usuario auditor)
}



