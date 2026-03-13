import axios from 'axios';
import { CreateModuloPayload, Modulo } from '../types/modulo';

const authUrl =
  process.env.REACT_APP_DONNYBACK_API_URL ||
  process.env.DONNYBACK_API_URL ||
  'http://localhost:8080/apiv1/auth/login';

const apiBaseUrl = authUrl.replace(/\/auth\/login\/?$/, '');

const moduloClient = axios.create({
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

function getErrorMessage(error: unknown) {
  if (axios.isAxiosError(error)) {
    const backendMessage = (error.response?.data as { mensaje?: string } | undefined)?.mensaje;
    return backendMessage || `Error HTTP ${error.response?.status ?? 'desconocido'}`;
  }

  return 'Error inesperado en operación de módulo.';
}

interface RawModuloResponse {
  nIdModulo?: number;
  cPcIp?: string;
  cPcUsuario?: string;
  cPcClave?: string;
  xDescripcion?: string;
  cUbicacion?: string;
  nEstado?: number;
  fAud?: string;
  bAud?: string;
  cAudUid?: string | null;
  nidModulo?: number;
  cpcIp?: string;
  cpcUsuario?: string;
  cpcClave?: string;
  xdescripcion?: string;
  cubicacion?: string;
  nestado?: number;
  faud?: string;
  baud?: string;
  caudUid?: string | null;
}

function normalizeModulo(raw: RawModuloResponse): Modulo {
  return {
    nIdModulo: raw.nIdModulo ?? raw.nidModulo ?? 0,
    cPcIp: raw.cPcIp ?? raw.cpcIp ?? '',
    cPcUsuario: raw.cPcUsuario ?? raw.cpcUsuario ?? '',
    cPcClave: raw.cPcClave ?? raw.cpcClave ?? '',
    xDescripcion: raw.xDescripcion ?? raw.xdescripcion ?? '',
    cUbicacion: raw.cUbicacion ?? raw.cubicacion ?? '',
    nEstado: raw.nEstado ?? raw.nestado ?? 0,
    fAud: raw.fAud ?? raw.faud ?? '',
    bAud: raw.bAud ?? raw.baud ?? '',
    cAudUid: raw.cAudUid ?? raw.caudUid ?? null,
  };
}

export const moduloService = {
  async listar(): Promise<Modulo[]> {
    try {
      const response = await moduloClient.get<RawModuloResponse[]>(`${apiBaseUrl}/modulo/listar`);
      return response.data.map(normalizeModulo);
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  },

  async crear(payload: CreateModuloPayload): Promise<Modulo> {
    try {
      const response = await moduloClient.post<RawModuloResponse>(`${apiBaseUrl}/modulo/crear`, payload);
      return normalizeModulo(response.data);
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  },

  async actualizar(id: number, payload: CreateModuloPayload): Promise<Modulo> {
    try {
      const response = await moduloClient.put<RawModuloResponse>(`${apiBaseUrl}/modulo/actualizar/${id}`, payload);
      return normalizeModulo(response.data);
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  },
};