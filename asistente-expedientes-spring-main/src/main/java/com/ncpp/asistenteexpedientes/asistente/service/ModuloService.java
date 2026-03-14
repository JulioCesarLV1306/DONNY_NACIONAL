package com.ncpp.asistenteexpedientes.asistente.service;

import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.entity.Modulo;

/**
 * Servicio de Gestión de Módulos (seg_modulo)
 *
 * Proporciona operaciones CRUD para los módulos físicos del sistema.
 */
public interface ModuloService {

    /**
     * Lista todos los módulos registrados
     *
     * @return Lista de módulos
     */
    public List<Modulo> findAll();

    /**
     * Busca un módulo por su ID
     *
     * @param nIdModulo ID del módulo
     * @return Módulo encontrado o null
     */
    public Modulo findById(Long nIdModulo);

    /**
     * Busca un módulo por su IP
     *
     * @param cPcIp Dirección IP del módulo
     * @return Módulo encontrado o null
     */
    public Modulo findByIp(String cPcIp);

    /**
     * Crea un nuevo módulo
     *
     * @param modulo Datos del módulo
     * @return Módulo creado
     */
    public Modulo create(Modulo modulo);

    /**
     * Actualiza un módulo existente
     *
     * @param nIdModulo ID del módulo
     * @param modulo Datos actualizados
     * @return Módulo actualizado o null si no existe
     */
    public Modulo update(Long nIdModulo, Modulo modulo);

    /**
     * Elimina un módulo por ID
     *
     * @param nIdModulo ID del módulo
     * @return true si se eliminó, false si no existe
     */
    public boolean delete(Long nIdModulo);
}
