import axios from 'axios';
import { Estadistica } from '../types/estadistica';

const authUrl =
  process.env.REACT_APP_DONNYBACK_API_URL ||
  process.env.DONNYBACK_API_URL ||
  'http://localhost:8080/apiv1/auth/login';

const apiBaseUrl = authUrl.replace(/\/auth\/login\/?$/, '');

const estadisticaClient = axios.create({
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

  return 'Error inesperado en operación de estadísticas.';
}

export const estadisticaService = {
  async listarHoy(): Promise<Estadistica[]> {
    try {
      const response = await estadisticaClient.get<Estadistica[]>(`${apiBaseUrl}/estadistica/hoy`);
      return response.data;
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  },

  async listarRango(fechaInicio: string, fechaFin: string): Promise<Estadistica[]> {
    try {
      const response = await estadisticaClient.get<Estadistica[]>(`${apiBaseUrl}/estadistica/rango`, {
        params: { fechaInicio, fechaFin },
      });
      return response.data;
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  },
};
