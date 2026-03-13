import axios from 'axios';
import { CreateUserPayload, UserResponse } from '../types/user';

const authUrl =
  process.env.REACT_APP_DONNYBACK_API_URL ||
  process.env.DONNYBACK_API_URL ||
  'http://localhost:8080/apiv1/auth/login';

const apiBaseUrl = authUrl.replace(/\/auth\/login\/?$/, '');

const usuarioClient = axios.create({
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

  return 'Error inesperado en operación de usuario.';
}

export const usuarioService = {
  async listar(): Promise<UserResponse[]> {
    try {
      const response = await usuarioClient.get<UserResponse[]>(`${apiBaseUrl}/usuario/listar`);
      return response.data;
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  },

  async crear(payload: CreateUserPayload): Promise<UserResponse> {
    try {
      const response = await usuarioClient.post<UserResponse>(`${apiBaseUrl}/usuario/crear`, payload);
      return response.data;
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  },

  async actualizar(id: number, payload: CreateUserPayload): Promise<UserResponse> {
    try {
      const response = await usuarioClient.put<UserResponse>(`${apiBaseUrl}/usuario/actualizar/${id}`, payload);
      return response.data;
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  },

  async buscarPorDni(dni: string): Promise<UserResponse> {
    try {
      const response = await usuarioClient.get<UserResponse>(`${apiBaseUrl}/usuario/buscar`, {
        params: { dni },
      });
      return response.data;
    } catch (error) {
      throw new Error(getErrorMessage(error));
    }
  },
};