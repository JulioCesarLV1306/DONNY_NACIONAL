package com.ncpp.asistenteexpedientes.sece.service;

import com.ncpp.asistenteexpedientes.sece.entity.Persona;
import com.ncpp.asistenteexpedientes.sece.repository.PersonaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaServiceImpl implements PersonaService{

    @Autowired
    PersonaRepository personaRepository;

    @Override
    public Persona buscarPorDni(String dni) {
      return personaRepository.findByDni(dni);
    }
}
