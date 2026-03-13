import { User } from './auth';

export interface CreateUserPayload {
  n_id_tipo: number;
  c_dni: string;
  x_ape_paterno: string;
  x_ape_materno: string;
  x_nombres: string;
  c_telefono: string;
  x_correo: string;
  l_activo: 'S' | 'N';
}

export type UserResponse = User;