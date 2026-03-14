package com.ncpp.asistenteexpedientes.asistente.service;

import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;

/**
 * Servicio de Gestión de Usuarios
 * 
 * Proporciona operaciones CRUD y utilitarias sobre la tabla seg_usuario.
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
public interface UsuarioService {

    /**
     * Lista todos los usuarios registrados
     * 
     * @return Lista de usuarios
     */
    public List<Usuario> findAll();

    /**
     * Busca un usuario por su ID
     * 
     * @param nIdUsuario ID del usuario
     * @return Usuario encontrado o null
     */
    public Usuario findById(Long nIdUsuario);
    
    /**
     * Busca un usuario por su DNI
     * 
     * @param dni DNI del usuario
     * @return Usuario encontrado o null
     */
    public Usuario findByDni(String dni);

    /**
     * Autentica un usuario con correo y DNI (clave)
     * 
     * @param xCorreo Correo registrado
     * @param cDni DNI del usuario (usado como clave)
     * @return Usuario autenticado o null
     */
    public Usuario authenticateByCorreoAndDni(String xCorreo, String cDni);
    
    /**
     * Crea un usuario si no existe en la base de datos.
     * Si existe, retorna el usuario existente.
     * 
     * @param dni DNI del usuario
     * @param nombreCompleto Nombre completo en formato "APELLIDO_PATERNO APELLIDO_MATERNO NOMBRES"
     * @return Usuario creado o existente con su ID asignado
     */
    public Usuario createIfNotExists(String dni, String nombreCompleto);
    
    /**
     * Crea un nuevo usuario en la base de datos
     * 
     * @param usuario Objeto usuario con los datos a insertar
     * @return Usuario creado con ID asignado
     */
    public Usuario create(Usuario usuario);

    /**
     * Actualiza un usuario existente
     * 
     * @param nIdUsuario ID del usuario a actualizar
     * @param usuario Datos a actualizar
     * @return Usuario actualizado o null si no existe
     */
    public Usuario update(Long nIdUsuario, Usuario usuario);

    /**
     * Elimina un usuario por ID
     * 
     * @param nIdUsuario ID del usuario
     * @return true si fue eliminado, false si no existe
     */
    public boolean delete(Long nIdUsuario);
}
