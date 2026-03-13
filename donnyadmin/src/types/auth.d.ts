export interface User {
  n_id_usuario: number;
  n_id_tipo: number;
  c_dni: string;
  x_ape_paterno: string;
  x_ape_materno: string;
  x_nombres: string;
  c_telefono: string | null;
  x_correo: string | null;
  l_activo: string;
  f_aud: string;
  b_aud: string;
  c_aud_uid: string;
}

export interface AuthResponse {
  autenticado: boolean;
  mensaje: string;
  usuario: User;
  token?: string;
}

export interface LoginCredentials {
  x_correo: string;
  c_dni: string;
}
