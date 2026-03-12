import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario';
import { HOST_BACKEND } from '../shared/var.constant';

/**
 * Servicio para gestionar usuarios del sistema (seg_usuario)
 * Proporciona operaciones CRUD y búsqueda de usuarios
 */
@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private url = `${HOST_BACKEND}/usuario`;

  constructor(private http: HttpClient) { }

  /**
   * Busca un usuario por DNI
   * @param cDni DNI del usuario a buscar
   * @returns Observable con el usuario encontrado
   */
  buscarPorDni(cDni: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.url}/buscar`, { params: { cDni } });
  }

  /**
   * Crea un nuevo usuario si no existe
   * Si ya existe, retorna el usuario existente
   * @param cDni DNI del usuario
   * @param xApePaterno Apellido paterno
   * @param xApeMaterno Apellido materno
   * @param xNombres Nombres
   * @returns Observable con el usuario creado o existente
   */
  crearSiNoExiste(cDni: string, xApePaterno: string, xApeMaterno: string, xNombres: string): Observable<Usuario> {
    const usuario: Partial<Usuario> = {
      cDni,
      xApePaterno,
      xApeMaterno,
      xNombres,
      nIdTipo: 1, // Tipo por defecto (usuario ciudadano)
      lActivo: 'S'
    };
    return this.http.post<Usuario>(`${this.url}/crear`, usuario);
  }

  /**
   * Crea un nuevo usuario
   * @param usuario Usuario a crear
   * @returns Observable con el usuario creado
   */
  crear(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.url}/crear`, usuario);
  }

  /**
   * Actualiza un usuario existente
   * @param usuario Usuario con los datos actualizados
   * @returns Observable con el usuario actualizado
   */
  actualizar(usuario: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.url}/actualizar`, usuario);
  }

  /**
   * Lista todos los usuarios activos
   * @returns Observable con array de usuarios
   */
  listarActivos(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.url}/listar`);
  }
}
