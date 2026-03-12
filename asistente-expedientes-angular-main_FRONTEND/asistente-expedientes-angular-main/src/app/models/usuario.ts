/**
 * Modelo Usuario - Interfaz TypeScript para seg_usuario
 * 
 * Versión refactorizada 2.0 - Compatible con nuevo esquema PostgreSQL
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
export interface Usuario {
    nIdUsuario: number;       // n_ = Numérico (ID)
    nIdTipo: number;          // FK a seg_tipo_usuario
    cDni: string;             // c_ = Código (DNI único)
    xApePaterno: string;      // x_ = Descripción (apellido paterno)
    xApeMaterno: string;      // x_ = Descripción (apellido materno)
    xNombres: string;         // x_ = Descripción (nombres)
    cTelefono?: string;       // c_ = Código (teléfono)
    xCorreo?: string;         // x_ = Descripción (correo)
    lActivo: string;          // l_ = Lógico ("S" o "N")
    fAud?: Date;              // f_ = Fecha (auditoría automática)
    bAud?: string;            // b_ = Bandera ("I", "U", "D")
    cAudUid?: string;         // c_ = Código (usuario auditor)
}

/**
 * Helper para obtener nombre completo del usuario
 */
export function getNombreCompleto(usuario: Usuario): string {
    return `${usuario.xApePaterno || ''} ${usuario.xApeMaterno || ''} ${usuario.xNombres || ''}`.trim();
}
