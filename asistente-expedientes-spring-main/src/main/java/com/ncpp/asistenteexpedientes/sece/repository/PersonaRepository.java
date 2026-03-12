package com.ncpp.asistenteexpedientes.sece.repository;

import com.ncpp.asistenteexpedientes.sece.entity.Persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer>{

    /*
    *1 => Administrador
    *2 => Fiscal Provincial PE
    *3 => Fiscal Adjunto Penal
    4 => Asistente de Fiscal
    *5 => Defensor Publico
    *6 => Procaduria
    *7 => Abogado
    *8 => Parte del Proceso
    9 => Invitado
    10 => Asistente Fiscal
    11 => CEM

    Administrador, Fiscal Provincial, Fiscal Adjunto, Defensores Publicos, Procurador: Todos.
    Abogados Libres: Apersonado en el expediente
    Partes del Proceso: Solo donde ellos aparezcan como imputado o Agraviado
    */

    @Query("SELECT p FROM Persona p JOIN p.tipo t WHERE p.dni = ?1 AND p.estado = 1  AND t.idTipo != 9")
    public Persona findByDni(String dni);
  
}
