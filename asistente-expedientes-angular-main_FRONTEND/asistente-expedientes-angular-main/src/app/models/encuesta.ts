/**
 * Modelo Encuesta - Interfaz TypeScript para met_encuesta
 * 
 * Versión refactorizada 2.0
 * CAMBIO PRINCIPAL: Ahora usa nIdUsuario (FK a seg_usuario) en lugar de dniSece/nombreSece
 * 
 * @author JC
 * @version 2.0
 */
export interface Encuesta {
    nIdEncuesta?: number;     // n_ = Numérico (ID, opcional en creación)
    nIdModulo: number;        // n_ = Numérico (FK a seg_modulo)
    nIdUsuario: number;       // n_ = Numérico (FK a seg_usuario) - NUEVO
    nCalificacion: number;    // n_ = Numérico (1-5)
    fFechaHora?: Date;        // f_ = Fecha (timestamp)
    fAud?: Date;              // f_ = Fecha (auditoría)
    bAud?: string;            // b_ = Bandera
    
    // DEPRECATED: Solo para compatibilidad temporal con backend antiguo
    /** @deprecated Use nIdModulo instead */
    idModulo?: number;
    /** @deprecated Use nCalificacion instead */
    calificacion?: number;
    /** @deprecated Use nIdUsuario instead */
    dniSece?: string;
    /** @deprecated Use nIdUsuario instead */
    nombreSece?: string;
}