package com.ncpp.asistenteexpedientes.asistente.service;

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
     * Busca un usuario por su DNI
     * 
     * @param dni DNI del usuario
     * @return Usuario encontrado o null
     */
    public Usuario findByDni(String dni);
    
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
}
