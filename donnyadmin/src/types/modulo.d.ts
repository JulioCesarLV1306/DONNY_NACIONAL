export interface CreateModuloPayload {
  cPcIp: string;
  cPcUsuario: string;
  cPcClave: string;
  xDescripcion: string;
  cUbicacion: string;
  nEstado: 0 | 1;
}

export interface Modulo {
  nIdModulo: number;
  cPcIp: string;
  cPcUsuario: string;
  cPcClave: string;
  xDescripcion: string;
  cUbicacion: string;
  nEstado: number;
  fAud: string;
  bAud: string;
  cAudUid: string | null;
}