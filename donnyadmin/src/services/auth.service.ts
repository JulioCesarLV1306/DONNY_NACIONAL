import axios from 'axios';
import { AuthResponse, LoginCredentials } from '../types/auth';

const loginUrl =
  process.env.REACT_APP_DONNYBACK_API_URL ||
  process.env.DONNYBACK_API_URL ||
  'http://localhost:8080/apiv1/auth/login';

const authClient = axios.create({
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const authService = {
  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    try {
      const response = await authClient.post<AuthResponse>(loginUrl, credentials);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const backendMessage = (error.response?.data as { mensaje?: string } | undefined)?.mensaje;
        throw new Error(backendMessage || 'No se pudo autenticar con el servidor.');
      }

      throw new Error('Error inesperado al autenticar.');
    }
  },
};
